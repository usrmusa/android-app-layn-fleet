package com.digilayn.laynfleet.core.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.digilayn.laynfleet.core.data.DemoFleetRepository
import com.digilayn.laynfleet.core.data.FleetRepository
import com.digilayn.laynfleet.core.domain.*

private enum class SharedScreen { LOGIN, PROFILE, FLEETS, HOME }

@Composable
fun LaynFleetFlow(
    product: ProductConfig,
    repository: FleetRepository = DemoFleetRepository,
    dashboard: @Composable (FleetSnapshot, Membership) -> Unit,
) {
    var screen by remember { mutableStateOf(SharedScreen.LOGIN) }
    var snapshot by remember { mutableStateOf<FleetSnapshot?>(null) }
    var membership by remember { mutableStateOf<Membership?>(null) }

    Surface(Modifier.fillMaxSize()) {
        when (screen) {
            SharedScreen.LOGIN -> LoginScreen(product) {
                val loaded = repository.loadSnapshot(product)
                snapshot = loaded
                screen = if (loaded.profileComplete) SharedScreen.FLEETS else SharedScreen.PROFILE
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
                    onSignOut = { screen = SharedScreen.LOGIN },
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
                            onClick = { screen = SharedScreen.LOGIN },
                            modifier = Modifier.fillMaxWidth().padding(top = 18.dp),
                        ) { Text("Sign out") }
                    }
                }
            }
        }
    }
}

@Composable
private fun LoginScreen(product: ProductConfig, onContinue: () -> Unit) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    Column(
        Modifier.fillMaxSize().background(MaterialTheme.colorScheme.primary)
            .statusBarsPadding().padding(24.dp),
        verticalArrangement = Arrangement.SpaceBetween,
    ) {
        Column {
            Spacer(Modifier.height(28.dp))
            BrandMark(product.appName)
            Text(
                if (product.product == Product.RIDER) "Your journey,\nkept close."
                else "Your fleet,\nunder control.",
                style = MaterialTheme.typography.displaySmall,
                color = MaterialTheme.colorScheme.onPrimary,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 30.dp),
            )
            Text(
                "Sign in with your Digilayn identity.",
                color = MaterialTheme.colorScheme.onPrimary.copy(alpha = .78f),
                modifier = Modifier.padding(top = 10.dp),
            )
        }
        Card(shape = RoundedCornerShape(24.dp)) {
            Column(Modifier.padding(20.dp)) {
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                )
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Password") },
                    singleLine = true,
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth().padding(top = 10.dp),
                )
                Button(
                    onClick = onContinue,
                    modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
                ) { Text("Continue") }
                Text(
                    "Demo login · Firebase Auth plugs into the shared core repository.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 10.dp),
                )
            }
        }
    }
}

@Composable
private fun BrandMark(appName: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            Modifier.size(44.dp).background(MaterialTheme.colorScheme.tertiary, CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            Text("L", fontWeight = FontWeight.Black, color = Color(0xFF13251F))
        }
        Text(
            "  $appName",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onPrimary,
            fontWeight = FontWeight.Bold,
        )
    }
}

@Composable
private fun CompleteProfileScreen(product: ProductConfig, onComplete: () -> Unit) = ScreenColumn {
    Text("Complete your profile", style = MaterialTheme.typography.headlineMedium)
    Text(
        "${product.appName} needs a few shared identity details before fleet access.",
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier.padding(top = 6.dp, bottom = 18.dp),
    )
    OutlinedTextField("", {}, label = { Text("Full name") }, modifier = Modifier.fillMaxWidth())
    OutlinedTextField(
        "", {}, label = { Text("Phone number") },
        modifier = Modifier.fillMaxWidth().padding(top = 10.dp),
    )
    Button(
        onClick = onComplete,
        modifier = Modifier.fillMaxWidth().padding(top = 18.dp),
    ) { Text("Save profile") }
}

@Composable
private fun MembershipRouter(
    product: ProductConfig,
    snapshot: FleetSnapshot,
    onSelect: (Membership) -> Unit,
    onSignOut: () -> Unit,
) = ScreenColumn {
    Text("Welcome, ${snapshot.userName}", style = MaterialTheme.typography.headlineMedium)
    Text(
        if (product.product == Product.RIDER) "Choose the fleet you want to view."
        else "Choose where you’re working today.",
        color = MaterialTheme.colorScheme.onSurfaceVariant,
    )
    Spacer(Modifier.height(20.dp))
    val active = snapshot.memberships.filter {
        it.status == MembershipStatus.ACTIVE && it.role in product.allowedRoles
    }
    if (active.isEmpty()) {
        Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)) {
            Column(Modifier.padding(20.dp)) {
                Text("No fleet yet", fontWeight = FontWeight.Bold)
                Text(
                    "Ask your fleet admin to add your registered email. Fleets are not publicly searchable.",
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
    OutlinedButton(onSignOut, Modifier.fillMaxWidth()) { Text("Sign out") }
}

@Composable
private fun FleetCard(membership: Membership, onClick: () -> Unit) {
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
                    "${membership.role.pretty()} · ${membership.operator.welcomeMessage}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
            }
            Text("›", style = MaterialTheme.typography.headlineSmall)
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
                "${membership.role.pretty()} · ${snapshot.unreadNotifications} new notifications",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        OutlinedButton(onSwitchFleet) { Text("Switch") }
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
                        trip.status.pretty(),
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
                    "${trip.pickupLocation}  →  ${trip.dropoffLocation}",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                if (showDriver) {
                    Text(
                        "${trip.driverName} · ${trip.vehicle}",
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

private fun Role.pretty() = name.lowercase().replace('_', ' ')
    .replaceFirstChar { it.uppercase() }
private fun TripStatus.pretty() = name.lowercase().replace('_', ' ')
    .replaceFirstChar { it.uppercase() }
