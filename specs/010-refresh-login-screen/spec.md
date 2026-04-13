# Feature Specification: Refresh Login Screen Design

**Feature Branch**: `010-refresh-login-screen`  
**Created**: 2026-04-12  
**Status**: Draft  
**Input**: User description: "update login like image attached. remove apple and google login"

## Clarifications

### Session 2026-04-12
- Q: What is the preferred method for displaying authentication error feedback? → A: Inline error messages using `TextInputLayout`.
- Q: What is the status of footer links (Terms of Service, Privacy Policy, Help Center)? → A: Temporarily removed from scope.
- Q: Which font family should be used for the new design? → A: Android system default (Roboto/Dynamic).

## User Scenarios & Testing *(mandatory)*

### User Story 1 - Authenticate with Refresh Design (Priority: P1)

As a user of Tambal Ban, I want a clean and modern login interface so that I can easily sign in to my account.

**Why this priority**: Correct authentication is the entry point for all personalized features.

**Independent Test**: Can be tested by navigating to the login screen, observing the new design, entering valid credentials, and successfully logging in.

**Acceptance Scenarios**:

1. **Given** the user is on the login screen, **When** they view the screen, **Then** they see the modern Tambal Ban branding (wrench icon, "Tambal Ban" title, "The Digital Concierge" subtitle).
2. **Given** the user is on the login screen, **When** they see the input fields, **Then** they see rounded text boxes for Email and Password with icons and descriptive placeholders.
3. **Given** the user enters valid credentials and taps "Login", **When** the action is processed, **Then** they are redirected to the main dashboard.

---

### User Story 2 - Account Registration Access (Priority: P2)

As a new user, I want to find the registration option easily from the login screen so I can create an account if I don't have one.

**Why this priority**: Essential for new user acquisition.

**Independent Test**: Tapping the "Register" link should open the account creation flow.

**Acceptance Scenarios**:

1. **Given** a user without an account on the login screen, **When** they tap the "Register" link, **Then** they are navigated to the registration screen.

---

### User Story 3 - Password Recovery Access (Priority: P2)

As a user who has forgotten their password, I want to see a recovery link so I can regain access to my account.

**Why this priority**: Prevents user churn due to forgotten credentials.

**Independent Test**: Tapping the "Forgot?" link should trigger the password recovery flow.

**Acceptance Scenarios**:

1. **Given** the user is at the password field, **When** they tap the "Forgot?" text, **Then** they are navigated to the password reset/recovery screen.

---

---

### Edge Cases

- **Error feedback**: Visual feedback for invalid email/password combinations MUST be displayed as inline error messages using Material 3 `TextInputLayout` error states.
- **Network failure**: How does the system communicate connectivity issues during the login attempt?
- **Empty fields**: How does the system handle the user tapping "Login" without filling in any details?

## Requirements *(mandatory)*

### Functional Requirements

- **FR-001**: System MUST display a modern login screen with a central card layout on a subtle gradiant background using system default typography.
- **FR-002**: System MUST show the Tambal Ban brand identity including the wrench icon and the "Digital Concierge" slogan.
- **FR-003**: System MUST provide rounded input fields for "Email Address" and "Password" with leading icons.
- **FR-004**: System MUST include a "Forgot?" link adjacent to the Password label.
- **FR-005**: System MUST provide a prominent "Login" button with an arrow indicator.
- **FR-006**: System MUST NOT include social login options (Google or Apple) as per user request.
- **FR-007**: System MUST provide a "Register" link at the bottom of the main content area.
- **FR-008**: System MUST display a copyright notice: "© 2024 TAMBAL BAN. POWERED BY THE DIGITAL CONCIERGE."

### Key Entities

- **Credentials**: User authentication data consisting of email and password.

## Success Criteria *(mandatory)*

### Measurable Outcomes

- **SC-001**: Users can successfully navigate from the login screen to the dashboard in under 1 second after tapping "Login" (assuming successful auth).
- **SC-002**: The login interaction (tapping button) provides immediate visual feedback to the user.
- **SC-003**: 100% of the visual elements defined in the image (except social logins) are present and correctly positioned on mobile devices.
- **SC-004**: Tapping on navigation actions (Forgot?, Login, Register) leads correctly to the intended screen with 0% error rate.
