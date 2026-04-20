package com.tambal_ban.ui.auth

import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.android.material.snackbar.Snackbar
import com.tambal_ban.R
import com.tambal_ban.databinding.ActivityRegisterBinding
import kotlinx.coroutines.launch

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private val viewModel: RegisterViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupInputs()
        setupButtons()
        setupObservers()
    }

    private fun setupInputs() {
        binding.etFullName.doAfterTextChanged {
            viewModel.onFullNameChanged(it.toString())
        }
        binding.etEmail.doAfterTextChanged {
            viewModel.onEmailChanged(it.toString())
        }
        binding.etPassword.doAfterTextChanged {
            viewModel.onPasswordChanged(it.toString())
        }
        binding.cbTerms.setOnCheckedChangeListener { _, isChecked ->
            viewModel.onTermsChanged(isChecked)
        }

        binding.etPassword.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                viewModel.register()
                true
            } else {
                false
            }
        }
    }

    private fun setupButtons() {
        binding.btnRegister.setOnClickListener {
            viewModel.register()
        }

        binding.tvAlreadyHaveAccount.setOnClickListener { finish() }
    }

    private fun setupObservers() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.validationState.collect { state ->
                    binding.tilEmail.error = state.emailError?.let { getString(it) }
                    binding.tilPassword.error = state.passwordError?.let { getString(it) }
                    binding.btnRegister.isEnabled = state.isFormValid

                    updatePasswordStrength(state.passwordStrength)
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

    private fun updatePasswordStrength(strength: PasswordStrength) {
        val (colorRes, progress) = when (strength) {
            PasswordStrength.NONE -> 0 to 0
            PasswordStrength.WEAK -> R.color.error to 33
            PasswordStrength.MEDIUM -> R.color.warning to 66
            PasswordStrength.STRONG -> R.color.success to 100
        }

        if (colorRes != 0) {
            binding.passwordStrengthIndicator.visibility = View.VISIBLE
            binding.passwordStrengthIndicator.setIndicatorColor(ContextCompat.getColor(this, colorRes))
            binding.passwordStrengthIndicator.progress = progress
        } else {
            binding.passwordStrengthIndicator.visibility = View.INVISIBLE
        }
    }

    private fun handleAuthState(state: AuthState) {
        when (state) {
            is AuthState.Loading -> {
                binding.progressBar.visibility = View.VISIBLE
                binding.btnRegister.isEnabled = false
            }
            is AuthState.Success -> {
                binding.progressBar.visibility = View.GONE
                Snackbar.make(binding.root, state.messageResId, Snackbar.LENGTH_SHORT).show()
                binding.root.postDelayed({ finish() }, 1000)
            }
            is AuthState.Error -> {
                binding.progressBar.visibility = View.GONE
                binding.btnRegister.isEnabled = true
                Snackbar.make(binding.root, state.messageResId, Snackbar.LENGTH_LONG).show()
            }
            is AuthState.Idle -> {
                binding.progressBar.visibility = View.GONE
            }
        }
    }
}
