# 02 Registration

## Status

Draft for confirmation before implementation.

## Blueprint Reference

Verified against `dev-flow/Layn_Fleet_Master_Blueprint.docx`.

Relevant blueprint rules:

- Firebase / Firestore app data lives under `/kasilayn/LaynFleet`.
- Global `/users` and `/usernames` remain shared Digilayn identity records.
- Shared login/profile flow checks `/kasilayn/LaynFleet/users/{userId}`.
- If the LaynFleet user profile is missing, it is copied from `/users/{userId}`.
- LaynFleet then updates the LaynFleet profile only.

## Scope

Registration is only for users choosing email and password.

Google account users do not use this registration screen before sign-in.

The registration flow is shared by both apps until registration success:

- `:laynrider`
- `:laynoperator`

Implementation should live in `:core`.

## Entry Point

The login screen should include a secondary route:

```text
Don't have an account? Create one
```

Tapping it opens the registration screen.

Registration screen must include a back action to return to login.

## Required Fields

Registration collects credentials only:

- Email
- Password
- Confirm password

Profile fields are not collected on this screen.

## Field Rules

### Email

Used for Firebase email/password authentication.

Validation:

- Required.
- Must be a valid email address.

### Password

Used for Firebase email/password authentication.

Validation:

- Required.
- Minimum 6 characters.
- Must match confirm password.

### Confirm Password

Validation:

- Required.
- Must match password exactly.

## Proposed Flow

```text
Login
  -> Create account
  -> Registration
    -> Validate email/password locally
    -> Firebase createUserWithEmailAndPassword
    -> Create Firebase Auth account
    -> Create incomplete global /users/{userId}
    -> Create incomplete /kasilayn/LaynFleet/users/{userId}
    -> Route to Complete Profile
```

## Profile Creation On Success

After Firebase account creation succeeds, create profile stubs before routing onward:

```text
/users/{userId}
  userId
  email
  createdByAppId
  profileComplete = false
  createdAt
  updatedAt

/kasilayn/LaynFleet/users/{userId}
  userId
  email
  createdByAppId
  fleetProfileCreated = true
  profileComplete = false
  createdAt
  updatedAt
```

The profile remains incomplete until the complete profile screen collects required identity fields.

## Error Handling

Firebase errors must use the shared production-level error mapper.

Registration mappings:

- Email already in use: `This email is already registered. Log in instead.`
- Weak password: `Password must be at least 6 characters.`
- Operation not allowed: `Email and password registration is not enabled. Contact support.`

## UI Requirements

Registration must use the shared validation input.

Required previews:

- Light mode registration.
- Dark mode registration.
- Minimum/small screen registration.

Screen requirements:

- Fully scrollable.
- Keyboard-safe with IME padding.
- Text wraps on narrow displays.
- No clipped buttons or fields on 320 x 568 previews.
- No placeholder-only labels.
- Back action must be available and reachable on small screens.

## Tests

Unit tests should cover:

- Password minimum length.
- Password confirmation matching.
- Registration-specific Firebase error mappings.

No UI unit tests are required for this slice.

## Confirmed Decisions

- Password minimum is 6 characters.
- Registration screen has a back action.
- Profile fields move to complete profile.
- Username is not collected on registration.
- Phone is not collected on registration.

## Open Decisions Before Build

- Should the global `/users/{userId}` stub include email only, or also provider metadata?
- Should profile stub creation be client-side first, or immediately isolated behind repository methods ready for Cloud Functions later?
