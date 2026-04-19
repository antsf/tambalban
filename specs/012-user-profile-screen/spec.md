# Feature Specification: User Profile Screen

**Feature Branch**: `012-user-profile-screen`  
**Created**: 2026-04-20  
**Status**: Draft  
**Input**: User description: "create profile feature like image"
**Input**: User description: "create profile feature like image"

## Clarifications

### Session 2026-04-20

- Q: Visual style for user data → A: Flat (no card).
- Q: Access point → A: Profile icon in search bar on main page.
- Q: Auth check → A: Redirect to login if not authenticated.
- Q: Scope of "Edit Profile" → A: Full Edit Capability (include the screen and logic to update user data).
- Q: Destination for Share and Rate Us → A: Redirect to Play Store link (https://play.google.com/store/apps/details?id=com.tambal_ban).

## User Scenarios & Testing *(mandatory)*

### User Story 1 - Access Profile from Home (Priority: P1)

As a user on the home screen, I want to tap a profile icon within the search bar to access my account details, so I can manage my settings without navigating away from the main context.

**Why this priority**: Defines the primary entry point for the feature.

**Independent Test**: On the Home Screen, tap the profile icon in the search bar and verify navigation to the Profile screen.

**Acceptance Scenarios**:

1. **Given** a logged-in user on the Home Screen, **When** they tap the profile icon in the search bar, **Then** they are navigated to the Profile Screen.
2. **Given** an unauthenticated user, **When** they tap the profile icon, **Then** they are redirected to the Login Screen.

---

### User Story 2 - View Personal Identity (Priority: P1)

As a user, I want to see my profile details (avatar, name, email, and phone) in a flat, clean header integrated with the page background, so I can verify my identity without unnecessary visual grouping.

**Why this priority**: Displays the core user information following the "Flat" design preference.

**Independent Test**: Verify that the profile header does not use a card container or background shift that creates a "card" effect.

**Acceptance Scenarios**:

1. **Given** the Profile screen, **When** it renders, **Then** the avatar and text are placed directly on the `surface` background.
2. **Given** the user details, **When** viewed, **Then** they use the design system's typography (Plus Jakarta Sans `headline-lg` for name) without a card boundary.

---

### User Story 2 - Account Management Navigation (Priority: P2)

As a user, I want to easily access account settings and app feedback options through a clean, icon-driven list, so I can manage my preferences efficiently.

**Why this priority**: Provides the primary functional entry points for all profile-related actions.

**Independent Test**: Can be tested by tapping each list item and verifying it triggers the correct navigation or action (e.g., "Edit Profile" leads to the edit flow).

**Acceptance Scenarios**:

1. **Given** the settings list, **When** the user scrolls, **Then** they see items grouped under semantic headers like "ACCOUNT SETTINGS" and "APP FEEDBACK".
2. **Given** a navigation item, **When** the user taps it, **Then** the ripple effect is contained within a 56dp high target area.

---

### User Story 3 - Edit Profile Information (Priority: P2)

As a user, I want to update my name, email, and phone number through a dedicated editing screen, so my account information remains accurate.

**Why this priority**: Core functionality for user-driven data maintenance.

**Independent Test**: Navigate to the Edit Profile screen, change a field, save, and verify the new data persists on the Profile View screen.

**Acceptance Scenarios**:

1. **Given** the Edit Profile screen, **When** the user modifies their name and taps "Save", **Then** the system updates the backend and reflects the change.
2. **Given** an invalid email format, **When** the user tries to save, **Then** the system displays a validation error and prevents submission.

---

### User Story 4 - Change Profile Picture (Priority: P2)

As a user, I want to upload or change my profile avatar, so I can personalize my appearance in the app.

**Why this priority**: Enhances user engagement and personalization.

**Independent Test**: Tap the avatar edit badge, select a new image, and verify the avatar updates across the app.

**Acceptance Scenarios**:

1. **Given** the Edit Profile screen, **When** the user taps the edit badge on the avatar, **Then** the system opens the image picker.
2. **Given** a new image is selected, **When** the user saves, **Then** the new avatar is uploaded and displayed.

---

### User Story 5 - Secure Logout (Priority: P3)

As a user, I want a clearly identifiable Logout action at the bottom of the screen, so I can exit the application safely when needed.

**Why this priority**: Basic security requirement for account-based applications.

**Independent Test**: Can be tested by tapping the Logout button and verifying that the session is cleared and the user is redirected to the login screen.

**Acceptance Scenarios**:

1. **Given** the Profile screen, **When** the user scrolls to the bottom, **Then** they see a Logout button with a soft background shift (e.g., `surface_container_highest`).
2. **Given** the Logout button, **When** tapped, **Then** a confirmation dialog or immediate logout occurs (to be determined by implementation).

---

### User Story 6 - Share App & Rate Us (Priority: P3)

As a user who likes the app, I want to share the app with others or rate it on the store, so I can support the developers.

**Why this priority**: Growth and feedback mechanism.

**Independent Test**: Tap "Share App" or "Rate Us" and verify it opens the Play Store link in a browser or store app.

**Acceptance Scenarios**:

1. **Given** the Profile screen, **When** the user taps "Share App", **Then** the system opens the sharing intent with the Play Store URL.
2. **Given** the Profile screen, **When** the user taps "Rate Us", **Then** the system opens the Play Store page for the app.

---

### Edge Cases

- **Missing Profile Data**: How does the system handle a user with no phone number or avatar? It must show a fallback "User" icon and hide or show a "Add phone" placeholder in the chip.
- **Long Names/Emails**: What happens with extremely long email addresses? The text must truncate gracefully with ellipsis or wrap without breaking the Hero Card's internal padding.
- **Offline Access**: If the user is offline, the Profile screen should display cached data with a subtle "Offline" indicator or timestamp.

## Requirements *(mandatory)*

### Functional Requirements

- **FR-001: Flat Identity Header**: The system MUST display user identity (avatar, name, email) directly on the `surface` background without a card container.
- **FR-002: Avatar with Edit Badge**: The system MUST show a circular avatar (min 96dp) with an Orchid (`#973497`) edit badge in the bottom-right corner.
- **FR-003: Identity Typography**: The system MUST use **Plus Jakarta Sans** for the User Name (`headline-lg`) and **Inter** for the Email (`body-md`).
- **FR-004: Contact Info Chip**: The phone number MUST be displayed in a pill-shaped chip using `surface_container_highest` background.
- **FR-005: Tonal List Items**: Each menu item MUST use a 56dp height and be separated by `1.5rem` vertical spacing rather than lines.
- **FR-006: Semantic Icons**: All menu items MUST include a leading icon from the project's iconography set.
- **FR-007: Trailing Navigation Indicators**: Each interactive list item MUST have a trailing chevron icon to indicate navigation.
- **FR-008: Soft-Action Logout**: The Logout button MUST be a full-width pill button or a card-style button with high-contrast text.
- **FR-009: Search Bar Access Point**: The Home Screen search bar MUST include a profile icon that triggers navigation to the Profile Screen.
- **FR-010: Authentication Gate**: The Profile Screen MUST verify user session; if unauthenticated, it MUST redirect to the Login Screen.
- **FR-011: Profile Editing Screen**: The system MUST provide a screen with input fields for Name, Email, and Phone Number.
- **FR-012: Input Validation**: The system MUST validate email format and ensure mandatory fields are not empty before saving.
- **FR-013: Media Selection**: The system MUST allow users to select an image from the device gallery for the avatar.
- **FR-014: Data Persistence**: The system MUST persist profile changes to the backend (Supabase) upon successful submission.
- **FR-015: App Store Redirection**: The "Rate Us" and "Share App" menu items MUST redirect users to the application's Play Store page.

### Key Entities

- **UserProfile**: Data object containing `displayName`, `email`, `phoneNumber`, and `avatarUrl`.
- **NavigationMenu**: A collection of `MenuItem` objects grouped by `Category`.

## Success Criteria *(mandatory)*

### Measurable Outcomes

- **SC-001**: 100% adherence to "The Responsive Guardian" design rules (Zero 1px borders for sectioning).
- **SC-002**: 100% of interactive items (buttons, list rows) have a minimum touch target height of 56dp.
- **SC-003**: Profile screen initial render time < 300ms on standard devices.
- **SC-004**: Contrast ratio between "Logout" text and its background meets WCAG 2.1 AA (4.5:1).
- **SC-005**: All typography strictly follows the Plus Jakarta Sans / Inter pairing as defined in the Design System.
