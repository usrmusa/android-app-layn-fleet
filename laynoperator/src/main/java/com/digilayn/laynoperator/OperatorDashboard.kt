package com.digilayn.laynoperator

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.digilayn.laynfleet.core.domain.FleetSnapshot
import com.digilayn.laynfleet.core.domain.Membership
import com.digilayn.laynfleet.core.domain.Role
import com.digilayn.laynfleet.core.ui.ActionGrid
import com.digilayn.laynfleet.core.ui.MetricRow
import com.digilayn.laynfleet.core.ui.SectionTitle
import com.digilayn.laynfleet.core.ui.TripList

@Composable
fun OperatorDashboard(snapshot: FleetSnapshot, membership: Membership) {
    if (membership.role == Role.DRIVER) {
        SectionTitle("Your route", "Only assigned riders are visible")
        MetricRow(listOf("2" to "Stops", "06:45" to "Start", "1" to "Vehicle"))
        Spacer(Modifier.height(18.dp))
        TripList(snapshot.trips, showDriver = false)
    } else {
        SectionTitle("Operations overview", "What needs attention today")
        MetricRow(listOf("8" to "Trips", "6" to "Riders", "2" to "Drivers"))
        Spacer(Modifier.height(18.dp))
        ActionGrid(
            listOf(
                "Add rider" to "Invite by email",
                "Add driver" to "Invite and accept",
                "Vehicles" to "3 active",
                "Route groups" to "2 running",
                "Send notice" to "Riders or drivers",
                "Admin partner" to "Same permissions",
            ),
        )
        SectionTitle("Trips today", "${snapshot.trips.size} scheduled")
        TripList(snapshot.trips)
    }
}
