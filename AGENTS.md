# LaynFleet Project Rules

## Project Root

Always treat this as the active LaynFleet project:

`/Users/lincoln.mgijima/Digilayn/Apps/LaynFleet`

Do not create duplicate planning or implementation folders under other `LaynFleet` paths unless the user explicitly asks for migration or comparison work.

## App Structure

- `laynrider` is the rider-facing app.
- `laynoperator` is the operator-facing app.
- `core` is for shared domain, validation, auth flow contracts, reusable UI, and cross-app behavior when appropriate.

## Feature Planning Docs

Keep product and development flow notes in one stable Obsidian-like folder inside the project root:

`dev-flow/`

Recommended file naming:

- `00_cold_start.md`
- `01_authentication.md`
- `02_terms_and_conditions.md`
- `components/validation_input.md`
- `components/feedback_card.md`

Do not create a new docs folder each chat. Extend or update the existing `dev-flow` files.

## Authentication Direction

Both apps must use the same flow up until login success:

1. Splash screen starts the app.
2. Check whether the user is authenticated.
3. Authenticated users route to the relevant dashboard.
4. Unauthenticated users route to app-specific welcome content.
5. Users must accept terms and conditions before continuing.
6. Login defaults to Google account first.
7. Email and password login is available as a secondary option.
8. Firebase errors must be mapped to relevant production-level user messages.
9. No placeholder UI, copy, or error behavior should be introduced.

After login success, app-specific role/profile checks and dashboard routing may diverge.

## Current Feature Focus

Start with the cold start flow:

`splash screen -> auth state check -> dashboard or welcome -> terms gate -> login`

Also prepare for a reusable global validation input that can be appended and extended later without rewriting each screen.
