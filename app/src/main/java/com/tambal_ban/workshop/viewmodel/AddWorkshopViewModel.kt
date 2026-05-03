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
import kotlinx.coroutines.launch

class AddWorkshopViewModel(application: Application) : AndroidViewModel(application) {

    private val workshopRepository: WorkshopRepository = (application as TambalBanApp).workshopRepository
    private val authRepository: AuthRepository = (application as TambalBanApp).authRepository

    private val _submissionResult = MutableLiveData<Result<Unit>>()
    val submissionResult: LiveData<Result<Unit>> = _submissionResult

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _isLoggedIn = MutableLiveData<Boolean>()
    val isLoggedIn: LiveData<Boolean> = _isLoggedIn

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
}
