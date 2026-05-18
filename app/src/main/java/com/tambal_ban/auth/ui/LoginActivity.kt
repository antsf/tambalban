package com.tambal_ban.auth.ui
import com.tambal_ban.auth.ui.* 
import com.tambal_ban.auth.viewmodel.* 
import com.tambal_ban.auth.data.* 

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.tambal_ban.R
import com.tambal_ban.core.utils.AnalyticsHelper
import com.tambal_ban.databinding.ActivityLoginBinding

class LoginActivity : com.tambal_ban.core.ui.BaseActivity() {

    private lateinit var binding: ActivityLoginBinding
    private val viewModel: LoginViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        applySafeArea(binding.root)

        setupButtons()
        setupObservers()
    }

    private fun setupButtons() {
        binding.btnLogin.setOnClickListener {
            val email = binding.etEmail.text
            val password = binding.etPassword.text
            
            // Clear previous errors
            binding.etEmail.setError(null)
            binding.etPassword.setError(null)

            if (validateInputs(email, password)) {
                viewModel.login(email, password)
            }
        }

        binding.tvRegister.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }

        binding.tvForgot.setOnClickListener {
            Toast.makeText(this, getString(R.string.info_forgot_password), Toast.LENGTH_SHORT).show()
        }
    }

    private fun validateInputs(email: String, password: String): Boolean {
        var isValid = true
        if (email.isEmpty()) {
            binding.etEmail.setError(getString(R.string.register_error_email_required))
            isValid = false
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.etEmail.setError(getString(R.string.register_error_email_invalid))
            isValid = false
        }

        if (password.isEmpty()) {
            binding.etPassword.setError(getString(R.string.register_error_password_required))
            isValid = false
        } else if (password.length < 6) {
            binding.etPassword.setError(getString(R.string.login_error_password_short))
            isValid = false
        }
        return isValid
    }

    private fun setupObservers() {
        viewModel.loginResult.observe(this) { result ->
            if (result.isSuccess) {
                AnalyticsHelper.logLogin()
                Toast.makeText(this, getString(R.string.welcome_back), Toast.LENGTH_SHORT).show()
                finish()
            } else {
                val error = result.exceptionOrNull()?.message ?: getString(R.string.error_auth_failed)
                if (error.contains("email", ignoreCase = true)) {
                    binding.etEmail.setError(error)
                } else if (error.contains("password", ignoreCase = true)) {
                    binding.etPassword.setError(error)
                } else {
                    Toast.makeText(this, error, Toast.LENGTH_LONG).show()
                }
            }
        }

        viewModel.isLoading.observe(this) { isLoading ->
            binding.btnLogin.setLoading(isLoading)
        }
    }
}
