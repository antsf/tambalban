# Phase 1: Data Model and State Transitions

- **Entities**: Because we're using Supabase Auth, user models are inherently managed by the SDK. However, our internal state representations in the ViewModels require specific state classes.

## Password Requirements Validation
- `minLength`: 8
- `hasUppercase`: Boolean
- `hasLowercase`: Boolean
- `hasDigit`: Boolean
- `hasSpecialChar`: Boolean

## Validated Fields (Data Classes representing UI State)
```kotlin
data class FormValidationState(
    val emailError: String? = null,
    val passwordError: String? = null,
    val passwordStrength: PasswordStrength = PasswordStrength.NONE,
    val confirmPasswordError: String? = null,
    val isTermsAccepted: Boolean = false,
    val isFormValid: Boolean = false
)

enum class PasswordStrength {
    NONE, WEAK, MEDIUM, STRONG
}
```

## Authentication Result States
```kotlin
sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    data class Success(val message: String) : AuthState()
    data class Error(val message: String, val type: ErrorType) : AuthState()
}

enum class ErrorType {
    NETWORK_OFFLINE,
    INVALID_CREDENTIALS,
    EMAIL_EXISTS,
    UNKNOWN
}
```

## Storage Keys
- `PREF_USER_EMAIL`: Stored user email.
