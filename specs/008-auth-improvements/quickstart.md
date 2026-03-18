# Feature Quickstart: Update Login and Register Features

## Branches & Environments
- **Feature Branch**: `008-auth-improvements`
- **Related Specs**: `/specs/008-auth-improvements/`

## Development Guide
1. Run `./gradlew app:assembleDebug` to compile Android UI and ViewModel components.
2. The core UI work happens in `LoginActivity`, `RegisterActivity`, and the new `ForgotPasswordActivity`.
3. Validation flow leverages `LoginViewModel` and `RegisterViewModel` via LiveData/StateFlow.
4. Snackbars are explicitly launched from UI observer logic.

**Offline Simulation**: Turn off device Wi-Fi/Data during the emulator session, attempt to log in/register, and verify the correct string is shown.
