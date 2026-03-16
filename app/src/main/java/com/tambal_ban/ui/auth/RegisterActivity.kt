package com.tambal_ban.ui.auth

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.tambal_ban.databinding.ActivityRegisterBinding

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private val viewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupButtons()
        setupObservers()
    }

    private fun setupButtons() {
        binding.btnRegister.setOnClickListener {
            val fullName = binding.etFullName.text.toString().trim()
            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()

            if (fullName.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty()) {
                if (password.length < 6) {
                    Toast.makeText(this, "Password minimal 6 karakter", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
                viewModel.register(email, password, fullName)
            } else {
                Toast.makeText(this, "Harap lengkapi semua data", Toast.LENGTH_SHORT).show()
            }
        }

        binding.tvAlreadyHaveAccount.setOnClickListener { finish() }
    }

    private fun setupObservers() {
        viewModel.authResult.observe(this) { result ->
            if (result == null) return@observe

            if (result.isSuccess) {
                Toast.makeText(this, "Pendaftaran berhasil! Selamat datang.", Toast.LENGTH_SHORT)
                        .show()
                finish()
            } else {
                Toast.makeText(
                                this,
                                "Gagal daftar: ${result.exceptionOrNull()?.message}",
                                Toast.LENGTH_LONG
                        )
                        .show()
            }
        }

        viewModel.isLoading.observe(this) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
            binding.btnRegister.isEnabled = !isLoading
        }
    }
}
