package com.tambal_ban.ui.add

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.tambal_ban.TambalBanApp
import com.tambal_ban.data.model.WorkshopSubmission
import com.tambal_ban.data.repository.AuthRepository
import com.tambal_ban.data.repository.SubmissionRepository
import kotlinx.coroutines.launch

class AddWorkshopViewModel(application: Application) : AndroidViewModel(application) {

    private val submissionRepository: SubmissionRepository = (application as TambalBanApp).submissionRepository
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

    fun submitWorkshop(name: String, address: String, latitude: Double, longitude: Double, phone: String, photoBytes: ByteArray? = null) {
        viewModelScope.launch {
            _isLoading.value = true
            val userId = authRepository.getUserId()
            
            var photoUrl: String? = null
            if (photoBytes != null) {
                val fileName = "workshop_${System.currentTimeMillis()}.jpg"
                val uploadResult = submissionRepository.uploadPhoto(fileName, photoBytes)
                if (uploadResult.isSuccess) {
                    photoUrl = uploadResult.getOrNull()
                } else {
                    _submissionResult.value = Result.failure(uploadResult.exceptionOrNull() ?: Exception("Gagal mengunggah foto"))
                    _isLoading.value = false
                    return@launch
                }
            }

            val submission = WorkshopSubmission(
                name = name,
                address = address,
                latitude = latitude,
                longitude = longitude,
                phone = phone,
                userId = userId,
                photoUrl = photoUrl
            )
            val result = submissionRepository.submitWorkshop(submission)
            _submissionResult.value = result
            _isLoading.value = false
        }
    }

    fun checkAuth() {
        _isLoggedIn.value = authRepository.isLoggedIn()
    }
}
