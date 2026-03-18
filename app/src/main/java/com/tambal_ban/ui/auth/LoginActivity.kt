package com.tambal_ban.ui.auth

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.android.material.snackbar.Snackbar
import com.tambal_ban.databinding.ActivityLoginBinding
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private val viewModel: LoginViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupInputs()
        setupButtons()
        setupObservers()
    }

    private fun setupInputs() {
        viewModel.savedEmail?.let {
            binding.etEmail.setText(it)
            viewModel.onEmailChanged(it)
        }
        binding.etEmail.doAfterTextChanged {
            viewModel.onEmailChanged(it.toString())
        }
        binding.etPassword.doAfterTextChanged {
            viewModel.onPasswordChanged(it.toString())
        }

        binding.etPassword.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                viewModel.login()
                true
            } else {
                false
            }
        }
    }

    private fun setupButtons() {
        binding.btnLogin.setOnClickListener {
            viewModel.login()
        }

        binding.tvForgotPassword.setOnClickListener {
            startActivity(Intent(this, ForgotPasswordActivity::class.java))
        }

        binding.tvNoAccount.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }

    private fun setupObservers() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.validationState.collect { state ->
                    binding.tilEmail.error = state.emailError?.let { getString(it) }
                    binding.tilPassword.error = state.passwordError?.let { getString(it) }
                    binding.btnLogin.isEnabled = state.isFormValid
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
                binding.btnLogin.isEnabled = false
            }
            is AuthState.Success -> {
                binding.progressBar.visibility = View.GONE
                setResult(RESULT_OK)
                finish()
            }
            is AuthState.Error -> {
                binding.progressBar.visibility = View.GONE
                binding.btnLogin.isEnabled = true
                Snackbar.make(binding.root, state.messageResId, Snackbar.LENGTH_LONG).show()
            }
            is AuthState.Idle -> {
                binding.progressBar.visibility = View.GONE
            }
        }
    }
}
