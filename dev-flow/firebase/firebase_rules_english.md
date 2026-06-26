# Firebase Firestore Rules - English Guide

## What This File Explains

This is the plain-English version of the Firestore rules for Digilayn shared identity, Poortjie public website content, and LaynFleet.

## Main Rule

Firestore is closed by default.

If a path is not listed in the rules, users cannot read it or write it.

## Naming Rules

LaynFleet uses camel case in Firebase paths.

```text
/kasilayn/LaynFleet/
```

App config stays at the Firebase root.

```text
/appConfig/{appId}
```

Global Digilayn identity stays outside LaynFleet.

```text
/users/{userId}
/usernames/{username}
```

## Role Checks

| Role check | What it means |
|---|---|
| Signed-in user | The request has Firebase Auth. |
| Owner | The signed-in user's uid matches the document `userId`. |
| Digilayn super user | The logged-in user's email is `digilayn@gmail.com`. |
| LaynFleet Operator | The user's `/kasilayn/LaynFleet/{operatorId}/users/{uid}` document has `isOperator == true`. |
| LaynFleet Driver | The user's `/kasilayn/LaynFleet/{operatorId}/users/{uid}` document has `isDriver == true`. |
| LaynFleet Member | The user's `/kasilayn/LaynFleet/{operatorId}/users/{uid}` document has `isMember == true`. |

## App Config

Path:

```text
/appConfig/{appId}
```

Examples:

```text
/appConfig/com.digilayn.laynoperator
/appConfig/com.digilayn.laynmember
```

| Action | Who can do it |
|---|---|
| Read | Anyone |
| Write | Digilayn super users only |

## Global Users

Path:

```text
/users/{userId}
```

This is the shared Digilayn identity profile. LaynFleet does not store all fleet roles here.

### Who Can Read

| Action | Who can do it |
|---|---|
| Get own user document | The owner |
| Get user documents | Digilayn super users |
| List users | Digilayn super users |

### Who Can Create

A signed-in user can create only their own user document.

Rules:

- The document ID must match the signed-in user's uid.
- `userId` must match the signed-in user's uid.
- Only approved identity fields are allowed.

Allowed create fields:

```text
userId
username
displayName
email
phone
photoUrl
bio
createdAt
updatedAt
devices
usernameLastChangedAt
lastActive
lastLoginDeviceId
```

### What The Owner Can Update

The owner can update their own user document, but `userId` must stay the same.

Allowed owner update fields:

```text
username
displayName
phone
photoUrl
bio
devices
usernameLastChangedAt
lastActive
updatedAt
lastLoginDeviceId
```

### Delete

| Action | Who can do it |
|---|---|
| Delete own user document | The owner |
| Delete user documents | Digilayn super users |

## Usernames

Path:

```text
/usernames/{username}
```

This collection reserves usernames.

| Action | Who can do it |
|---|---|
| Get a username document | Any signed-in user |
| List usernames | Digilayn super users only |
| Create username reservation | Any signed-in user, for themselves |
| Release username | The owner, by setting `userId` to `null` |
| Update username documents | Digilayn super users |
| Delete own username document | The owner |
| Delete username documents | Digilayn super users |

Allowed fields:

```text
userId
lockedUntil
```

Create rule:

- `userId` must equal the signed-in user's uid.

Release rule:

- The current owner can update the username document only by setting `userId` to `null`.

## User Traces

Path:

```text
/userTraces/{userId}
```

Used for account deletion request traces.

| Action | Who can do it |
|---|---|
| Create own trace | The signed-in owner |
| Read | Nobody |
| Update | Nobody |
| Delete | Nobody |

Create rules:

- `userId` must equal the signed-in user's uid.
- `action` must be `accountDeleteRequested`.
- `status` must be `pending`.
- `appType` must be the application id, for example `com.digilayn.laynmember`.

Allowed fields:

```text
userId
action
status
appType
createdAt
```

## Public Poortjie Content

Poortjie is the public website entry point. It may expose LaynFleet web features, but LaynFleet data must still use the LaynFleet path.

Public Poortjie paths:

```text
/poortjie/{document}
/poortjie/services
/poortjie/news
/poortjie/news/updates/{newsId}
```

| Action | Who can do it |
|---|---|
| Read | Anyone |
| Write | Digilayn super users and Poortjie Operators |

LaynFleet web paths must use:

```text
/kasilayn/LaynFleet/{operatorId}/...
```

## LaynFleet Operation

Root path:

```text
/kasilayn/LaynFleet/{operatorId}
```

The operator document is the root of one fleet space.

The `operatorId` belongs to the first owner/operator who created or owns the fleet. This makes it easy to track the original fleet owner.

### Who Can Read Operator Records

