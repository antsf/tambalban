package com.tambal_ban.ui.auth

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.tambal_ban.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private val viewModel: LoginViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

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
            Toast.makeText(this, "Fitur lupa kata sandi akan segera hadir", Toast.LENGTH_SHORT).show()
        }
    }

    private fun validateInputs(email: String, password: String): Boolean {
        var isValid = true
        if (email.isEmpty()) {
            binding.etEmail.setError("Email wajib diisi")
            isValid = false
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.etEmail.setError("Format email tidak valid")
            isValid = false
        }

        if (password.isEmpty()) {
            binding.etPassword.setError("Kata sandi wajib diisi")
            isValid = false
        } else if (password.length < 6) {
            binding.etPassword.setError("Kata sandi minimal 6 karakter")
            isValid = false
        }
        return isValid
    }

    private fun setupObservers() {
        viewModel.loginResult.observe(this) { result ->
            if (result.isSuccess) {
                Toast.makeText(this, "Selamat datang kembali!", Toast.LENGTH_SHORT).show()
                finish()
            } else {
                val error = result.exceptionOrNull()?.message ?: "Autentikasi gagal"
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
