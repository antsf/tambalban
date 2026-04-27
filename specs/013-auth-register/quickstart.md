# Quickstart: Testing User Registration

**Feature**: User Registration (`013-auth-register`)

## Setup
1. Ensure `SUPABASE_URL` and `SUPABASE_KEY` are valid in `BuildConfig`.
2. Clear app data or use a new email address.

## Manual Test Flow
1. Navigate to the Login screen.
2. Click "Register" link.
3. Fill Name: "Test User", Email: "test@example.com", Password: "Password123".
4. Toggle visibility to verify 20dp icon and state.
5. Click "Register".
6. Verify redirection to Map screen.

## Automated Test
`./gradlew testDebugUnitTest --tests com.tambal_ban.viewmodel.auth.RegisterViewModelTest`
