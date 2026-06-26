# Feedback Card

## Status

Implemented initial shared component.

## Scope

The feedback card is a shared UI component for short user-facing status messages.

It belongs in `:core` so both apps can use the same status language and visual system.

## Variants

- Positive
- Negative
- Neutral

## Required Behavior

- Show a title.
- Show a message.
- Use an appropriate icon for the tone.
- Use theme colors instead of hardcoded one-off styling.
- Wrap text on small screens.
- Avoid layout shifts caused by long messages.

## First Consumers

- Authentication errors.
- Authentication success states if a screen remains visible long enough to show them.
- Future neutral loading or account state messages.

## Preview Requirements

Every screen that uses feedback cards should include preview states where practical:

- Error/negative state.
- Neutral state.
- Light and dark mode if the feedback card is central to the screen.
