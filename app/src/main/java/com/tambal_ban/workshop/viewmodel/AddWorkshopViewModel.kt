package com.tambal_ban.workshop.viewmodel
import com.tambal_ban.workshop.ui.*
import com.tambal_ban.workshop.viewmodel.*
import com.tambal_ban.workshop.data.*

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.tambal_ban.TambalBanApp
import com.tambal_ban.workshop.data.WorkshopSubmission
import com.tambal_ban.auth.data.AuthRepository
import com.tambal_ban.workshop.data.WorkshopRepository
import com.tambal_ban.core.location.LocationService
import kotlinx.coroutines.launch

class AddWorkshopViewModel(application: Application) : AndroidViewModel(application) {

    private val workshopRepository: WorkshopRepository = (application as TambalBanApp).workshopRepository
    private val authRepository: AuthRepository = (application as TambalBanApp).authRepository
    private val locationService: LocationService = LocationService(application)

    private val _formState = MutableLiveData<AddWorkshopFormState>(AddWorkshopFormState())
    val formState: LiveData<AddWorkshopFormState> = _formState

    private val _submissionResult = MutableLiveData<Result<Unit>>()
    val submissionResult: LiveData<Result<Unit>> = _submissionResult

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _isLoggedIn = MutableLiveData<Boolean>()
    val isLoggedIn: LiveData<Boolean> = _isLoggedIn

    private val _formErrors = MutableLiveData<Map<String, String>>()
    val formErrors: LiveData<Map<String, String>> = _formErrors

    init {
        _isLoggedIn.value = authRepository.isLoggedIn()
    }

    fun addWorkshop(
        name: String,
        address: String,
        city: String,
        lat: Double,
        lon: Double,
        phone: String,
        province: String? = null,
        openingHours: String? = null,
        imageUri: Uri? = null
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            val userId = authRepository.getUserId()
            val submission = WorkshopSubmission(
                name = name,
                address = address,
                city = city,
                lat = lat,
                lon = lon,
                phone = phone,
                province = province?.takeIf { it.isNotBlank() },
                openingHours = openingHours?.takeIf { it.isNotBlank() }
            )
            val result = workshopRepository.addWorkshop(submission, imageUri, userId, getApplication())
            _submissionResult.value = result.map { Unit }
            _isLoading.value = false
        }
    }

    fun checkAuth() {
        _isLoggedIn.value = authRepository.isLoggedIn()
    }

    fun updateFormField(field: String, value: Any) {
        val current = _formState.value ?: AddWorkshopFormState()
        val updated = when (field) {
            "name" -> current.copy(name = value as String)
            "address" -> current.copy(address = value as String)
            "city" -> current.copy(city = value as String)
            "phone" -> current.copy(phone = value as String)
            "province" -> current.copy(province = value as String)
            "openingHours" -> current.copy(openingHours = value as String)
            "lat" -> current.copy(lat = (value as String).toDoubleOrNull() ?: current.lat)
            "lon" -> current.copy(lon = (value as String).toDoubleOrNull() ?: current.lon)
            "selectedImageUri" -> current.copy(selectedImageUri = value as? Uri)
            else -> current
        }
        _formState.value = updated
    }

    fun fetchCurrentLocation() {
        val current = _formState.value ?: AddWorkshopFormState()
        _formState.value = current.copy(isLoadingLocation = true, locationError = null)

        viewModelScope.launch {
            try {
                val location = locationService.getLastKnownLocation()
                if (location != null) {
                    _formState.value = _formState.value?.copy(
                        lat = location.latitude,
                        lon = location.longitude,
                        isLoadingLocation = false
                    )
                } else {
                    _formState.value = _formState.value?.copy(
                        isLoadingLocation = false,
                        locationError = "Lokasi tidak tersedia"
                    )
                }
            } catch (e: Exception) {
                _formState.value = _formState.value?.copy(
                    isLoadingLocation = false,
                    locationError = e.message ?: "Lokasi tidak tersedia"
                )
            }
        }
    }

    fun validateForm(form: AddWorkshopFormState): Boolean {
        val errors = mutableMapOf<String, String>()

        if (form.name.isBlank()) {
            errors["name"] = "Nama wajib diisi"
        }
        if (form.address.isBlank()) {
            errors["address"] = "Alamat wajib diisi"
        }
        if (form.city.isBlank()) {
            errors["city"] = "Kota wajib diisi"
        }
        if (form.phone.isBlank()) {
            errors["phone"] = "Nomor telepon wajib diisi"
        } else if (!isValidPhoneNumber(form.phone)) {
            errors["phone"] = "Format nomor telepon tidak valid"
        }
        if (form.lat < -90 || form.lat > 90) {
            errors["lat"] = "Latitude harus antara -90 hingga 90"
        }
        if (form.lon < -180 || form.lon > 180) {
            errors["lon"] = "Longitude harus antara -180 hingga 180"
        }

        _formErrors.value = errors
        return errors.isEmpty()
    }

    private fun isValidPhoneNumber(phone: String): Boolean {
        return phone.matches(Regex("^[0-9+\\-()\\s]+$")) && phone.length >= 10
    }

    fun submitWorkshop(form: AddWorkshopFormState) {
        if (!validateForm(form)) {
            _submissionResult.value = Result.failure(Exception("Mohon isi semua field yang diperlukan dengan benar"))
            return
        }

        val userId = authRepository.getUserId() ?: return
        _isLoading.value = true

        viewModelScope.launch {
            try {
                val submission = WorkshopSubmission(
                    name = form.name,
                    address = form.address,
                    city = form.city,
                    lat = form.lat,
                    lon = form.lon,
                    phone = form.phone,
                    province = form.province.takeIf { it.isNotBlank() },
                    openingHours = form.openingHours.takeIf { it.isNotBlank() }
                )
                workshopRepository.addWorkshop(submission, form.selectedImageUri, userId, getApplication())
                _submissionResult.value = Result.success(Unit)
                _formState.value = AddWorkshopFormState()  // Reset form
            } catch (e: Exception) {
                _submissionResult.value = Result.failure(e)
            } finally {
                _isLoading.value = false
            }
        }
    }
}
