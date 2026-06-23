# LaynRider

LaynRider is the passenger-facing Android app in the Layn Fleet platform. It is for riders, parents, and guardians who have been added to a private operator fleet by an administrator.

## Product boundary

- Application ID: `com.digilayn.laynrider`
- Allowed role: `RIDER`
- Backend root: `/kasilayn/LaynFleet`
- No public fleet discovery, in-app payments, in-app chat, or live tracking in the MVP.
- A rider may belong to more than one operator and chooses the fleet they want to view.

## Rider experience

The app checks configuration, authenticates through the shared Digilayn identity, completes the Layn Fleet profile, and loads active rider memberships.

- No active memberships: show the No Fleet screen.
- One active membership: open that operator.
- Multiple active memberships: show the fleet picker.
- Revoked or suspended memberships must not open a dashboard.

The rider dashboard is intentionally personal. For the selected operator it shows only trips where:

```text
trip.riderUserId == signedInUserId
trip.operatorId == selectedOperatorId
```

Each trip exposes the information the rider needs for that journey: passenger, pickup address, drop-off address, scheduled pickup time, current status, assigned driver, and assigned vehicle. It must never expose another rider’s trips or passenger details.

## Planned screens

- Splash and app-config check
- Login/register
- Complete profile
- No Fleet
- Fleet picker
- Operator-branded home
- Passenger list and add/edit passenger
- Subscription/request details
- Request update
- Trip status
- Notifications
- Settings and account deletion

## Firestore access

Relevant paths from the master blueprint:

```text
/kasilayn/LaynFleet/appConfig/com.digilayn.laynrider
/kasilayn/LaynFleet/users/{userId}
/kasilayn/LaynFleet/operators/{operatorId}/users/{userId}
/kasilayn/LaynFleet/operators/{operatorId}/passengers/{passengerId}
/kasilayn/LaynFleet/operators/{operatorId}/trips/{tripId}
/kasilayn/LaynFleet/notifications/{notificationId}
```

Production trip queries should filter by both `riderUserId` and the selected `operatorId`. Firestore security rules must independently enforce the same ownership rule; client-side filtering is a UI safeguard, not an authorization boundary.

Riders may read their own fleet profile, membership, linked passengers, trips, and notifications. They may submit subscription/update requests, but an admin approves or rejects changes.

## Notifications

Rider-facing events include driver or vehicle changes, driver waiting outside, arrival at destination, trip cancellation, update approval/rejection, payment-status reminders, and targeted admin notes.

## Current implementation

The current repository is demo-backed. `FleetSnapshot.tripsForRider()` scopes dashboard trips to the signed-in rider and selected operator. Replace `DemoFleetRepository` with Firebase-backed authentication and Firestore reads before production.

## Build

From the project root:

```bash
./gradlew :laynrider:assembleDebug
./gradlew :core:testDebugUnitTest
```

The module requires Android SDK 36 and supports Android 10/API 29 and newer.

## Production work remaining

- Connect Firebase Auth, Firestore, and FCM.
- Write and test Firestore ownership rules and required indexes.
- Implement passenger and subscription-update workflows.
- Add loading, empty, error, revoked-membership, and offline states.
- Implement account deletion/anonymisation according to the Digilayn identity policy.
- Finalise notification copy and trip-generation cadence.