| Reader | When allowed |
|---|---|
| Digilayn super user | Always |
| LaynFleet Operator | When they belong to this operator space |
| LaynFleet Driver | Only fields needed for their assigned work and fleet display |
| LaynFleet Member | Only fields needed for their membership, dashboard, support, and branding |

### Who Can Create Operator Records

| Creator | Condition |
|---|---|
| Digilayn super user | Always |
| Approved web onboarding flow | Only after operator approval and setup |

New operator records must include:

```text
operatorId
ownerUserId
name
shortName
status
billingStatus
createdAt
updatedAt
```

### Who Can Update Operator Records

| Action | Who can do it |
|---|---|
| Update branding and support fields | LaynFleet Operators inside that operator space |
| Update status, billing, or platform fields | Digilayn super users only |
| Delete operator record | Digilayn super users only |

## Operator Users

Path:

```text
/kasilayn/LaynFleet/{operatorId}/users/{userId}
```

This is the operator-specific copy of a global Digilayn user.

When an operator adds a person, user data is copied from:

```text
/users/{userId}
```

into:

```text
/kasilayn/LaynFleet/{operatorId}/users/{userId}
```

A user can belong to multiple operators. Each operator has its own copied user document.

### Allowed Role Fields

```text
isOperator
isDriver
isMember
```

### Required Tracking Fields

```text
operatorId
userId
originalOwnerId
createdByOperatorId
createdAt
updatedAt
status
```

`originalOwnerId` stores the first owner/operator linked to the fleet.

`createdByOperatorId` stores the operator/admin who added this user.

### Who Can Read Operator Users

| Reader | When allowed |
|---|---|
| Digilayn super user | Always |
| LaynFleet Operator | All users inside their operator space |
| LaynFleet Driver | Their own user document, and limited member contact data only for assigned trips |
| LaynFleet Member | Their own user document |

### Who Can Create Operator Users

| Creator | Condition |
|---|---|
| Digilayn super user | Always |
| LaynFleet Operator | Can add users inside their operator space |

Creation rules:

- `operatorId` must match the operator path.
- `userId` must match the document ID.
- `createdByOperatorId` must match the signed-in operator.
- At least one role flag must be true: `isOperator`, `isDriver`, or `isMember`.
- Status must start as a valid membership status.

### What A LaynFleet Operator Can Do

| Action | Condition |
|---|---|
| Add member | User is copied into the operator space with `isMember == true`. |
| Add driver | User is copied into the operator space with `isDriver == true`. |
| Add operator partner | User is copied into the operator space with `isOperator == true`. |
| Suspend user | User belongs to the same operator. |
| Revoke user | User belongs to the same operator. |
| Update role flags | User belongs to the same operator. |

### What The User Can Do

| Action | Condition |
|---|---|
| Read own operator profile | `userId` matches their uid. |
| Update own basic profile fields | Only safe personal fields may change. |
| Accept driver/operator invite | Their status is `pendingAcceptance`. |
| Reject driver/operator invite | Their status is `pendingAcceptance`. |

### Delete

| Action | Who can do it |
|---|---|
| Soft remove operator user | LaynFleet Operators inside that operator space |
| Hard delete operator user | Digilayn super users only |

## Vehicles

Path:

```text
/kasilayn/LaynFleet/{operatorId}/vehicles/{vehicleId}
```

| Action | Who can do it |
|---|---|
| Read | LaynFleet Operators in the same operator space |
| Read assigned vehicle | Assigned LaynFleet Driver |
| Create | LaynFleet Operators in the same operator space |
| Update | LaynFleet Operators in the same operator space |
| Delete | Digilayn super users only |

## Passengers

Path:

```text
/kasilayn/LaynFleet/{operatorId}/passengers/{passengerId}
```

Passengers are people being transported, for example children, staff, or delivery-related people.

### Who Can Read Passengers

| Reader | When allowed |
|---|---|
| Digilayn super user | Always |
| LaynFleet Operator | All passengers inside their operator space |
| LaynFleet Member | When `memberUserId` matches their uid |
| Assigned LaynFleet Driver | Only passengers linked to assigned trips |

### Who Can Create Passengers

| Creator | Condition |
|---|---|
| LaynFleet Operator | Can create passengers directly for members in the same operator space |
| LaynFleet Member | Can create passenger update/add request for themselves, pending operator review |

Members do not directly approve their own passengers. Member-created passenger changes must go through review.

### What A LaynFleet Operator Can Do

| Action | Condition |
|---|---|
| Create passenger | Passenger belongs to a member in the same operator space. |
| Approve passenger request | Passenger request belongs to the same operator space. |
| Reject passenger request | Rejection reason is provided. |
| Update passenger | Passenger belongs to the same operator space. |
| Remove passenger | Passenger belongs to the same operator space. |

