package com.digilayn.laynrider

import androidx.compose.runtime.Composable
import com.digilayn.laynfleet.core.domain.FleetSnapshot
import com.digilayn.laynfleet.core.ui.ActionGrid
import com.digilayn.laynfleet.core.ui.SectionTitle
import com.digilayn.laynfleet.core.ui.TripList

@Composable
fun RiderDashboard(snapshot: FleetSnapshot) {
    SectionTitle("Today’s trips", "Live status from your fleet team")
    TripList(snapshot.trips)
    SectionTitle("Your fleet space", "Passenger and subscription shortcuts")
    ActionGrid(
        listOf(
            "Passengers" to "1 linked",
            "Request update" to "Change transport details",
            "Notifications" to "Trip and admin messages",
            "Settings" to "Profile, support, privacy",
        ),
    )
}
