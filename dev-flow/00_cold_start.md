# 00 Cold Start

## Status

Drafted for implementation.

## Scope

Cold start owns the first route decision for both LaynFleet apps:

- `:laynrider`
- `:laynoperator`

Both apps enter through `core/src/main/java/com/digilayn/laynfleet/core/ui/LaynFleetFlow.kt`, so the route flow must stay shared until the user has successfully authenticated.

## Flow

```text
App launch
  -> Splash screen
  -> Check authentication state
    -> Authenticated
      -> Load shared fleet snapshot/profile state
      -> Profile incomplete: Complete profile
      -> Profile complete: Fleet picker or dashboard
    -> Unauthenticated
      -> Welcome page
      -> Terms gate
      -> Login page
```

## Shared Route States

The shared flow should model cold start explicitly instead of defaulting directly to login:

- `SPLASH`
- `WELCOME`
- `LOGIN`
- `PROFILE`
- `FLEETS`
- `HOME`

`SPLASH` is a real state, even while Firebase is pending. It prevents flashing the wrong screen while auth state is being resolved.

## Splash Screen

Purpose:

- Show brand presence immediately.
- Check whether a user is already authenticated.
- Avoid showing welcome or login until the auth state is known.

Required behavior:

- If Firebase reports a signed-in user, route forward as authenticated.
- If Firebase reports no signed-in user, route to `WELCOME`.
- If auth check fails unexpectedly, route to `WELCOME` and keep a user-safe error available for the auth screen.

Current temporary behavior:

- `LaynFleetFlow` starts at `LOGIN`.

Target behavior:

- `LaynFleetFlow` starts at `SPLASH`.

## Authenticated Route

After splash confirms an authenticated user:

```text
loadSnapshot(product)
  -> profileComplete = false: PROFILE
  -> profileComplete = true and no selected membership: FLEETS
  -> selected membership exists: HOME
```

Firebase implementation note:

- Firebase auth confirms identity.
- Repository/profile loading confirms LaynFleet access and app role eligibility.
- Rider app allows `RIDER`.
- Operator app allows `ADMIN` and `DRIVER`.

## Unauthenticated Route

After splash confirms no authenticated user:

```text
WELCOME
  -> user reads app-specific welcome copy
  -> user must accept terms and conditions
  -> Next routes to LOGIN only when accepted
```

Welcome copy must be app-specific through `ProductConfig`.

Rider direction:

```text
See your daily route, trip status, driver updates, and transport details in one place.
```

Operator direction:

```text
Manage daily fleet movement, driver assignments, route visibility, and admin operations in one place.
```

## Terms Gate

Users must accept terms before reaching login.

Preferred behavior:

- Keep `Next` disabled until accepted.
- Show a short helper message near the checkbox when needed.
- Do not persist terms acceptance as final legal consent until the backend/user-profile decision is made.

Open decision:

- Store pre-login acceptance locally only, or persist acceptance after successful login.

## Login Handoff

The login screen is not part of cold start implementation beyond routing into it.

Authentication feature requirements live in `01_authentication.md`.

Known login direction:

- Google account login is primary/default.
- Email/password login is secondary.
- Firebase errors must be mapped to production-level messages.
- No placeholder auth behavior should remain.

## Global Validation Input Dependency

Authentication screens must use a reusable validation input component rather than one-off fields.

The component should support:

- Value and value change callbacks.
- Label.
- Keyboard options.
- Visual transformation.
- Leading/trailing icon slots.
- Required validation.
- Custom validation rules.
- Error text.
- Disabled/loading states.
- Password visibility behavior through composition, not hardcoded email/password-only logic.

This should be documented separately in:

`dev-flow/components/validation_input.md`

## Production Rules

- No placeholder copy.
- No fake loading states that imply real Firebase is connected.
- No screen flashes from login to dashboard during auth resolution.
- No app-specific forks before login success except welcome copy and allowed role configuration.
- Shared route logic belongs in `:core`.

## Implementation Plan

1. Add `SPLASH` and `WELCOME` route states to `LaynFleetFlow`.
2. Add a splash composable that checks the current auth state through an injected auth/session contract.
3. Add a welcome composable that renders product-specific content and terms acceptance.
4. Route unauthenticated users from splash to welcome, then to login.
5. Route authenticated users from splash into existing profile/fleet/dashboard flow.
6. Replace current demo login assumptions when Firebase auth contracts are introduced in `01_authentication.md`.

## Acceptance Criteria

- Both apps launch into the same shared cold start flow.
- Splash is shown while auth status is unresolved.
- Authenticated users do not see welcome/login.
- Unauthenticated users see app-specific welcome copy.
- Users cannot continue to login until terms are accepted.
- The route flow is documented and implemented from `:core`.
