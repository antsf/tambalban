package com.tambal_ban.auth.ui
import com.tambal_ban.auth.ui.* 
import com.tambal_ban.auth.viewmodel.* 
import com.tambal_ban.auth.data.* 

import android.net.Uri
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import coil.load
import com.tambal_ban.R
import com.tambal_ban.databinding.ActivityEditProfileBinding
import androidx.core.content.FileProvider
import com.google.android.material.bottomsheet.BottomSheetDialog
import java.io.File
import com.tambal_ban.auth.viewmodel.ProfileViewModel

/**
 * US3 & US4: Edit Profile Activity.
 */
class EditProfileActivity : com.tambal_ban.core.ui.BaseActivity() {

    private lateinit var binding: ActivityEditProfileBinding
    private val viewModel: ProfileViewModel by viewModels()

    private val pickMedia = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        if (uri != null) {
            onImageSelected(uri)
        }
    }

    private var cameraImageUri: Uri? = null
    private val takePhoto = registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
        if (success) {
            cameraImageUri?.let { onImageSelected(it) }
        }
    }

    private val requestCameraPermission = registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
        if (granted) {
            launchCamera()
        } else {
            Toast.makeText(this, "Izin kamera diperlukan", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        applySafeArea(binding.root)

        setupObservers()
        setupListeners()
        
        // Pre-fill email from Auth immediately
        viewModel.userEmail?.let {
            binding.etEmail.setText(it)
            binding.etEmail.isEnabled = false
        }

        viewModel.getProfile()
    }

    private fun setupObservers() {
        viewModel.profile.observe(this) { profile ->
            if (profile != null) {
                binding.etFullName.setText(profile.fullName ?: "-")
                binding.etEmail.setText(profile.email ?: "-")
                binding.etEmail.isEnabled = false // Email is read-only
                binding.etPhone.setText(formatPhoneNumber(profile.phone))
                
                if (!profile.avatarUrl.isNullOrEmpty()) {
                    binding.avatarView.loadAvatar(profile.avatarUrl)
                }
            }
        }

        viewModel.isLoading.observe(this) { isLoading ->
            binding.btnSave.setLoading(isLoading)
        }

        viewModel.isUploading.observe(this) { isUploading ->
            binding.avatarView.setLoading(isUploading)
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
        binding.btnBack.setOnClickListener {
            finish()
        }

        binding.btnSave.setOnClickListener {
            validateAndSave()
        }

        binding.avatarView.setOnEditClickListener {
            showImageSourceDialog()
        }
    }

    private fun showImageSourceDialog() {
        val dialog = BottomSheetDialog(this)
        val view = layoutInflater.inflate(R.layout.dialog_choose_image_source, null)
        
        view.findViewById<View>(R.id.btnCamera).setOnClickListener {
            checkCameraPermissionAndLaunch()
            dialog.dismiss()
        }
        
        view.findViewById<View>(R.id.btnGallery).setOnClickListener {
            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
            dialog.dismiss()
        }
        
        dialog.setContentView(view)
        dialog.show()
    }

    private fun checkCameraPermissionAndLaunch() {
        if (androidx.core.content.ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) 
            == android.content.pm.PackageManager.PERMISSION_GRANTED) {
            launchCamera()
        } else {
            requestCameraPermission.launch(android.Manifest.permission.CAMERA)
        }
    }

    private fun launchCamera() {
        val photoFile = File(getExternalFilesDir(android.os.Environment.DIRECTORY_PICTURES), "avatar_${System.currentTimeMillis()}.jpg")
        cameraImageUri = FileProvider.getUriForFile(this, "${packageName}.fileprovider", photoFile)
        takePhoto.launch(cameraImageUri)
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
