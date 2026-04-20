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

class LoginViewModel(application: Application) : AndroidViewModel(application) {

    private val authRepository: AuthRepository = (application as TambalBanApp).authRepository

    private val _validationState = MutableStateFlow(FormValidationState())
    val validationState: StateFlow<FormValidationState> = _validationState.asStateFlow()

    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    private var email = authRepository.getSavedEmail() ?: ""
    private var password = ""

    val savedEmail = authRepository.getSavedEmail()

    fun onEmailChanged(newEmail: String) {
        email = newEmail
        validateForm()
    }

    fun onPasswordChanged(newPassword: String) {
        password = newPassword
        validateForm()
    }

    private fun validateForm() {
        val emailError = when {
            email.isEmpty() -> null
            !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() -> R.string.error_email_invalid
            else -> null
        }

        val passwordError = when {
            password.isEmpty() -> null
            password.length < 8 -> R.string.error_password_too_short
            else -> null
        }

        _validationState.update {
            it.copy(
                emailError = emailError,
                passwordError = passwordError,
                isFormValid = emailError == null && passwordError == null &&
                             email.isNotEmpty() && password.isNotEmpty()
            )
        }
    }

    fun login() {
        if (!_validationState.value.isFormValid) return

        viewModelScope.launch {
            _authState.value = AuthState.Loading
            authRepository.saveEmail(email)
            val result = authRepository.login(email, password)
            if (result.isSuccess) {
                _authState.value = AuthState.Success(R.string.auth_success_login)
            } else {
                val error = result.exceptionOrNull()
                val messageResId = when {
                    error?.message?.contains("invalid", ignoreCase = true) == true -> R.string.error_invalid_credentials
                    error?.message?.contains("network", ignoreCase = true) == true -> R.string.error_network_offline
                    else -> R.string.error_unknown
                }
                _authState.value = AuthState.Error(messageResId, ErrorType.INVALID_CREDENTIALS)
            }
        }
    }
}
