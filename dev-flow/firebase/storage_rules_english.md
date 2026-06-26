# Firebase Storage Rules - English Guide

## What This File Explains

This is the plain-English version of the Storage rules.

Source file:

```text
/Users/lincoln.mgijima/Digilayn/Firebase/rules/storage.rules
```

## Main Rule

The Storage rules match every file path:

```text
/{allPaths=**}
```

## Access Window

Reads and writes are allowed only before:

```text
2026-06-24
```

## Current Result

Because the current date is after `2026-06-24`, the rule now denies all client access.

| Action | Current result |
|---|---|
| Read files | Denied |
| Upload files | Denied |
| Update files | Denied |
| Delete files | Denied |

## Important Note

There are no more specific Storage rules in the file.

That means every Storage path follows this expired rule.
