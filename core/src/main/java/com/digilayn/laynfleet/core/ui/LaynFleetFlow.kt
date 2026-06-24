package com.digilayn.laynfleet.core.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withLink
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.digilayn.laynfleet.core.R
import com.digilayn.laynfleet.core.data.DemoFleetRepository
import com.digilayn.laynfleet.core.data.FleetRepository
import com.digilayn.laynfleet.core.domain.*
import com.digilayn.laynfleet.core.ui.theme.LaynFleetTheme

private enum class SharedScreen { SPLASH, WELCOME, LOGIN, PROFILE, FLEETS, HOME }

interface AuthSessionResolver {
    fun currentUserId(): String?
}

object SignedOutAuthSessionResolver : AuthSessionResolver {
    override fun currentUserId(): String? = null
}

@Composable
fun LaynFleetFlow(
    product: ProductConfig,
    authSessionResolver: AuthSessionResolver = SignedOutAuthSessionResolver,
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
                if (authSessionResolver.currentUserId() == null) {
                    screen = SharedScreen.WELCOME
                } else {
                    loadAuthenticatedSession()
                }
            }
            SharedScreen.WELCOME -> WelcomeScreen(product) {
                screen = SharedScreen.LOGIN
            }
            SharedScreen.LOGIN -> LoginScreen(product) {
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
                        ) { Text(stringResource(R.string.sign_out)) }
                    }
                }
            }
        }
    }
}

@Composable
private fun SplashScreen(product: ProductConfig, onAuthStateResolved: () -> Unit) {
    LaunchedEffect(product) {
        onAuthStateResolved()
    }

    Box(
        Modifier.fillMaxSize().statusBarsPadding().padding(horizontal = 32.dp),
        contentAlignment = Alignment.Center,
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            BrandMark(product.appName)
            Spacer(Modifier.height(28.dp))
            CircularProgressIndicator(
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(34.dp),
            )
            Text(
                stringResource(R.string.splash_checking_session),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 16.dp),
            )
        }
    }
}

@Composable
private fun WelcomeScreen(product: ProductConfig, onContinue: () -> Unit) {
    var hasAcceptedTerms by rememberSaveable { mutableStateOf(false) }

    Scaffold(Modifier.fillMaxSize()) { paddingValues ->
        Column(
            Modifier.fillMaxSize()
                .padding(paddingValues)
                .consumeWindowInsets(paddingValues)
                .navigationBarsPadding()
                .padding(horizontal = 24.dp, vertical = 28.dp),
        ) {
            Column(
                Modifier.weight(1f).verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                BrandMark(product.appName)
                Text(
                    stringResource(R.string.welcome_title, product.appName),
                    style = MaterialTheme.typography.headlineMedium,
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 32.dp),
                )
                Text(
                    stringResource(
                        if (product.product == Product.RIDER) {
                            R.string.welcome_rider_message
                        } else {
                            R.string.welcome_operator_message
                        },
                    ),
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 12.dp),
                )
            }

            TermsAgreement(
                checked = hasAcceptedTerms,
                onCheckedChange = { hasAcceptedTerms = it },
            )
            Text(
                stringResource(R.string.welcome_terms_required),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(start = 52.dp, top = 2.dp, bottom = 16.dp),
            )
            Button(
                onClick = onContinue,
                enabled = hasAcceptedTerms,
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth().height(50.dp),
            ) {
                Text(stringResource(R.string.continue_label))
            }
        }
    }
}

