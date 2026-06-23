package com.digilayn.laynfleet.core.domain

import org.junit.Assert.assertEquals
import org.junit.Test

class FleetSnapshotTest {
    @Test
    fun tripsForRider_returnsOnlyCurrentRiderTripsForSelectedOperator() {
        val currentUserId = "rider-1"
        val snapshot = FleetSnapshot(
            config = AppConfig("app", "1", "1"),
            userId = currentUserId,
            userName = "Rider",
            profileComplete = true,
            memberships = emptyList(),
            trips = listOf(
                trip("own-selected", "operator-a", currentUserId),
                trip("other-rider", "operator-a", "rider-2"),
                trip("other-operator", "operator-b", currentUserId),
            ),
            unreadNotifications = 0,
        )

        assertEquals(
            listOf("own-selected"),
            snapshot.tripsForRider("operator-a").map(Trip::id),
        )
    }

    private fun trip(id: String, operatorId: String, riderUserId: String) = Trip(
        id = id,
        operatorId = operatorId,
        riderUserId = riderUserId,
        passengerName = "Passenger",
        pickupLocation = "Pickup",
        dropoffLocation = "Drop-off",
        scheduledTime = "08:00",
        driverName = "Driver",
        vehicle = "Vehicle",
        status = TripStatus.SCHEDULED,
    )
}
