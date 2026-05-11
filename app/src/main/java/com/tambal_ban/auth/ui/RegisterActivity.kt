package com.tambal_ban.auth.ui
import com.tambal_ban.auth.ui.* 
import com.tambal_ban.auth.viewmodel.* 
import com.tambal_ban.auth.data.* 

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.tambal_ban.databinding.ActivityRegisterBinding
import com.tambal_ban.map.ui.MainActivity
import com.tambal_ban.core.utils.AuthErrorMapper

/**
 * Activity for user registration.
 */
class RegisterActivity : com.tambal_ban.core.ui.BaseActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private val viewModel: RegisterViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)
        applySafeArea(binding.root)

        setupButtons()
        setupObservers()
    }

    private fun setupButtons() {
        binding.btnRegister.setOnClickListener {
            val name = binding.etName.text
            val email = binding.etEmail.text
            val password = binding.etPassword.text

            viewModel.validateAndRegister(name, email, password)
        }

        binding.tvLogin.setOnClickListener {
            finish()
        }
    }

    private fun setupObservers() {
        viewModel.validationError.observe(this) { error ->
            if (error != null) {
                when (error.first) {
                    "name" -> binding.etName.setError(error.second)
                    "email" -> binding.etEmail.setError(error.second)
                    "password" -> binding.etPassword.setError(error.second)
                }
            } else {
                binding.etName.setError(null)
                binding.etEmail.setError(null)
                binding.etPassword.setError(null)
            }
        }

        viewModel.registerResult.observe(this) { result ->
            if (result.isSuccess) {
                Toast.makeText(this, getString(com.tambal_ban.R.string.register_success), Toast.LENGTH_SHORT).show()
                val intent = Intent(this, MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
            } else {
                val error = result.exceptionOrNull()
                val message = error?.let { AuthErrorMapper.map(it) } ?: getString(com.tambal_ban.R.string.register_error_general)
                
                // Show error message
                Toast.makeText(this, message, Toast.LENGTH_LONG).show()
                
                // Optional: Map specific errors to fields
                if (message.contains("email", ignoreCase = true)) {
                    binding.etEmail.setError(message)
                } else if (message.contains("password", ignoreCase = true)) {
                    binding.etPassword.setError(message)
                }
            }
        }

        viewModel.isLoading.observe(this) { isLoading ->
            binding.btnRegister.setLoading(isLoading)
            binding.etName.isEnabled = !isLoading
            binding.etEmail.isEnabled = !isLoading
            binding.etPassword.isEnabled = !isLoading
        }
    }
}
