# 04 Set Password

## Status

Draft for confirmation before implementation.

## Scope

Google-first users should be prompted to set a password after login if they do not already have an email/password credential.

This is not part of registration. It is an account strengthening flow for users who authenticated through Google first.

## Trigger

When a user logs in with Google for the first time:

```text
Google login success
  -> account has no password credential
  -> dashboard shows actionable notification
```

Notification copy direction:

```text
Set a password so you can sign in anywhere without relying only on Google.
```

## Dashboard Notification

The notification should:

- Be visible on dashboard.
- Explain the benefit briefly.
- Route to Set Password when tapped.
- Be dismissible only if product decides to allow dismissal.

Open decision:

- Should this notification be mandatory until password is set, or dismissible?

## Set Password Screen

Required fields:

- Password
- Confirm password

Validation:

- Password is required.
- Password minimum is 6 characters.
- Confirm password is required.
- Passwords must match.

Required explanation:

```text
Google sign-in will still work. Adding a password lets you sign in with your email on devices or browsers where Google sign-in is not available.
```

## Firebase Behavior

Implementation direction:

- Link an email/password credential to the existing Google-authenticated Firebase user.
- Do not create a second account.
- If Firebase requires recent login, show a clear re-authentication error and route through Google re-auth before retrying.

## Success Behavior

On success:

```text
password linked
  -> mark passwordSetupComplete = true
  -> remove dashboard notification
  -> return to dashboard
```

Proposed profile flag locations:

```text
/users/{userId}
  passwordSetupComplete = true

/kasilayn/LaynFleet/users/{userId}
  passwordSetupComplete = true
```

## UI Requirements

Set password must use shared validation input.

Required previews:

- Light mode set password.
- Dark mode set password.
- Minimum/small screen set password.

Screen requirements:

- Fully scrollable.
- Keyboard-safe with IME padding.
- Text wraps on narrow displays.
- No clipped buttons or fields on 320 x 568 previews.

## Tests

Unit tests should cover:

- Password minimum length.
- Password confirmation matching.
- Mapping recent-login-required errors to a user-facing message.

No UI unit tests are required for this slice.

## Open Decisions Before Build

- Is the dashboard notification mandatory or dismissible?
- Should the app store `passwordSetupComplete`, or infer it from Firebase provider data on each session?
