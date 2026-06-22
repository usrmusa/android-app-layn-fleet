# Layn Fleet Android

The project ships two separate applications backed by one shared library.

| Module | Application ID | Responsibility |
|---|---|---|
| `:laynrider` | `com.digilayn.laynrider` | Rider and guardian experience |
| `:laynoperator` | `com.digilayn.laynoperator` | Admin and driver experience |
| `:core` | Android library | Login, profile, app config, membership routing, fleet picker, shared models/data/UI |

Firebase credentials and production rules remain pending. Implement `FleetRepository` in `:core`
and inject it into `LaynFleetFlow` for both apps.