@Composable
private fun LoginScreen(product: ProductConfig, onContinue: () -> Unit) {
    var isLoginMode by rememberSaveable { mutableStateOf(true) }
    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var confirmPassword by rememberSaveable { mutableStateOf("") }
    var passwordVisible by rememberSaveable { mutableStateOf(false) }
    var confirmPasswordVisible by rememberSaveable { mutableStateOf(false) }

    Scaffold(Modifier.fillMaxSize()) { paddingValues ->
        Column(
            Modifier.fillMaxSize()
                .padding(paddingValues)
                .consumeWindowInsets(paddingValues)
                .imePadding(),
        ) {
            Column(
                Modifier.fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 24.dp, vertical = 28.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                BrandMark(product.appName)

                Text(
                    stringResource(
                        if (isLoginMode) R.string.auth_welcome_back
                        else R.string.auth_create_account,
                    ),
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 32.dp),
                )

                Spacer(Modifier.height(32.dp))

                AuthTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = stringResource(R.string.email),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                )

                Spacer(Modifier.height(10.dp))

                AuthPasswordField(
                    value = password,
                    onValueChange = { password = it },
                    label = stringResource(R.string.password),
                    visible = passwordVisible,
                    onVisibilityChange = { passwordVisible = !passwordVisible },
                )

                if (!isLoginMode) {
                    Spacer(Modifier.height(10.dp))
                    AuthPasswordField(
                        value = confirmPassword,
                        onValueChange = { confirmPassword = it },
                        label = stringResource(R.string.auth_confirm_password),
                        visible = confirmPasswordVisible,
                        onVisibilityChange = {
                            confirmPasswordVisible = !confirmPasswordVisible
                        },
                    )
                }

                Spacer(Modifier.height(24.dp))

                Button(
                    onClick = onContinue,
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                ) {
                    Text(
                        stringResource(
                            if (isLoginMode) R.string.auth_log_in
                            else R.string.auth_sign_up,
                        ),
                    )
                }

                TextButton(
                    onClick = { isLoginMode = !isLoginMode },
                    modifier = Modifier.padding(top = 8.dp),
                ) {
                    Text(
                        stringResource(
                            if (isLoginMode) R.string.auth_switch_to_signup
                            else R.string.auth_switch_to_login,
                        ),
                    )
                }
            }
        }
    }
}

@Composable
private fun BrandMark(appName: String) {
    Image(
        painter = painterResource(R.drawable.laynrider_logo),
        contentDescription = stringResource(R.string.auth_logo_description, appName),
        contentScale = ContentScale.Fit,
        modifier = Modifier.fillMaxWidth(.82f).aspectRatio(3181f / 1521f),
    )
}

@Composable
private fun AuthTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    trailingIcon: (@Composable () -> Unit)? = null,
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        keyboardOptions = keyboardOptions,
        visualTransformation = visualTransformation,
        trailingIcon = trailingIcon,
        singleLine = true,
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth(),
    )
}

@Composable
private fun AuthPasswordField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    visible: Boolean,
    onVisibilityChange: () -> Unit,
) {
    AuthTextField(
        value = value,
        onValueChange = onValueChange,
        label = label,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
        visualTransformation = if (visible) {
            VisualTransformation.None
        } else {
            PasswordVisualTransformation()
        },
        trailingIcon = {
            IconButton(onClick = onVisibilityChange) {
                Icon(
                    imageVector = if (visible) {
                        Icons.Default.VisibilityOff
                    } else {
                        Icons.Default.Visibility
                    },
                    contentDescription = stringResource(
                        if (visible) R.string.auth_hide_password
                        else R.string.auth_show_password,
                    ),
                )
            }
        },
    )
}

@Composable
private fun TermsAgreement(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
) {
    Row(
        Modifier.fillMaxWidth()
            .toggleable(value = checked, onValueChange = onCheckedChange)
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Checkbox(checked = checked, onCheckedChange = null)
        val termsText = buildAnnotatedString {
            append(stringResource(R.string.auth_terms_prefix))
            withLink(LinkAnnotation.Clickable(tag = "terms") {}) {
                withStyle(
                    SpanStyle(
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold,
                        textDecoration = TextDecoration.Underline,
                    ),
                ) {
                    append(stringResource(R.string.auth_terms_link))
                }
            }
        }
        Text(
            text = termsText,
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.weight(1f).padding(start = 4.dp, end = 8.dp),
        )
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

@Preview(name = "Rider login", showBackground = true, widthDp = 390, heightDp = 844)
@Composable
private fun RiderLoginPreview() {
    LaynFleetTheme(darkTheme = false) {
        LoginScreen(Products.Rider) {}
    }
}

@Preview(name = "Operator login · Dark", showBackground = true, widthDp = 390, heightDp = 844)
@Composable
private fun OperatorLoginDarkPreview() {
    LaynFleetTheme(darkTheme = true) {
        LoginScreen(Products.Operator) {}
    }
}

@Preview(name = "Rider welcome", showBackground = true, widthDp = 390, heightDp = 844)
@Composable
private fun RiderWelcomePreview() {
    LaynFleetTheme(darkTheme = false) {
        WelcomeScreen(Products.Rider) {}
    }
}

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
