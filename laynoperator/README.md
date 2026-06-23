# LaynOperator

LaynOperator is the operations Android app in the Layn Fleet platform. It serves fleet administrators and drivers inside approved operator spaces; operator onboarding itself remains on the Layn Fleet website.

## Product boundary

- Application ID: `com.digilayn.laynoperator`
- Allowed roles: `ADMIN`, `DRIVER`
- Backend root: `/kasilayn/LaynFleet`
- No owner role. Admin partners inside one operator have equal permissions.
- No in-app payments, in-app chat, public fleet discovery, or live tracking in the MVP.

## Role behaviour

### Admin

Admins manage users, passengers, vehicles, route groups, daily trips, trip events, and notifications inside their own operator. An admin may add another admin partner; creation records should retain `createdByAdminId` and `originalAdminId` for audit.

### Driver

Drivers must accept an invitation before membership becomes active. They may work for multiple operators, see only assigned trips and the rider contact information needed for those active assignments, and update trip statuses. They must never see unrelated rider data.

## Routing

1. Check app configuration.
2. Authenticate through shared Digilayn identity.
3. Complete the Layn Fleet profile if required.
4. Load `ADMIN` and `DRIVER` memberships.
5. Show pending invitation acceptance/rejection when applicable.
6. Show No Fleet, open the only active operator, or present the operator picker.
7. Route to the admin or driver dashboard based on the selected membership.

## Planned screens

- Splash and app-config check
- Login/register and complete profile
- No Fleet and operator picker
- Invite acceptance
- Admin dashboard
- Add rider, driver, or admin partner
- Vehicle list and add vehicle
- Passenger/user list
- Route groups
- Trips today and trip detail
- Send notification
- Driver dashboard and driver trip detail
- Settings

## Firestore access

Relevant paths from the master blueprint:

```text
/kasilayn/LaynFleet/appConfig/com.digilayn.laynoperator
/kasilayn/LaynFleet/operators/{operatorId}
/kasilayn/LaynFleet/operators/{operatorId}/users/{userId}
/kasilayn/LaynFleet/operators/{operatorId}/vehicles/{vehicleId}
/kasilayn/LaynFleet/operators/{operatorId}/passengers/{passengerId}
/kasilayn/LaynFleet/operators/{operatorId}/routeGroups/{routeGroupId}
/kasilayn/LaynFleet/operators/{operatorId}/trips/{tripId}
/kasilayn/LaynFleet/operators/{operatorId}/tripEvents/{eventId}
/kasilayn/LaynFleet/notifications/{notificationId}
```

Production reads and writes must be scoped to the selected operator. Driver trip queries should filter by `driverId`; security rules must independently verify assignment before exposing rider contact details or accepting status updates.

## Trip operations

Approved subscriptions generate operational trips. Drivers work from trips rather than raw subscriptions. Supported statuses include scheduled, driver on the way, waiting outside, passenger picked up, arrived, completed, delayed, cancelled, unavailable, vehicle changed, and driver changed.

Status changes and admin actions create in-app/push notifications for the appropriate rider or driver. Trip events should carry visibility flags for riders, drivers, and admins.

## Current implementation

The current repository and dashboards use demo data. The admin dashboard shows an operations overview, while the driver dashboard presents assigned-route information. Firebase-backed role checks, operator-scoped queries, driver assignment filters, and security rules remain required before production.

## Build

From the project root:

```bash
./gradlew :laynoperator:assembleDebug
./gradlew :core:testDebugUnitTest
```

The module requires Android SDK 36 and supports Android 10/API 29 and newer.

## Production work remaining

- Connect Firebase Auth, Firestore, and FCM.
- Implement and test operator, role, and driver-assignment security rules.
- Add invitation acceptance/rejection and role-aware navigation.
- Implement rider, driver, admin-partner, vehicle, route-group, and trip management.
- Define trip-generation cadence and date handling.
- Add loading, empty, error, suspended/revoked, and offline states.
- Complete notification templates, privacy/anonymisation, monitoring, and release setup.
