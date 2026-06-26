# Validation Input

## Status

Drafted as a shared component requirement.

## Scope

Create one reusable input component for auth and future forms across both apps.

The component belongs in `:core` because `:laynrider` and `:laynoperator` share the same auth flow.

## Required Capabilities

- Standard text input.
- Password input through visual transformation and trailing icon composition.
- Email keyboard support.
- Required field validation.
- Custom validation rules.
- Error text rendered directly under the field.
- Loading and disabled states.
- Leading and trailing icon slots.
- Single-line default with room to extend later.

## Validation Model

Validation should be appendable:

```text
value
  -> required rule
  -> format rule
  -> custom screen rule
  -> first failing message shown
```

Examples:

- Email is required.
- Enter a valid email address.
- Password is required.
- Password must be at least 8 characters.

## Design Direction

- Use Material 3 `OutlinedTextField`.
- Match `LaynFleetTheme`.
- Avoid placeholder-only labels.
- Show concrete error text for invalid input.
- Keep password visibility as a reusable trailing action, not a separate unrelated field implementation.

## First Consumers

- Email/password login.
- Sign-up if enabled later.
- Complete profile.
- Phone number.

## Current Implementation Notes

- Email/password login uses local validation before Firebase is called.
- Errors can be hidden until a submit attempt through the input's error visibility control.
- Validation rules are appendable and tested in JVM unit tests.
