package com.digilayn.laynoperator

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.digilayn.laynfleet.core.data.DemoFleetRepository
import com.digilayn.laynfleet.core.domain.FleetSnapshot
import com.digilayn.laynfleet.core.domain.Membership
import com.digilayn.laynfleet.core.domain.Products
import com.digilayn.laynfleet.core.domain.Role
import com.digilayn.laynfleet.core.ui.ActionGrid
import com.digilayn.laynfleet.core.ui.MetricRow
import com.digilayn.laynfleet.core.ui.ScreenColumn
import com.digilayn.laynfleet.core.ui.SectionTitle
import com.digilayn.laynfleet.core.ui.TripList
import com.digilayn.laynfleet.core.ui.theme.LaynFleetTheme

@Composable
fun OperatorDashboard(snapshot: FleetSnapshot, membership: Membership) {
    if (membership.role == Role.DRIVER) {
        SectionTitle(
            stringResource(R.string.your_route),
            stringResource(R.string.assigned_riders_only),
        )
        MetricRow(
            listOf(
                "2" to stringResource(R.string.stops),
                "06:45" to stringResource(R.string.start),
                "1" to stringResource(R.string.vehicle),
            ),
        )
        Spacer(Modifier.height(18.dp))
        TripList(snapshot.trips, showDriver = false)
    } else {
        SectionTitle(
            stringResource(R.string.operations_overview),
            stringResource(R.string.attention_today),
        )
        MetricRow(
            listOf(
                "8" to stringResource(R.string.trips),
                "6" to stringResource(R.string.riders),
                "2" to stringResource(R.string.drivers),
            ),
        )
        Spacer(Modifier.height(18.dp))
        ActionGrid(
            listOf(
                stringResource(R.string.add_rider) to stringResource(R.string.invite_by_email),
                stringResource(R.string.add_driver) to stringResource(R.string.invite_and_accept),
                stringResource(R.string.vehicles) to stringResource(R.string.three_active),
                stringResource(R.string.route_groups) to stringResource(R.string.two_running),
                stringResource(R.string.send_notice) to stringResource(R.string.riders_or_drivers),
                stringResource(R.string.admin_partner) to stringResource(R.string.same_permissions),
            ),
        )
        SectionTitle(
            stringResource(R.string.trips_today),
            stringResource(R.string.scheduled_count, snapshot.trips.size),
        )
        TripList(snapshot.trips)
    }
}

@Preview(name = "Admin dashboard", showBackground = true, widthDp = 390, heightDp = 844)
@Composable
private fun AdminDashboardPreview() {
    val snapshot = DemoFleetRepository.loadSnapshot(Products.Operator)
    LaynFleetTheme(darkTheme = false) {
        ScreenColumn {
            OperatorDashboard(snapshot, snapshot.memberships.first { it.role == Role.ADMIN })
        }
    }
}

@Preview(name = "Driver dashboard", showBackground = true, widthDp = 390, heightDp = 844)
@Composable
private fun DriverDashboardPreview() {
    val snapshot = DemoFleetRepository.loadSnapshot(Products.Operator)
    LaynFleetTheme(darkTheme = false) {
        ScreenColumn {
            OperatorDashboard(snapshot, snapshot.memberships.first { it.role == Role.DRIVER })
        }
    }
}
