package com.tambal_ban.ui.auth

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.tambal_ban.R
import com.tambal_ban.TambalBanApp
import com.tambal_ban.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class RegisterViewModel(application: Application) : AndroidViewModel(application) {

    private val authRepository: AuthRepository = (application as TambalBanApp).authRepository

    private val _validationState = MutableStateFlow(FormValidationState())
    val validationState: StateFlow<FormValidationState> = _validationState.asStateFlow()

    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    private var email = ""
    private var password = ""
    private var fullName = ""
    private var termsAccepted = false

    fun onEmailChanged(newEmail: String) {
        email = newEmail
        validateForm()
    }

    fun onPasswordChanged(newPassword: String) {
        password = newPassword
        validateForm()
    }

    fun onFullNameChanged(newName: String) {
        fullName = newName
        validateForm()
    }

    fun onTermsChanged(accepted: Boolean) {
        termsAccepted = accepted
        validateForm()
    }

    private fun validateForm() {
        val emailError = when {
            email.isEmpty() -> null
            !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() -> R.string.error_email_invalid
            else -> null
        }

        val (passwordError, strength) = validatePassword(password)

        _validationState.update {
            it.copy(
                emailError = emailError,
                passwordError = passwordError,
                passwordStrength = strength,
                isTermsAccepted = termsAccepted,
                isFormValid = emailError == null && passwordError == null &&
                             email.isNotEmpty() && password.isNotEmpty() &&
                             fullName.isNotEmpty() && termsAccepted
            )
        }
    }

    private fun validatePassword(password: String): Pair<Int?, PasswordStrength> {
        if (password.isEmpty()) return null to PasswordStrength.NONE

        val hasUpper = password.any { it.isUpperCase() }
        val hasLower = password.any { it.isLowerCase() }
        val hasDigit = password.any { it.isDigit() }
        val isLongEnough = password.length >= 8

        val strength = when {
            !isLongEnough -> PasswordStrength.WEAK
            hasUpper && hasLower && hasDigit -> PasswordStrength.STRONG
            (hasUpper && hasLower) || (hasUpper && hasDigit) || (hasLower && hasDigit) -> PasswordStrength.MEDIUM
            else -> PasswordStrength.WEAK
        }

        val error = when {
            !isLongEnough -> R.string.error_password_too_short
            !hasUpper -> R.string.error_password_no_upper
            !hasLower -> R.string.error_password_no_lower
            !hasDigit -> R.string.error_password_no_digit
            else -> null
        }

        return error to strength
    }

    fun register() {
        if (!_validationState.value.isFormValid) return

        viewModelScope.launch {
            _authState.value = AuthState.Loading
            val result = authRepository.register(email, password, fullName)
            if (result.isSuccess) {
                _authState.value = AuthState.Success(R.string.auth_success_register)
            } else {
                val error = result.exceptionOrNull()
                val messageResId = when {
                    error?.message?.contains("already exists", ignoreCase = true) == true -> R.string.error_email_exists
                    error?.message?.contains("network", ignoreCase = true) == true -> R.string.error_network_offline
                    else -> R.string.error_unknown
                }
                _authState.value = AuthState.Error(messageResId, ErrorType.UNKNOWN)
            }
        }
    }
}
