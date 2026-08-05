# Feature Specification: Supabase Backend Integration

> **SUPERSEDED SCHEMA:** this spec references the retired `workshops` /
> `workshop_submissions` tables. The live shared table is `tambal_ban` — see
> [`017-workshop-schema-update`](../017-workshop-schema-update/spec.md) and
> [`../../supabase_schema.sql`](../../supabase_schema.sql).

**Feature Branch**: `002-supabase-backend-integration`  
**Created**: 2026-03-15  
**Status**: Draft  
**Input**: User description for connecting Android Tambal Ban Finder to Supabase for Auth, DB, and REST API.

## User Scenarios & Testing *(mandatory)*

### User Story 1 - View Nearby Workshops (Priority: P1)

As a public user (motorcycle/car driver), I want to see tire repair workshops on a map so that I can find the nearest help for my flat tire.

**Why this priority**: This is the core functionality of the app and provides immediate value to all users without requiring account creation.

**Independent Test**: Can be tested by opening the Map screen and verifying that workshop markers appear and can be tapped to see details.

**Acceptance Scenarios**:

1. **Given** I am on the Home Map screen, **When** I have internet connection, **Then** I should see markers for verified workshops.
2. **Given** I have tapped a workshop marker, **When** I am in the detail view, **Then** I should see the workshop's name, phone, and rating, and have options to Call or Navigate.

---

### User Story 2 - User Authentication (Priority: P2)

As a user, I want to create an account and log in so that I can access features like adding workshops and writing reviews.

**Why this priority**: Authentication is a gateway for all user-contributed content features.

**Independent Test**: Can be tested by navigating to the login screen and successfully logging in with valid credentials, resulting in an active session.

**Acceptance Scenarios**:

1. **Given** I am on the Login screen, **When** I enter valid email and password, **Then** I should be redirected to the previous screen or Home screen with an active session.
2. **Given** I enter invalid credentials, **When** I tap Login, **Then** I should see an appropriate error message and remain on the Login screen.

---

### User Story 3 - Submit New Workshop (Priority: P3)

As an authenticated user, I want to add a new workshop location that I've discovered so that I can help other drivers find it.

**Why this priority**: This allows the community to grow the database of repair shops.

**Independent Test**: Can be tested by an authenticated user filling out the "Add Workshop" form and verifying that the data is sent to the `workshop_submissions` table.

**Acceptance Scenarios**:

1. **Given** I am an authenticated user on the "Add Workshop" screen, **When** I submit valid workshop details (name, location), **Then** the submission should be saved as "pending" in the system.
2. **Given** I am not logged in, **When** I tap "Add Workshop", **Then** I should be prompted to log in before I can see the submission form.

---

### User Story 4 - Write Workshop Review (Priority: P3)

As an authenticated user, I want to rate and review a workshop so that I can share my experience with others.

**Why this priority**: Helps users identify high-quality service providers through community feedback.

**Independent Test**: Can be tested by an authenticated user submitting a rating and comment on a workshop detail page.

**Acceptance Scenarios**:

1. **Given** I am an authenticated user on a Workshop Detail screen, **When** I submit a rating and comment, **Then** the review should be recorded and eventually visible to other users.

---

### Edge Cases

- **Offline Access**: If the user loses internet connection while viewing the map, markers should persist if cached, but new data fetching should fail gracefully with a notification.
- **Session Expiry**: If the user's Auth token expires while using the app, the system must handle the 401 error by prompting the user to log in again when they attempt an authenticated action.

## Requirements *(mandatory)*

### Functional Requirements

- **FR-001**: System MUST display a map using the `osmdroid` library.
- **FR-002**: System MUST fetch verified workshop data from the Supabase REST API `/workshops?verified=eq.true`.
- **FR-003**: System MUST allow public users to view details of a selected workshop (name, address, phone, coordinates, rating).
- **FR-004**: System MUST allow users to trigger a phone call via the device's dialer intent from the workshop detail screen.
- **FR-005**: System MUST allow users to trigger navigation to a workshop using external map applications (e.g., Google Maps).
- **FR-006**: System MUST authenticate users using email and password via Supabase Auth REST API.
- **FR-007**: System MUST store the session access token securely using `EncryptedSharedPreferences`.
- **FR-008**: System MUST allow authenticated users to submit new workshop data to the `workshop_submissions` table.
- **FR-009**: System MUST allow authenticated users to submit reviews (rating and comments) to the `reviews` table.
- **FR-010**: System MUST include a `SupabaseConfig` object to manage `SUPABASE_URL` and `SUPABASE_ANON_KEY`.

### Key Entities *(include if feature involves data)*

- **Workshop**: Represents a tire repair shop. Attributes: ID, Name, Address, Phone, Latitude, Longitude, Rating, Verification Status.
- **User**: Represents an application user. Attributes: ID, Email, Role.
- **Review**: Represents a user's feedback on a workshop. Attributes: ID, Workshop ID, User ID, Rating, Comment.
- **Submission**: Represents a new workshop proposal awaiting admin approval. Attributes: ID, Name, Location, Submitter ID, Status.

## Success Criteria *(mandatory)*

### Measurable Outcomes

- **SC-001**: Users can load and view workshop markers on the map within 2 seconds of opening the screen on a 4G connection.
- **SC-002**: Authenticated users can complete the workshop submission process in under 1 minute.
- **SC-003**: The app maintains 99.9% crash-free sessions specifically regarding network failures and API errors.
- **SC-004**: Zero instances of authentication tokens being stored in plain text on the device filesystem.
- **SC-005**: 100% of "Call" and "Navigate" actions successfully launch the corresponding system apps.
