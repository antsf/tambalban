# Quickstart: Refresh Login Screen Design

This guide explains how to verify the new login screen design after scope clarification.

## 1. Visual Verification
To see the new design without logging out current users:
1. Open `app/src/main/res/layout/activity_login.xml` in Android Studio's **Design Preview**.
2. Verify:
   - Card layout is perfectly centered vertically and horizontally.
   - All text uses the system default font family.
   - The footer area contains **ONLY** the copyright notice and "Powered by" text.
   - Google/Apple buttons and social login text are **ABSENT**.

## 2. Interactive Testing
1. **Empty Fields**: Tap "Login" with empty fields. Verify inline `setError` messages appear for both Email and Password.
2. **Invalid Email**: Enter a malformed email. Verify the error message appears inline under the email field.
3. **Password Security**: Verify the password field uses a trailing visibility toggle (eye icon) and dots for masking.

## 3. Navigation Links
- Tapping **Register** → `RegisterActivity`.
- Tapping **Forgot?** → Trigger password reset flow (or show placeholder toast if not implemented).

*Note: TOS, Privacy, and Help links are out of scope for this task.*
