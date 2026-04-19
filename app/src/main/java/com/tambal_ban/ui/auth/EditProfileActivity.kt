package com.tambal_ban.ui.auth

import android.net.Uri
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import coil.load
import com.tambal_ban.R
import com.tambal_ban.databinding.ActivityEditProfileBinding
import com.tambal_ban.viewmodel.ProfileViewModel

/**
 * US3 & US4: Edit Profile Activity.
 */
class EditProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditProfileBinding
    private val viewModel: ProfileViewModel by viewModels()

    private val pickMedia = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        if (uri != null) {
            onImageSelected(uri)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupObservers()
        setupListeners()
        
        viewModel.getProfile()
    }

    private fun setupObservers() {
        viewModel.profile.observe(this) { profile ->
            if (profile != null) {
                binding.etFullName.setText(profile.fullName)
                binding.etEmail.setText(profile.email)
                binding.etPhone.setText(formatPhoneNumber(profile.phone))
                
                if (!profile.avatarUrl.isNullOrEmpty()) {
                    binding.avatarView.loadAvatar(profile.avatarUrl)
                }
            }
        }

        viewModel.isUpdateSuccess.observe(this) { success ->
            if (success) {
                Toast.makeText(this, "Profil berhasil diperbarui", Toast.LENGTH_SHORT).show()
                finish()
            }
        }

        viewModel.error.observe(this) { error ->
            if (error != null) {
                Toast.makeText(this, error, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupListeners() {
        binding.btnSave.setOnClickListener {
            validateAndSave()
        }

        binding.avatarView.setOnEditClickListener {
            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }
    }

    private fun validateAndSave() {
        val name = binding.etFullName.text.trim()
        val email = binding.etEmail.text.trim()
        val phone = binding.etPhone.text.trim()

        if (name.isEmpty()) {
            binding.etFullName.setError("Nama tidak boleh kosong")
            return
        }

        if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.etEmail.setError("Masukkan email yang valid")
            return
        }

        viewModel.updateProfile(name, phone)
    }

    private fun onImageSelected(uri: Uri) {
        // T014: Upload avatar logic
        val inputStream = contentResolver.openInputStream(uri)
        val bytes = inputStream?.readBytes()
        if (bytes != null) {
            val mimeType = contentResolver.getType(uri) ?: "image/png"
            viewModel.uploadAvatar(bytes, mimeType)
        }
    }

    private fun formatPhoneNumber(phone: String?): String {
        if (phone == null) return ""
        return when {
            phone.startsWith("+62") -> "0" + phone.substring(3)
            phone.startsWith("62") -> "0" + phone.substring(2)
            else -> phone
        }
    }
}
