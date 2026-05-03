package com.tambal_ban.workshop.ui

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.ViewModelProvider
import com.tambal_ban.R
import com.tambal_ban.core.ui.BaseActivity
import com.tambal_ban.databinding.ActivityAddWorkshopBinding
import com.tambal_ban.workshop.viewmodel.AddWorkshopViewModel
import com.google.android.material.snackbar.Snackbar
import coil.load

class AddWorkshopActivity : BaseActivity() {

    private lateinit var binding: ActivityAddWorkshopBinding
    private lateinit var viewModel: AddWorkshopViewModel

    private val photoPickerLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let {
            viewModel.updateFormField("selectedImageUri", it)
            binding.ivPhotoPreview.load(it)
            binding.ivPhotoPreview.visibility = android.view.View.VISIBLE
        }
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
        binding.toolbar.apply {
            title = getString(R.string.screen_add_workshop)
            setNavigationOnClickListener { finish() }
        }
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
            binding.tilLat.editText?.setText(formState.lat.toString())
            binding.tilLon.editText?.setText(formState.lon.toString())

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
            binding.btnSubmit.isEnabled = !isLoading
            binding.btnCancel.isEnabled = !isLoading
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

        binding.btnAddPhoto.setOnClickListener {
            photoPickerLauncher.launch("image/*")
        }

        binding.btnSubmit.setOnClickListener {
            viewModel.formState.value?.let { form ->
                viewModel.submitWorkshop(form)
            }
        }

        binding.btnCancel.setOnClickListener {
            finish()
        }
    }
}
