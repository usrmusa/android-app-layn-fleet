# 01 Authentication

## Status

Implemented initial Firebase sign-in wiring.

## Scope

Authentication is shared by both apps until login success:

- `:laynrider`
- `:laynoperator`

The shared implementation lives in `:core`.

## Flow

```text
Login screen
  -> Continue with Google
    -> Credential Manager returns Google ID token
    -> Firebase signs in with Google credential
    -> Shared login success handler

  -> Email and password
    -> Validate fields locally
    -> Firebase signs in with email/password
    -> Shared login success handler

  -> Create account
    -> Registration screen
    -> Validate email/password fields
    -> Firebase creates account with email/password
    -> Create incomplete Digilayn and LaynFleet profile records
    -> Complete profile screen
    -> Shared login success handler after profile is complete
```

## Current Implementation

- Firebase Auth is wrapped by `FirebaseAuthService`.
- Google account login uses Credential Manager through `GoogleCredentialSignIn`.
- Each app passes its generated `default_web_client_id` from its own Firebase config.
- `LaynFleetFlow` receives an `AuthService`.
- Splash checks `AuthService.currentUserId()`.
- Login success loads the current fleet snapshot and continues into profile/fleet routing.

## Login Priority

Google sign-in is primary.

Email/password is secondary and must remain available.

Email/password users must have a route to registration from the login screen.

## Validation

Email/password login must validate locally before Firebase is called.

Rules:

- Email is required.
- Email must have a valid format.
- Password is required.

Validation uses the shared validation module and reusable validation input.

Registration-specific validation lives in `02_registration.md`.

Profile-completion validation lives in `03_profile_completion.md`.

## Error Handling

Firebase errors must be mapped to user-facing messages.

Known mappings:

- Invalid email: `Enter a valid email address.`
- User not found: `No account exists with this email.`
- Wrong password: `The password is incorrect.`
- Invalid credential: `The email or password is incorrect.`
- Disabled user: `This account has been disabled. Contact support.`
- Too many requests: `Too many attempts. Try again later.`
- Network failure: `Check your connection and try again.`
- Account exists with different credential: `This email is already linked to another sign-in method.`
- Google sign-in cancelled: `Google sign-in was cancelled.`
- Unknown error: `We could not sign you in. Please try again.`

Registration mappings:

- Email already in use: `This email is already registered. Log in instead.`
- Weak password: `Password must be at least 6 characters.`
- Operation not allowed: `Email and password registration is not enabled. Contact support.`

## UI Requirements

Authentication screens must be scrollable and support small screens.

Required previews:

- Light mode login.
- Dark mode login.
- Minimum/small screen login.
- Light mode welcome.
- Dark mode welcome.
- Minimum/small screen welcome.
- Light mode registration.
- Dark mode registration.
- Minimum/small screen registration.
- Light mode complete profile.
- Dark mode complete profile.
- Minimum/small screen complete profile.
- Light mode set password.
- Dark mode set password.
- Minimum/small screen set password.

Form screens must use IME padding.

Text must wrap and remain readable on narrow displays.

## Tests

Unit tests cover:

- Firebase auth error mapping.
- Validation rule ordering and appendable validation behavior.

UI previews are required for visual coverage, but no UI unit tests are required for this slice.

## Open Decisions

- Whether forgot-password belongs in the first auth release.
- Whether terms acceptance is stored locally only before login or persisted after login.
- Whether successful login must immediately enforce role/profile access from Firestore before dashboard routing.

## Google First-Time Password Setup

When a user signs in with Google for the first time and has no password credential, dashboard should show an actionable notification prompting them to set a password.

Notification intent:

```text
Set a password so you can sign in anywhere without relying only on Google.
```

Clicking the notification routes to a set password screen.

The set password screen must explain:

- Google sign-in remains available.
- A password lets the user sign in with email/password on devices or environments where Google auth is unavailable.
- The password is attached to the same account, not a second account.

This flow is documented in `04_set_password.md`.
