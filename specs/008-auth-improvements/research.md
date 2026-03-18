# Phase 0: Research

**Feature Focus**: Improving UX for Authentication with Material Design 3 and Android ViewModels.

## 1. Material Design 3 Input Validation (`TextInputLayout`)
- **Decision**: Use `TextInputLayout` with `app:errorEnabled` for all form fields.
- **Rationale**: Built-in support for displaying inline error messages below text fields with proper animation, spacing, and accessibility. Also handles end-icon toggling (for password visibility) cleanly.
- **Alternatives**: Custom error `TextViews` below each field (more code, less standard).

## 2. Password Strength Indicator in Android
- **Decision**: Implement a custom view component (e.g., `ProgressBar` with an accompanying text label) below the password field, reacting to `addTextChangedListener` (debounce applied) logic in the `RegisterViewModel`.
- **Rationale**: `TextInputLayout` lacks native password strength capability. Using a progress bar gives the required visual feedback efficiently without complex 3rd party libs.
- **Alternatives**: Importing external libraries like `MaterialEditText` (violates Simplicity First).

## 3. Form State Management (MVVM)
- **Decision**: Use Kotlin `StateFlow` to manage the UI state of the authentication screens. Combine flows for individual validations (email valid, password valid, confirm match) into a single form evaluation state.
- **Rationale**: Represents streams of data explicitly, handles configuration changes securely, and cleanly decouples logic from Activities.
- **Alternatives**: `LiveData` (legacy but still acceptable; `StateFlow` preferred for advanced stream combination), RxJava (too heavy, violates Simplicity First).

## 4. 'Forgot Password' Flow using Supabase
- **Decision**: Trigger Supabase auth `resetPasswordForEmail()` method in the new `ForgotPasswordActivity`. Note: The user will get a deep-link email; deep link handling may need future implementation if not already set, but the specified "UI + basic flow" focuses on sending the email in this feature update.
- **Rationale**: The standard Supabase way for retrieving passwords.

## 5. Offline Safety and Snackbars
- **Decision**: Check `ConnectivityManager` before submission, and launch `Snackbar` (with `Snackbar.make()`) on error events triggered from ViewModel sealed class states.
- **Rationale**: Standard Android approach for transient, offline-friendly notifications. Doesn't interrupt user flow like a Dialog might but provides more context than a Toast.
