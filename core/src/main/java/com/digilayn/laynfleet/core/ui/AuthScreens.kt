package com.digilayn.laynfleet.core.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withLink
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.digilayn.laynfleet.core.R
import com.digilayn.laynfleet.core.auth.AuthFailure
import com.digilayn.laynfleet.core.auth.AuthService
import com.digilayn.laynfleet.core.auth.FirebaseAuthErrorMapper
import com.digilayn.laynfleet.core.auth.GoogleCredentialSignIn
import com.digilayn.laynfleet.core.auth.SignedOutAuthService
import com.digilayn.laynfleet.core.domain.Product
import com.digilayn.laynfleet.core.domain.ProductConfig
import com.digilayn.laynfleet.core.domain.Products
import com.digilayn.laynfleet.core.ui.theme.LaynFleetTheme
import com.digilayn.laynfleet.core.validation.ValidationRules
import com.digilayn.laynfleet.core.validation.validate
import kotlinx.coroutines.launch

@Composable
internal fun SplashScreen(product: ProductConfig, onAuthStateResolved: () -> Unit) {
    LaunchedEffect(product) {
        onAuthStateResolved()
    }

    Column(
        Modifier.fillMaxSize()
            .statusBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 32.dp, vertical = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
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

@Composable
internal fun WelcomeScreen(product: ProductConfig, onContinue: () -> Unit) {
    var hasAcceptedTerms by rememberSaveable { mutableStateOf(false) }

    Scaffold(Modifier.fillMaxSize()) { paddingValues ->
        Column(
            Modifier.fillMaxSize()
                .padding(paddingValues)
                .consumeWindowInsets(paddingValues)
                .navigationBarsPadding()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 28.dp),
            verticalArrangement = Arrangement.Center,
        ) {
            Column(
                Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                BrandMark(product.appName)

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
                    modifier = Modifier.padding(top = 48.dp),
                )
            }

            TermsAgreement(
                checked = hasAcceptedTerms,
                onCheckedChange = { hasAcceptedTerms = it },
                modifier = Modifier.padding(top = 40.dp),
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
internal fun LoginScreen(
    product: ProductConfig,
    googleServerClientId: String,
    authService: AuthService,
    onContinue: () -> Unit,
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var passwordVisible by rememberSaveable { mutableStateOf(false) }
    var isLoading by rememberSaveable { mutableStateOf(false) }
    var errorMessage by rememberSaveable { mutableStateOf<String?>(null) }
    var attemptedEmailLogin by rememberSaveable { mutableStateOf(false) }

    val emailRules = remember {
        listOf(
            ValidationRules.required("Email is required."),
            ValidationRules.email("Enter a valid email address."),
        )
    }
    val passwordRules = remember {
        listOf(ValidationRules.required("Password is required."))
    }

    fun handleAuthResult(result: Result<Unit>) {
        isLoading = false
        result.fold(
            onSuccess = { onContinue() },
            onFailure = {
                errorMessage = when (it) {
                    is AuthFailure -> it.authError.message
                    else -> FirebaseAuthErrorMapper.fromThrowable(it).message
                }
            },
        )
    }

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
                    stringResource(R.string.auth_welcome_back),
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 32.dp),
                )

                Text(
                    stringResource(R.string.auth_google_first_message),
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 8.dp),
                )

                Spacer(Modifier.height(24.dp))

                Button(
                    onClick = {
                        errorMessage = null
                        isLoading = true
                        scope.launch {
                            val credentialResult = GoogleCredentialSignIn(googleServerClientId)
                                .requestIdToken(context)
                            credentialResult.fold(
                                onSuccess = { idToken ->
                                    handleAuthResult(authService.signInWithGoogleIdToken(idToken))
                                },
                                onFailure = { handleAuthResult(Result.failure(it)) },
                            )
                        }
                    },
                    enabled = !isLoading && googleServerClientId.isNotBlank(),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth().height(52.dp),
                ) {
                    Icon(Icons.Default.AccountCircle, contentDescription = null)
                    Text(
                        stringResource(R.string.auth_continue_with_google),
                        modifier = Modifier.padding(start = 8.dp),
                    )
                }

                Text(
                    stringResource(R.string.auth_email_password_option),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 24.dp, bottom = 12.dp),
                )

                ValidationInput(
                    value = email,
                    onValueChange = { email = it },
                    label = stringResource(R.string.email),
                    rules = emailRules,
                    showErrors = attemptedEmailLogin,
                    enabled = !isLoading,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    modifier = Modifier.fillMaxWidth(),
                )

                Spacer(Modifier.height(10.dp))

                ValidationInput(
                    value = password,
                    onValueChange = { password = it },
                    label = stringResource(R.string.password),
                    rules = passwordRules,
                    showErrors = attemptedEmailLogin,
                    enabled = !isLoading,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    visualTransformation = if (passwordVisible) {
                        VisualTransformation.None
                    } else {
                        PasswordVisualTransformation()
                    },
                    trailingIcon = {
                        PasswordVisibilityButton(
                            visible = passwordVisible,
                            onVisibilityChange = { passwordVisible = !passwordVisible },
                            showContentDescription = stringResource(R.string.auth_show_password),
                            hideContentDescription = stringResource(R.string.auth_hide_password),
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                )

                Button(
                    onClick = {
                        attemptedEmailLogin = true
                        if (validate(email, emailRules) != null || validate(password, passwordRules) != null) {
                            return@Button
                        }
                        errorMessage = null
                        isLoading = true
                        scope.launch {
                            handleAuthResult(authService.signInWithEmail(email, password))
                        }
                    },
                    enabled = !isLoading,
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth().padding(top = 14.dp).height(50.dp),
                ) {
                    Text(
                        if (isLoading) stringResource(R.string.auth_signing_in)
                        else stringResource(R.string.auth_log_in),
                    )
                }

                errorMessage?.let {
                    FeedbackCard(
                        title = stringResource(R.string.auth_error_title),
                        message = it,
                        tone = FeedbackTone.NEGATIVE,
                        modifier = Modifier.padding(top = 18.dp),
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
private fun TermsAgreement(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier.fillMaxWidth()
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

@Preview(name = "Login light", showBackground = true, widthDp = 390, heightDp = 844)
@Composable
private fun LoginLightPreview() {
    LaynFleetTheme(darkTheme = false) {
        LoginScreen(Products.Rider, "preview-client-id", SignedOutAuthService) {}
    }
}

@Preview(name = "Login dark", showBackground = true, widthDp = 390, heightDp = 844)
@Composable
private fun LoginDarkPreview() {
    LaynFleetTheme(darkTheme = true) {
        LoginScreen(Products.Operator, "preview-client-id", SignedOutAuthService) {}
    }
}

@Preview(name = "Login minimum", showBackground = true, widthDp = 320, heightDp = 568)
@Composable
private fun LoginMinimumPreview() {
    LaynFleetTheme(darkTheme = false) {
        LoginScreen(Products.Rider, "preview-client-id", SignedOutAuthService) {}
    }
}

@Preview(name = "Welcome light", showBackground = true, widthDp = 390, heightDp = 844)
@Composable
private fun WelcomeLightPreview() {
    LaynFleetTheme(darkTheme = false) {
        WelcomeScreen(Products.Rider) {}
    }
}

@Preview(name = "Welcome dark", showBackground = true, widthDp = 390, heightDp = 844)
@Composable
private fun WelcomeDarkPreview() {
    LaynFleetTheme(darkTheme = true) {
        WelcomeScreen(Products.Operator) {}
    }
}

@Preview(name = "Welcome minimum", showBackground = true, widthDp = 320, heightDp = 568)
@Composable
private fun WelcomeMinimumPreview() {
    LaynFleetTheme(darkTheme = false) {
        WelcomeScreen(Products.Rider) {}
    }
}
