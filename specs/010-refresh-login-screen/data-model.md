# Data Model: Authentication

This document outlines the entities and UI states for the refresh login screen redesigned for mobile.

## Core Entities

### AuthCredentials (Internal)
| Field | Type | Validation |
|-------|------|------------|
| email | String | Required, Email regex |
| password | String | Required, Min 8 characters |

## UI State Management

### LoginViewState
*Represented in LoginViewModel to drive the UI.*

State | Trigger | UI Visibility
---|---|---
`Idle` | Initial / Reset | Standard form, Submit enabled
`Loading` | Tap Login | Disable form, Show progress bar
`Success` | Auth complete | Hidden, Handle navigation
`Error(msg)` | Auth failed | Inline error text, Re-enable form

## Success Scenarios
- Valid credentials lead to dashboard.
- "Forgot password" leads to recovery flow.
- "Register" leads to new user creation.

*Social login (Google/Apple) and legal footer data are not managed in this model.*
