package com.tambal_ban.ui.auth

data class FormValidationState(
    val emailError: Int? = null,
    val passwordError: Int? = null,
    val passwordStrength: PasswordStrength = PasswordStrength.NONE,
    val isTermsAccepted: Boolean = false,
    val isFormValid: Boolean = false
)

enum class PasswordStrength {
    NONE, WEAK, MEDIUM, STRONG
}

sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    data class Success(val messageResId: Int) : AuthState()
    data class Error(val messageResId: Int, val type: ErrorType) : AuthState()
}

enum class ErrorType {
    NETWORK_OFFLINE,
    INVALID_CREDENTIALS,
    EMAIL_EXISTS,
    UNKNOWN
}
