package com.digilayn.laynfleet.core.domain

enum class Product { RIDER, OPERATOR }
enum class Role { SUPER_ADMIN, ADMIN, DRIVER, RIDER }
enum class MembershipStatus {
    PENDING_ACCEPTANCE, ACTIVE, REJECTED, REVOKED, SUSPENDED, REMOVED
}
enum class TripStatus {
    SCHEDULED, DRIVER_ON_THE_WAY, DRIVER_WAITING_OUTSIDE, PASSENGER_PICKED_UP,
    ARRIVED_AT_DESTINATION, COMPLETED, DELAYED, CANCELLED, DRIVER_UNAVAILABLE,
    VEHICLE_CHANGED, DRIVER_CHANGED
}

data class ProductConfig(
    val product: Product,
    val appId: String,
    val appName: String,
    val allowedRoles: Set<Role>,
)

object Products {
    val Rider = ProductConfig(
        Product.RIDER,
        "com.digilayn.laynrider",
        "LaynRider",
        setOf(Role.RIDER),
    )
    val Operator = ProductConfig(
        Product.OPERATOR,
        "com.digilayn.laynoperator",
        "LaynOperator",
        setOf(Role.ADMIN, Role.DRIVER),
    )
}

data class AppConfig(
    val appId: String,
    val latestVersion: String,
    val minimumSupportedVersion: String,
    val forceUpdate: Boolean = false,
    val maintenanceMode: Boolean = false,
    val maintenanceTitle: String = "Scheduled maintenance",
    val maintenanceMessage: String = "We’ll be back shortly.",
    val supportEmail: String = "support@digilayn.com",
)

data class Operator(
    val id: String,
    val name: String,
    val shortName: String,
    val welcomeMessage: String,
    val primaryColor: Long,
)

data class Membership(
    val operator: Operator,
    val role: Role,
    val status: MembershipStatus,
)

data class Trip(
    val id: String,
    val passengerName: String,
    val pickupLocation: String,
    val dropoffLocation: String,
    val scheduledTime: String,
    val driverName: String,
    val vehicle: String,
    val status: TripStatus,
)

data class FleetSnapshot(
    val config: AppConfig,
    val userName: String,
    val profileComplete: Boolean,
    val memberships: List<Membership>,
    val trips: List<Trip>,
    val unreadNotifications: Int,
)