## Subscriptions

Path:

```text
/kasilayn/LaynFleet/{operatorId}/subscriptions/{subscriptionId}
```

Subscriptions are created and managed by operators. They define recurring transport needs.

### Who Can Read Subscriptions

| Reader | When allowed |
|---|---|
| Digilayn super user | Always |
| LaynFleet Operator | All subscriptions inside their operator space |
| LaynFleet Member | When `memberUserId` matches their uid |
| LaynFleet Driver | Only if linked to assigned trips or route groups |

### Who Can Create Subscriptions

| Creator | Condition |
|---|---|
| LaynFleet Operator | Can create subscriptions for members in the same operator space |
| Digilayn super user | Always |

### What A LaynFleet Operator Can Do

| Action | Condition |
|---|---|
| Create subscription | Member belongs to the same operator space. |
| Update subscription | Subscription belongs to the same operator space. |
| Cancel subscription | Cancellation reason is provided. |
| Assign to route group | Route group belongs to the same operator space. |

### What A LaynFleet Member Can Do

| Action | Condition |
|---|---|
| Read own subscription | `memberUserId` matches their uid. |
| Request subscription update | Update request is created under the same operator space. |

Members cannot directly change approved subscription records.

## Subscription Requests

Path:

```text
/kasilayn/LaynFleet/{operatorId}/subscriptionRequests/{requestId}
```

Subscription requests are used when members ask to change transport details, such as pickup time, location, date, return needs, or passenger details.

### Who Can Read Subscription Requests

| Reader | When allowed |
|---|---|
| Digilayn super user | Always |
| LaynFleet Operator | All requests inside their operator space |
| LaynFleet Member | When `memberUserId` matches their uid |

### Who Can Create Subscription Requests

| Creator | Condition |
|---|---|
| LaynFleet Member | Can create requests for themselves. |
| LaynFleet Operator | Can create internal requests inside the same operator space. |

New requests must start clean:

- `operatorId` must match the path.
- `memberUserId` must match the signed-in member when created by a member.
- `status` must be `pendingOperatorReview`.
- Approval, rejection, and completion fields must be empty.

### What A LaynFleet Operator Can Do

| Action | Condition |
|---|---|
| Approve request | Request is `pendingOperatorReview`; approved fields are applied to the target subscription/passenger. |
| Reject request | Request is `pendingOperatorReview`; rejection reason is provided. |
| Cancel request | Request has not already been approved or rejected. |

### What A LaynFleet Member Can Do

| Action | Condition |
|---|---|
| Create update request | Request belongs to their own membership. |
| Cancel own pending request | Request is still `pendingOperatorReview`. |
| Read outcome | Request belongs to them. |

## Route Groups

Path:

```text
/kasilayn/LaynFleet/{operatorId}/routeGroups/{routeGroupId}
```

Route groups define recurring operational routes.

| Action | Who can do it |
|---|---|
| Read | LaynFleet Operators in the same operator space |
| Read assigned route | Assigned LaynFleet Driver |
| Create | LaynFleet Operators in the same operator space |
| Update | LaynFleet Operators in the same operator space |
| Delete | Digilayn super users only |

Members do not read raw route groups unless a safe member-facing summary is exposed through their subscription or trip.

## Trips

Path:

```text
/kasilayn/LaynFleet/{operatorId}/trips/{tripId}
```

Trips are daily operational records generated from approved subscriptions.

### Who Can Read Trips

| Reader | When allowed |
|---|---|
| Digilayn super user | Always |
| LaynFleet Operator | All trips inside their operator space |
| LaynFleet Member | When `memberUserId` matches their uid |
| Assigned LaynFleet Driver | When `driverUserId` matches their uid |

### Who Can Create Trips

| Creator | Condition |
|---|---|
| LaynFleet Operator | Can create trips inside their operator space |
| Cloud Function | Can generate trips from approved subscriptions |
| Digilayn super user | Always |

### What A LaynFleet Operator Can Do

| Action | Condition |
|---|---|
| Create trip | Source subscription belongs to the same operator space. |
| Assign driver | Driver belongs to the same operator space and has `isDriver == true`. |
| Change vehicle | Vehicle belongs to the same operator space. |
| Cancel trip | Trip is not completed; cancellation reason is provided. |
| Update trip | Trip belongs to the same operator space. |

### What The Assigned Driver Can Do

| Action | Condition |
|---|---|
| Start trip | Trip is assigned to the driver. |
| Mark driver on the way | Trip is assigned to the driver. |
| Mark waiting outside | Trip is assigned to the driver. |
| Mark passenger picked up | Trip is assigned to the driver. |
| Mark arrived at destination | Trip is assigned to the driver. |
| Complete trip | Trip is assigned to the driver and not cancelled. |
| Report delay | Trip is assigned to the driver; status message is provided. |
| Request cancellation | Trip is assigned to the driver; reason is provided. |

