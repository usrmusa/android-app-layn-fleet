# 03 Profile Completion

## Status

Draft for confirmation before implementation.

## Blueprint Reference

Verified against `dev-flow/Layn_Fleet_Master_Blueprint.docx`.

Relevant blueprint rules:

- Global `/users` and `/usernames` are shared Digilayn identity records.
- LaynFleet app profile lives under `/kasilayn/LaynFleet/users/{userId}`.
- Shared flow checks profile completion before routing to membership/dashboard.

## Scope

Complete profile runs after:

- Email/password registration succeeds.
- Existing login finds an incomplete profile.
- Google first-time login creates or discovers an incomplete profile.

Both apps use the same complete profile flow until profile success.

## Required Fields

- Username
- Display name

Optional:

- Photo
- Phone

## Username

Username is a Digilayn-wide identity field.

Persistence:

```text
/usernames/{usernameLowercase}
  userId
  createdAt
  updatedAt
```

Rules:

- Required.
- Unique across the whole Digilayn project.
- Stored strict lowercase by default.
- Trim whitespace.
- Reject spaces.
- Proposed allowed characters: lowercase letters, numbers, underscores, hyphens.

Availability check:

```text
check /usernames/{usernameLowercase}
  -> missing: username available
  -> exists with same userId: username belongs to current user
  -> exists with different userId: username unavailable
```

## Display Name

Display name is a human-readable identity name.

Rules:

- Required.
- Minimum 1 word.
- Maximum 3 words.
- Trim whitespace.
- Collapse repeated spaces.
- Enforce title case per word.

Example:

```text
lincoln musa mgijima -> Lincoln Musa Mgijima
```

## Phone

Phone is optional.

Rules:

- Input should show a pre-typed leading `0`.
- Final value must be 10 digits when provided.
- Digits only.
- Blank phone is allowed.

Proposed UX:

```text
0 [remaining 9 digits]
```

## Photo

Photo is optional.

Rules:

- User can skip.
- If provided, store a profile photo URL/reference on profile records.
- Storage location and image upload rules remain a later implementation detail unless already defined elsewhere.

## Proposed Flow

```text
Complete Profile
  -> Validate username
  -> Check /usernames/{usernameLowercase}
  -> Validate display name
  -> Validate optional phone/photo
  -> Write /usernames/{usernameLowercase}
  -> Update /users/{userId}
  -> Update /kasilayn/LaynFleet/users/{userId}
  -> profileComplete = true
  -> Shared login success handler
```

## Profile Writes

Global profile:

```text
/users/{userId}
  username
  displayName
  displayNameSearch
  phone?
  photoUrl?
  updatedAt
```

LaynFleet profile copy:

```text
/kasilayn/LaynFleet/users/{userId}
  username
  displayName
  phone?
  photoUrl?
  profileComplete = true
  updatedAt
```

## UI Requirements

Complete profile must use shared validation input.

Required previews:

- Light mode complete profile.
- Dark mode complete profile.
- Minimum/small screen complete profile.

Screen requirements:

- Fully scrollable.
- Keyboard-safe with IME padding.
- Text wraps on narrow displays.
- No clipped buttons or fields on 320 x 568 previews.
- Clear username availability error.

## Tests

Unit tests should cover:

- Username normalization.
- Username allowed characters.
- Display name title-case formatting.
- Display name 1-3 word validation.
- Optional phone blank state.
- Phone 10-digit validation when provided.

No UI unit tests are required for this slice.

## Open Decisions Before Build

- Exact allowed username characters: confirm `a-z`, `0-9`, `_`, `-`.
- Whether username can be changed later.
- Whether display name should reject numeric characters.
- Whether photo upload is in scope for the first implementation or a placeholder action hidden until storage rules exist.
