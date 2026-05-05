package com.tambal_ban.workshop.ui

import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModelProvider
import coil.load
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.snackbar.Snackbar
import com.tambal_ban.R
import com.tambal_ban.core.ui.BaseActivity
import com.tambal_ban.databinding.ActivityAddWorkshopBinding
import com.tambal_ban.workshop.viewmodel.AddWorkshopViewModel
import java.io.File

class AddWorkshopActivity : BaseActivity() {

    private lateinit var binding: ActivityAddWorkshopBinding
    private lateinit var viewModel: AddWorkshopViewModel

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

    private fun onImageSelected(uri: Uri) {
        viewModel.updateFormField("selectedImageUri", uri)
        binding.ivPhotoPreview.load(uri)
        binding.ivPhotoPreview.visibility = android.view.View.VISIBLE
        binding.placeholderPhoto.visibility = android.view.View.GONE
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAddWorkshopBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this).get(AddWorkshopViewModel::class.java)

        setupToolbar()
        setupFormWiring()
        observeViewModel()
        setupButtonListeners()
    }

    private fun setupToolbar() {
        binding.toolbar.setNavigationOnClickListener { finish() }
    }

    private fun setupFormWiring() {
        binding.tilName.editText?.addTextChangedListener(createFieldWatcher("name"))
        binding.tilAddress.editText?.addTextChangedListener(createFieldWatcher("address"))
        binding.tilCity.editText?.addTextChangedListener(createFieldWatcher("city"))
        binding.tilPhone.editText?.addTextChangedListener(createFieldWatcher("phone"))
        binding.tilProvince.editText?.addTextChangedListener(createFieldWatcher("province"))
        binding.tilOpeningHours.editText?.addTextChangedListener(createFieldWatcher("openingHours"))
        binding.tilLat.editText?.addTextChangedListener(createFieldWatcher("lat"))
        binding.tilLon.editText?.addTextChangedListener(createFieldWatcher("lon"))
    }

    private fun createFieldWatcher(fieldName: String): TextWatcher {
        return object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                viewModel.updateFormField(fieldName, s.toString())
            }
        }
    }

    private fun observeViewModel() {
        viewModel.formState.observe(this) { formState ->
            // Only update if value is different to avoid infinite loops with TextWatcher
            val currentLat = binding.tilLat.editText?.text.toString()
            val newLat = formState.lat.toString()
            if (currentLat != newLat) {
                binding.tilLat.editText?.setText(newLat)
            }

            val currentLon = binding.tilLon.editText?.text.toString()
            val newLon = formState.lon.toString()
            if (currentLon != newLon) {
                binding.tilLon.editText?.setText(newLon)
            }

            binding.btnCurrentLocation.isEnabled = !formState.isLoadingLocation
            if (formState.isLoadingLocation) {
                binding.btnCurrentLocation.text = getString(R.string.getting_location)
            } else {
                binding.btnCurrentLocation.text = getString(R.string.btn_current_location)
            }

            formState.locationError?.let { error ->
                Snackbar.make(binding.root, error, Snackbar.LENGTH_SHORT).show()
            }
        }

        viewModel.isLoading.observe(this) { isLoading ->
            binding.btnSubmit.setLoading(isLoading)
        }

        viewModel.submissionResult.observe(this) { result ->
            result.onSuccess {
                Snackbar.make(binding.root, R.string.msg_submission_pending, Snackbar.LENGTH_LONG).show()
                finish()
            }
            result.onFailure { error ->
                Snackbar.make(binding.root, error.message ?: getString(R.string.submission_failed), Snackbar.LENGTH_SHORT).show()
            }
        }

        viewModel.formErrors.observe(this) { errors ->
            errors.forEach { (field, message) ->
                when (field) {
                    "name" -> binding.tilName.error = message
                    "address" -> binding.tilAddress.error = message
                    "city" -> binding.tilCity.error = message
                    "phone" -> binding.tilPhone.error = message
                    "lat" -> binding.tilLat.error = message
                    "lon" -> binding.tilLon.error = message
                }
            }
        }
    }

    private fun setupButtonListeners() {
        binding.btnCurrentLocation.setOnClickListener {
            viewModel.fetchCurrentLocation()
        }

        binding.cardAddPhoto.setOnClickListener {
            showImageSourceDialog()
        }

        binding.btnSubmit.setOnClickListener {
            viewModel.formState.value?.let { form ->
                viewModel.submitWorkshop(form)
            }
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
        val photoFile = File(getExternalFilesDir(android.os.Environment.DIRECTORY_PICTURES), "workshop_${System.currentTimeMillis()}.jpg")
        cameraImageUri = FileProvider.getUriForFile(this, "${packageName}.fileprovider", photoFile)
        takePhoto.launch(cameraImageUri)
    }
}
