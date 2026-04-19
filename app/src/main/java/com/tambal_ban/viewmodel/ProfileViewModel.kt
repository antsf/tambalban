package com.tambal_ban.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.tambal_ban.TambalBanApp
import com.tambal_ban.data.model.Profile
import com.tambal_ban.data.repository.AuthRepository
import com.tambal_ban.data.repository.ProfileRepository
import kotlinx.coroutines.launch

/**
 * ViewModel for Profile-related screens.
 */
class ProfileViewModel(application: Application) : AndroidViewModel(application) {

    private val profileRepository: ProfileRepository = (application as TambalBanApp).profileRepository
    private val authRepository: AuthRepository = (application as TambalBanApp).authRepository

    private val _profile = MutableLiveData<Profile?>()
    val profile: LiveData<Profile?> = _profile

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    private val _isUpdateSuccess = MutableLiveData<Boolean>()
    val isUpdateSuccess: LiveData<Boolean> = _isUpdateSuccess

    private val _isLoggedOut = MutableLiveData<Boolean>()
    val isLoggedOut: LiveData<Boolean> = _isLoggedOut

    fun getProfile() {
        val userId = authRepository.getUserId() ?: return
        viewModelScope.launch {
            _isLoading.value = true
            val result = profileRepository.getProfile(userId)
            if (result.isSuccess) {
                _profile.value = result.getOrNull()
            } else {
                _error.value = result.exceptionOrNull()?.message
            }
            _isLoading.value = false
        }
    }

    fun updateProfile(fullName: String, phone: String) {
        val userId = authRepository.getUserId() ?: return
        viewModelScope.launch {
            _isLoading.value = true
            val updates = mapOf(
                "full_name" to fullName,
                "phone" to phone
            )
            val result = profileRepository.updateProfile(userId, updates)
            if (result.isSuccess) {
                _isUpdateSuccess.value = true
                getProfile() // Refresh data
            } else {
                _error.value = result.exceptionOrNull()?.message
            }
            _isLoading.value = false
        }
    }

    fun uploadAvatar(bytes: ByteArray, mimeType: String) {
        val userId = authRepository.getUserId() ?: return
        viewModelScope.launch {
            _isLoading.value = true
            val result = profileRepository.uploadAvatar(userId, bytes, mimeType)
            if (result.isSuccess) {
                val path = result.getOrNull()!!
                // Update profile with new avatar path/url
                // Note: For now we just pass the path, assume backend or repository handles full URL
                updateProfileAvatar(path)
            } else {
                _error.value = result.exceptionOrNull()?.message
                _isLoading.value = false
            }
        }
    }

    private fun updateProfileAvatar(path: String) {
        val userId = authRepository.getUserId() ?: return
        viewModelScope.launch {
            val result = profileRepository.updateProfile(userId, mapOf("avatar_url" to path))
            if (result.isSuccess) {
                _isUpdateSuccess.value = true
                getProfile()
            } else {
                _error.value = result.exceptionOrNull()?.message
            }
            _isLoading.value = false
        }
    }

    fun logout() {
        authRepository.logout()
        _isLoggedOut.value = true
    }

    fun isLoggedIn(): Boolean = authRepository.isLoggedIn()
}
