package com.digilayn.laynfleet.core.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.digilayn.laynfleet.core.R
import com.digilayn.laynfleet.core.auth.AuthService
import com.digilayn.laynfleet.core.auth.SignedOutAuthService
import com.digilayn.laynfleet.core.data.DemoFleetRepository
import com.digilayn.laynfleet.core.data.FleetRepository
import com.digilayn.laynfleet.core.domain.*
import com.digilayn.laynfleet.core.ui.theme.LaynFleetTheme

private enum class SharedScreen { SPLASH, WELCOME, LOGIN, PROFILE, FLEETS, HOME }

@Composable
fun LaynFleetFlow(
    product: ProductConfig,
    googleServerClientId: String = "",
    authService: AuthService = SignedOutAuthService,
    repository: FleetRepository = DemoFleetRepository,
    dashboard: @Composable (FleetSnapshot, Membership) -> Unit,
) {
    var screen by remember { mutableStateOf(SharedScreen.SPLASH) }
    var snapshot by remember { mutableStateOf<FleetSnapshot?>(null) }
    var membership by remember { mutableStateOf<Membership?>(null) }

    fun loadAuthenticatedSession() {
        val loaded = repository.loadSnapshot(product)
        snapshot = loaded
        screen = if (loaded.profileComplete) SharedScreen.FLEETS else SharedScreen.PROFILE
    }

    Surface(Modifier.fillMaxSize()) {
        when (screen) {
            SharedScreen.SPLASH -> SplashScreen(product) {
                if (authService.currentUserId() == null) {
                    screen = SharedScreen.WELCOME
                } else {
                    loadAuthenticatedSession()
                }
            }
            SharedScreen.WELCOME -> WelcomeScreen(product) {
                screen = SharedScreen.LOGIN
            }
            SharedScreen.LOGIN -> LoginScreen(
                product = product,
                googleServerClientId = googleServerClientId,
                authService = authService,
            ) {
                loadAuthenticatedSession()
            }
            SharedScreen.PROFILE -> CompleteProfileScreen(product) {
                screen = SharedScreen.FLEETS
            }
            SharedScreen.FLEETS -> snapshot?.let { loaded ->
                MembershipRouter(
                    product = product,
                    snapshot = loaded,
                    onSelect = {
                        membership = it
                        screen = SharedScreen.HOME
                    },
                    onSignOut = {
                        authService.signOut()
                        screen = SharedScreen.LOGIN
                    },
                )
            }
            SharedScreen.HOME -> {
                val loaded = snapshot
                val selected = membership
                if (loaded != null && selected != null) {
                    ScreenColumn {
                        DashboardHeader(
                            snapshot = loaded,
                            membership = selected,
                            onSwitchFleet = { screen = SharedScreen.FLEETS },
                        )
                        Spacer(Modifier.height(22.dp))
                        dashboard(loaded, selected)
                        HorizontalDivider(Modifier.padding(top = 20.dp))
                        OutlinedButton(
                            onClick = {
                                authService.signOut()
                                screen = SharedScreen.LOGIN
                            },
                            modifier = Modifier.fillMaxWidth().padding(top = 18.dp),
                        ) { Text(stringResource(R.string.sign_out)) }
                    }
                }
            }
        }
    }
}

@Composable
private fun CompleteProfileScreen(product: ProductConfig, onComplete: () -> Unit) = ScreenColumn {
    Text(stringResource(R.string.complete_profile), style = MaterialTheme.typography.headlineMedium)
    Text(
        stringResource(R.string.profile_explanation, product.appName),
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier.padding(top = 6.dp, bottom = 18.dp),
    )
    OutlinedTextField(
        "",
        {},
        label = { Text(stringResource(R.string.full_name)) },
        modifier = Modifier.fillMaxWidth(),
    )
    OutlinedTextField(
        "", {}, label = { Text(stringResource(R.string.phone_number)) },
        modifier = Modifier.fillMaxWidth().padding(top = 10.dp),
    )
    Button(
        onClick = onComplete,
        modifier = Modifier.fillMaxWidth().padding(top = 18.dp),
    ) { Text(stringResource(R.string.save_profile)) }
}

@Composable
private fun MembershipRouter(
    product: ProductConfig,
    snapshot: FleetSnapshot,
    onSelect: (Membership) -> Unit,
    onSignOut: () -> Unit,
) = ScreenColumn {
    Text(
        stringResource(R.string.welcome_user, snapshot.userName),
        style = MaterialTheme.typography.headlineMedium,
    )
    Text(
        stringResource(
            if (product.product == Product.RIDER) R.string.choose_rider_fleet
            else R.string.choose_operator_fleet,
        ),
        color = MaterialTheme.colorScheme.onSurfaceVariant,
    )
    Spacer(Modifier.height(20.dp))
    val active = snapshot.memberships.filter {
        it.status == MembershipStatus.ACTIVE && it.role in product.allowedRoles
    }
    if (active.isEmpty()) {
        Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)) {
            Column(Modifier.padding(20.dp)) {
                Text(stringResource(R.string.no_fleet_yet), fontWeight = FontWeight.Bold)
                Text(
                    stringResource(R.string.no_fleet_explanation),
                    modifier = Modifier.padding(top = 6.dp),
                )
            }
        }
    } else {
        active.forEach {
            FleetCard(it) { onSelect(it) }
            Spacer(Modifier.height(12.dp))
        }
    }
    OutlinedButton(onSignOut, Modifier.fillMaxWidth()) {
        Text(stringResource(R.string.sign_out))
    }
}

