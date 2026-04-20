package com.tambal_ban.ui.auth

import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.lifecycle.viewModelScope
import com.google.android.material.snackbar.Snackbar
import com.tambal_ban.R
import com.tambal_ban.TambalBanApp
import com.tambal_ban.data.repository.AuthRepository
import com.tambal_ban.databinding.ActivityForgotPasswordBinding
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ForgotPasswordActivity : AppCompatActivity() {

    private lateinit var binding: ActivityForgotPasswordBinding
    private val viewModel: ForgotPasswordViewModel by viewModels {
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return ForgotPasswordViewModel((application as TambalBanApp).authRepository) as T
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityForgotPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupInputs()
        setupButtons()
        setupObservers()
    }

    private fun setupInputs() {
        binding.etEmail.doAfterTextChanged {
            viewModel.onEmailChanged(it.toString())
        }
    }

    private fun setupButtons() {
        binding.btnBack.setOnClickListener { finish() }
        binding.btnSendRecovery.setOnClickListener { viewModel.sendRecovery() }
    }

    private fun setupObservers() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.validationState.collect { state ->
                    binding.tilEmail.error = state.emailError?.let { getString(it) }
                    binding.btnSendRecovery.isEnabled = state.isFormValid
                }
            }
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.authState.collect { state ->
                    handleAuthState(state)
                }
            }
        }
    }

    private fun handleAuthState(state: AuthState) {
        when (state) {
            is AuthState.Loading -> {
                binding.progressBar.visibility = View.VISIBLE
                binding.btnSendRecovery.isEnabled = false
            }
            is AuthState.Success -> {
                binding.progressBar.visibility = View.GONE
                Snackbar.make(binding.root, state.messageResId, Snackbar.LENGTH_LONG).show()
                binding.root.postDelayed({ finish() }, 2000)
            }
            is AuthState.Error -> {
                binding.progressBar.visibility = View.GONE
                binding.btnSendRecovery.isEnabled = true
                Snackbar.make(binding.root, state.messageResId, Snackbar.LENGTH_LONG).show()
            }
            is AuthState.Idle -> {
                binding.progressBar.visibility = View.GONE
            }
        }
    }
}

class ForgotPasswordViewModel(private val authRepository: AuthRepository) : ViewModel() {

    private val _validationState = MutableStateFlow(FormValidationState())
    val validationState: StateFlow<FormValidationState> = _validationState.asStateFlow()

    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    private var email = ""

    fun onEmailChanged(newEmail: String) {
        email = newEmail
        val emailError = when {
            email.isEmpty() -> null
            !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() -> R.string.error_email_invalid
            else -> null
        }
        _validationState.value = FormValidationState(
            emailError = emailError,
            isFormValid = emailError == null && email.isNotEmpty()
        )
    }

    fun sendRecovery() {
        if (!_validationState.value.isFormValid) return

        viewModelScope.launch {
            _authState.value = AuthState.Loading
            val result = authRepository.recover(email)
            if (result.isSuccess) {
                _authState.value = AuthState.Success(R.string.recovery_link_sent)
            } else {
                _authState.value = AuthState.Error(R.string.error_recovery_failed, ErrorType.UNKNOWN)
            }
        }
    }
}
