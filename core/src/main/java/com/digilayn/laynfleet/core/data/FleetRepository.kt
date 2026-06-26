package com.digilayn.laynfleet.core.data

import com.digilayn.laynfleet.core.domain.*
import com.digilayn.laynfleet.core.util.FlowLogger
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

interface FleetRepository {
    fun loadSnapshot(product: ProductConfig): FleetSnapshot
}

interface RegistrationProfileRepository {
    suspend fun createProfileStubs(userId: String, email: String?, product: ProductConfig): Result<Unit>
}

object GlobalIdentityPaths {
    fun user(userId: String) = "users/$userId"
}

object FirestorePaths {
    const val ROOT = "kasilayn/LaynFleet"
    fun appConfig(appId: String) = "$ROOT/appConfig/$appId"
    fun user(userId: String) = "$ROOT/users/$userId"
    fun operator(operatorId: String) = "$ROOT/operators/$operatorId"
    fun operatorUser(operatorId: String, userId: String) =
        "${operator(operatorId)}/users/$userId"
    fun vehicles(operatorId: String) = "${operator(operatorId)}/vehicles"
    fun passengers(operatorId: String) = "${operator(operatorId)}/passengers"
    fun routeGroups(operatorId: String) = "${operator(operatorId)}/routeGroups"
    fun trips(operatorId: String) = "${operator(operatorId)}/trips"
    fun tripEvents(operatorId: String) = "${operator(operatorId)}/tripEvents"
    fun notifications() = "$ROOT/notifications"
    fun devices() = "$ROOT/devices"
    fun deletionRequests() = "$ROOT/deletionRequests"
}

class FirestoreRegistrationProfileRepository(
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance(),
) : RegistrationProfileRepository {
    override suspend fun createProfileStubs(
        userId: String,
        email: String?,
        product: ProductConfig,
    ): Result<Unit> = runCatching {
        FlowLogger.d("FirestoreRegistrationProfileRepository", "Creating profile stubs for userId: $userId, email: $email")
        val now = FieldValue.serverTimestamp()
        val globalPath = GlobalIdentityPaths.user(userId)
        val fleetPath = FirestorePaths.user(userId)

        val globalProfile = mapOf(
            "userId" to userId,
            "email" to email,
            "createdByAppId" to product.appId,
            "profileComplete" to false,
            "createdAt" to now,
            "updatedAt" to now,
        )
        val fleetProfile = mapOf(
            "userId" to userId,
            "email" to email,
            "createdByAppId" to product.appId,
            "fleetProfileCreated" to true,
            "profileComplete" to false,
            "createdAt" to now,
            "updatedAt" to now,
        )

        FlowLogger.d("FirestoreRegistrationProfileRepository", "Batch write started. Global path: $globalPath, Fleet path: $fleetPath")
        firestore.runBatch { batch ->
            batch.set(firestore.document(globalPath), globalProfile)
            batch.set(firestore.document(fleetPath), fleetProfile)
        }.await()
        FlowLogger.i("FirestoreRegistrationProfileRepository", "Batch write successful for $userId")
    }.onFailure {
        FlowLogger.e("FirestoreRegistrationProfileRepository", "Profile stub creation failed for $userId", it)
    }
}

object InMemoryRegistrationProfileRepository : RegistrationProfileRepository {
    var lastStub: RegistrationProfileStub? = null
        private set

    override suspend fun createProfileStubs(
        userId: String,
        email: String?,
        product: ProductConfig,
    ): Result<Unit> {
        lastStub = RegistrationProfileStub(
            userId = userId,
            email = email,
            createdByAppId = product.appId,
            createdAt = Timestamp.now(),
        )
        return Result.success(Unit)
    }
}

data class RegistrationProfileStub(
    val userId: String,
    val email: String?,
    val createdByAppId: String,
    val createdAt: Timestamp,
)

object DemoFleetRepository : FleetRepository {
    override fun loadSnapshot(product: ProductConfig): FleetSnapshot {
        val currentUserId = "user-lerato"
        val schoolRun = Operator(
            "soweto-school-run", "Soweto School Run", "SSR",
            "Safe trips. Familiar faces.", 0xFF175C4C,
        )
        val cityLink = Operator(
            "city-link", "City Link Transport", "CLT",
            "Moving your day forward.", 0xFF1D4E89,
        )
        val memberships = if (product.product == Product.RIDER) {
            listOf(
                Membership(schoolRun, Role.RIDER, MembershipStatus.ACTIVE),
                Membership(cityLink, Role.RIDER, MembershipStatus.ACTIVE),
            )
        } else {
            listOf(
                Membership(schoolRun, Role.ADMIN, MembershipStatus.ACTIVE),
                Membership(cityLink, Role.DRIVER, MembershipStatus.ACTIVE),
            )
        }
        return FleetSnapshot(
            AppConfig(product.appId, "1.0", "1.0"),
            userId = currentUserId,
            userName = "Lerato",
            profileComplete = true,
            memberships = memberships,
            trips = listOf(
                Trip(
                    "trip-001", schoolRun.id, currentUserId,
                    "Amahle", "Orlando West", "Maponya Academy",
                    "06:45", "Thabo M.", "Toyota Quantum · GP 245-771",
                    TripStatus.DRIVER_ON_THE_WAY,
                ),
                Trip(
                    "trip-002", schoolRun.id, currentUserId,
                    "Amahle", "Maponya Academy", "Orlando West",
                    "15:00", "Thabo M.", "Toyota Quantum · GP 245-771",
                    TripStatus.SCHEDULED,
                ),
                Trip(
                    "trip-003", schoolRun.id, "user-naledi",
                    "Kagiso", "Diepkloof", "Maponya Academy",
                    "07:10", "Thabo M.", "Toyota Quantum · GP 245-771",
                    TripStatus.SCHEDULED,
                ),
                Trip(
                    "trip-004", cityLink.id, currentUserId,
                    "Lerato", "Braamfontein", "Rosebank",
                    "08:30", "Zanele K.", "Honda BR-V · GP 812-440",
                    TripStatus.SCHEDULED,
                ),
            ),
            unreadNotifications = 3,
        )
    }
}
