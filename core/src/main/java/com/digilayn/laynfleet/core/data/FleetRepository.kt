package com.digilayn.laynfleet.core.data

import com.digilayn.laynfleet.core.domain.*

interface FleetRepository {
    fun loadSnapshot(product: ProductConfig): FleetSnapshot
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

object DemoFleetRepository : FleetRepository {
    override fun loadSnapshot(product: ProductConfig): FleetSnapshot {
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
            userName = "Lerato",
            profileComplete = true,
            memberships = memberships,
            trips = listOf(
                Trip(
                    "trip-001", "Amahle", "Orlando West", "Maponya Academy",
                    "06:45", "Thabo M.", "Toyota Quantum · GP 245-771",
                    TripStatus.DRIVER_ON_THE_WAY,
                ),
                Trip(
                    "trip-002", "Amahle", "Maponya Academy", "Orlando West",
                    "15:00", "Thabo M.", "Toyota Quantum · GP 245-771",
                    TripStatus.SCHEDULED,
                ),
            ),
            unreadNotifications = 3,
        )
    }
}