### What The Member Can Do

| Action | Condition |
|---|---|
| Read own trip | `memberUserId` matches their uid. |
| See cancellation | Trip belongs to them. |
| See driver/vehicle changes | Trip belongs to them. |

Members cannot directly update trip status.

## Trip Events

Path:

```text
/kasilayn/LaynFleet/{operatorId}/tripEvents/{eventId}
```

Trip events are timeline records for trip changes.

### Who Can Read Trip Events

| Reader | When allowed |
|---|---|
| Digilayn super user | Always |
| LaynFleet Operator | Events inside their operator space |
| LaynFleet Driver | Events for assigned trips where `visibleToDriver == true` |
| LaynFleet Member | Events for own trips where `visibleToMember == true` |

### Who Can Create Trip Events

| Creator | Condition |
|---|---|
| LaynFleet Operator | Event belongs to a trip in the same operator space. |
| Assigned LaynFleet Driver | Event belongs to their assigned trip. |
| Cloud Function | Event is created from a valid system action. |

Trip events should not be edited after creation, except by Digilayn super users.

## Notifications

Path:

```text
/kasilayn/LaynFleet/{operatorId}/notifications/{notificationId}
```

Notifications are stored per operator.

### Who Can Read Notifications

| Reader | When allowed |
|---|---|
| Digilayn super user | Always |
| LaynFleet Operator | Notifications inside their operator space |
| LaynFleet Member | When `toUserId` matches their uid |
| LaynFleet Driver | When `toUserId` matches their uid |

### Who Can Create Notifications

| Creator | Condition |
|---|---|
| LaynFleet Operator | Can notify users inside the same operator space. |
| Cloud Function | Can create system notifications. |
| Digilayn super user | Always |

### What A User Can Do

| Action | Condition |
|---|---|
| Mark own notification as read | `toUserId` matches their uid. |

Users cannot change notification title, message, recipient, type, or related records.

## Devices

Path:

```text
/kasilayn/LaynFleet/{operatorId}/devices/{deviceId}
```

Devices store push tokens and app/device info for LaynFleet users inside an operator space.

| Action | Who can do it |
|---|---|
| Read own device | Device `userId` matches signed-in uid |
| Create own device | Device `userId` matches signed-in uid |
| Update own device | Device `userId` matches signed-in uid |
| Read operator devices | LaynFleet Operators inside the same operator space |
| Delete device | Owner or Digilayn super user |

## Operator Requests From Web

Path:

```text
/kasilayn/LaynFleet/operatorRequests/{requestId}
```

This path is used by the Poortjie website LaynFleet section to capture operator onboarding requests before a real operator space is created.

| Action | Who can do it |
|---|---|
| Create | Signed-in web user |
| Read own request | Request `webUserId` matches signed-in uid |
| Read all requests | Digilayn super users |
| Update status/review fields | Digilayn super users |
| Delete | Digilayn super users |

## Suggested Places

Path:

```text
/kasilayn/LaynFleet/{operatorId}/suggestedPlaces/{placeId}
```

| Action | Who can do it |
|---|---|
| Read | Signed-in users belonging to the same operator space |
| Write | LaynFleet Operators in the same operator space and Digilayn super users |

## Driver Presence

Driver presence is not used in the MVP.

Reserved future path:

```text
/kasilayn/LaynFleet/{operatorId}/presence/{driverUserId}
```

Future use:

- Online/offline status.
- Last active time.
- Current trip reference.
- Live location, if live tracking is added later.

For MVP, Firestore rules should deny read and write access to this path unless the feature is explicitly enabled later.

## Delete Summary

| Path type | Who can delete |
|---|---|
| App config | Digilayn super users |
| Global users | Owner or Digilayn super users |
| LaynFleet operator records | Digilayn super users |
| Operator users | Soft remove by LaynFleet Operator; hard delete by Digilayn super user |
| Vehicles | Digilayn super users |
| Passengers | LaynFleet Operators inside the same operator space; hard delete by Digilayn super user |
| Subscriptions | LaynFleet Operators inside the same operator space; hard delete by Digilayn super user |
| Subscription requests | Owner can cancel pending own request; operators can remove within same operator space; hard delete by Digilayn super user |
| Trips | LaynFleet Operators can cancel; hard delete by Digilayn super user |
| Trip events | Digilayn super users only |
| Notifications | Digilayn super users only |
| Devices | Owner or Digilayn super user |

## Final Default Deny

Every path not matched above denies reads and writes.