@Composable
private fun FleetCard(membership: Membership, onClick: () -> Unit) {
    val openFleetDescription = stringResource(R.string.open_fleet)
    Card(
        Modifier.fillMaxWidth().clickable(onClick = onClick),
        shape = RoundedCornerShape(18.dp),
    ) {
        Row(Modifier.padding(18.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(
                Modifier.size(52.dp).background(
                    Color(membership.operator.primaryColor),
                    RoundedCornerShape(16.dp),
                ),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    membership.operator.shortName.take(2),
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                )
            }
            Column(Modifier.weight(1f).padding(horizontal = 14.dp)) {
                Text(membership.operator.name, fontWeight = FontWeight.Bold)
                Text(
                    stringResource(
                        R.string.membership_summary,
                        membership.role.label(),
                        membership.operator.welcomeMessage,
                    ),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
            }
            Text(
                "›",
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.semantics {
                    contentDescription = openFleetDescription
                },
            )
        }
    }
}

@Composable
private fun DashboardHeader(
    snapshot: FleetSnapshot,
    membership: Membership,
    onSwitchFleet: () -> Unit,
) {
    Row(verticalAlignment = Alignment.Top) {
        Column(Modifier.weight(1f)) {
            Text(
                membership.operator.shortName,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold,
            )
            Text(
                membership.operator.name,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
            )
            Text(
                stringResource(
                    R.string.notification_summary,
                    membership.role.label(),
                    snapshot.unreadNotifications,
                ),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        OutlinedButton(onSwitchFleet) { Text(stringResource(R.string.switch_fleet)) }
    }
}

@Composable
fun SectionTitle(title: String, subtitle: String) {
    Text(title, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
    Text(
        subtitle,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier.padding(top = 2.dp, bottom = 12.dp),
    )
}

@Composable
fun MetricRow(metrics: List<Pair<String, String>>) {
    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        metrics.forEach { (value, label) ->
            Card(Modifier.weight(1f)) {
                Column(Modifier.padding(14.dp)) {
                    Text(value, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                    Text(label, style = MaterialTheme.typography.bodySmall)
                }
            }
        }
    }
}

@Composable
fun ActionGrid(actions: List<Pair<String, String>>) {
    actions.chunked(2).forEach { items ->
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            items.forEach { (title, subtitle) ->
                Card(
                    Modifier.weight(1f),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceContainer,
                    ),
                ) {
                    Column(Modifier.padding(15.dp)) {
                        Text(title, fontWeight = FontWeight.Bold)
                        Text(
                            subtitle,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(top = 4.dp),
                        )
                    }
                }
            }
        }
        Spacer(Modifier.height(10.dp))
    }
}

@Composable
fun TripList(trips: List<Trip>, showDriver: Boolean = true) {
    trips.forEach { trip ->
        Card(
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
            ),
            shape = RoundedCornerShape(18.dp),
        ) {
            Column(Modifier.padding(18.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        trip.status.label(),
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.background(
                            MaterialTheme.colorScheme.tertiaryContainer,
                            RoundedCornerShape(50),
                        ).padding(horizontal = 10.dp, vertical = 6.dp),
                    )
                    Spacer(Modifier.weight(1f))
                    Text(trip.scheduledTime, fontWeight = FontWeight.Bold)
                }
                Text(
                    trip.passengerName,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 14.dp),
                )
                Text(
                    stringResource(
                        R.string.trip_route,
                        trip.pickupLocation,
                        trip.dropoffLocation,
                    ),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                if (showDriver) {
                    Text(
                        stringResource(
                            R.string.driver_vehicle,
                            trip.driverName,
                            trip.vehicle,
                        ),
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(top = 10.dp),
                    )
                }
            }
        }
        Spacer(Modifier.height(12.dp))
    }
}

@Composable
fun ScreenColumn(content: @Composable ColumnScope.() -> Unit) {
    Column(
        Modifier.fillMaxSize().statusBarsPadding().verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 22.dp),
        content = content,
    )
}

@Composable
private fun Role.label() = stringResource(
    when (this) {
        Role.SUPER_ADMIN -> R.string.role_super_admin
        Role.ADMIN -> R.string.role_admin
        Role.DRIVER -> R.string.role_driver
        Role.RIDER -> R.string.role_rider
    },
)

@Composable
private fun TripStatus.label() = stringResource(
    when (this) {
        TripStatus.SCHEDULED -> R.string.trip_status_scheduled
        TripStatus.DRIVER_ON_THE_WAY -> R.string.trip_status_driver_on_the_way
        TripStatus.DRIVER_WAITING_OUTSIDE -> R.string.trip_status_driver_waiting_outside
        TripStatus.PASSENGER_PICKED_UP -> R.string.trip_status_passenger_picked_up
        TripStatus.ARRIVED_AT_DESTINATION -> R.string.trip_status_arrived_at_destination
        TripStatus.COMPLETED -> R.string.trip_status_completed
        TripStatus.DELAYED -> R.string.trip_status_delayed
        TripStatus.CANCELLED -> R.string.trip_status_cancelled
        TripStatus.DRIVER_UNAVAILABLE -> R.string.trip_status_driver_unavailable
        TripStatus.VEHICLE_CHANGED -> R.string.trip_status_vehicle_changed
        TripStatus.DRIVER_CHANGED -> R.string.trip_status_driver_changed
    },
)

@Preview(name = "Complete profile", showBackground = true)
@Composable
private fun CompleteProfilePreview() {
    LaynFleetTheme(darkTheme = false) {
        CompleteProfileScreen(Products.Rider) {}
    }
}

@Preview(name = "Fleet selection", showBackground = true)
@Composable
private fun MembershipRouterPreview() {
    val snapshot = DemoFleetRepository.loadSnapshot(Products.Rider)
    LaynFleetTheme(darkTheme = false) {
        MembershipRouter(Products.Rider, snapshot, {}, {})
    }
}
