package com.digilayn.laynrider

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.digilayn.laynfleet.core.data.DemoFleetRepository
import com.digilayn.laynfleet.core.domain.FleetSnapshot
import com.digilayn.laynfleet.core.domain.Products
import com.digilayn.laynfleet.core.ui.ActionGrid
import com.digilayn.laynfleet.core.ui.ScreenColumn
import com.digilayn.laynfleet.core.ui.SectionTitle
import com.digilayn.laynfleet.core.ui.TripList
import com.digilayn.laynfleet.core.ui.theme.LaynFleetTheme

@Composable
fun RiderDashboard(snapshot: FleetSnapshot) {
    SectionTitle(
        stringResource(R.string.todays_trips),
        stringResource(R.string.live_fleet_status),
    )
    TripList(snapshot.trips)
    SectionTitle(
        stringResource(R.string.your_fleet_space),
        stringResource(R.string.passenger_shortcuts),
    )
    ActionGrid(
        listOf(
            stringResource(R.string.passengers) to stringResource(R.string.one_linked),
            stringResource(R.string.request_update) to
                stringResource(R.string.change_transport_details),
            stringResource(R.string.notifications) to
                stringResource(R.string.trip_admin_messages),
            stringResource(R.string.settings) to
                stringResource(R.string.profile_support_privacy),
        ),
    )
}

@Preview(name = "Rider dashboard", showBackground = true, widthDp = 390, heightDp = 844)
@Composable
private fun RiderDashboardPreview() {
    val snapshot = DemoFleetRepository.loadSnapshot(Products.Rider)
    LaynFleetTheme(darkTheme = false) {
        ScreenColumn {
            RiderDashboard(snapshot)
        }
    }
}

@Preview(name = "Rider dashboard · Dark", showBackground = true, widthDp = 390, heightDp = 844)
@Composable
private fun RiderDashboardDarkPreview() {
    val snapshot = DemoFleetRepository.loadSnapshot(Products.Rider)
    LaynFleetTheme(darkTheme = true) {
        ScreenColumn {
            RiderDashboard(snapshot)
        }
    }
}
