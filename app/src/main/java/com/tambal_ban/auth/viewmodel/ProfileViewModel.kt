package com.tambal_ban.auth.viewmodel
import com.tambal_ban.auth.ui.* 
import com.tambal_ban.auth.viewmodel.* 
import com.tambal_ban.auth.data.* 

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.tambal_ban.TambalBanApp
import com.tambal_ban.auth.data.Profile
import com.tambal_ban.auth.data.AuthRepository
import com.tambal_ban.auth.data.ProfileRepository
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

    private val _isUploading = MutableLiveData<Boolean>()
    val isUploading: LiveData<Boolean> = _isUploading

    private val _isUpdateSuccess = MutableLiveData<Boolean>()
    val isUpdateSuccess: LiveData<Boolean> = _isUpdateSuccess

    private val _isLoggedOut = MutableLiveData<Boolean>()
    val isLoggedOut: LiveData<Boolean> = _isLoggedOut

    val userEmail: String? get() = authRepository.getUserEmail()

    fun getProfile() {
        if (!authRepository.isLoggedIn()) return
        viewModelScope.launch {
            _isLoading.value = true
            val result = profileRepository.getProfile()
            if (result.isSuccess) {
                _profile.value = result.getOrNull()
            } else {
                _error.value = result.exceptionOrNull()?.message
                Log.d("Profile", "error get profile: ${_error.value}")
            }
            _isLoading.value = false
        }
    }

    fun updateProfile(fullName: String, phone: String) {
        if (!authRepository.isLoggedIn()) return
        viewModelScope.launch {
            _isLoading.value = true
            val updates = mapOf(
                "full_name" to fullName,
                "phone" to phone
            )
            val result = profileRepository.updateProfile(updates)
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
        if (!authRepository.isLoggedIn()) return
        viewModelScope.launch {
            _isUploading.value = true
            val result = profileRepository.uploadAvatar(bytes, mimeType)
            if (result.isSuccess) {
                val path = result.getOrNull()!!
                // Update profile with new avatar path/url
                // Note: For now we just pass the path, assume backend or repository handles full URL
                updateProfileAvatar(path)
            } else {
                _error.value = result.exceptionOrNull()?.message
                _isUploading.value = false
            }
        }
    }

    private fun updateProfileAvatar(path: String) {
        if (!authRepository.isLoggedIn()) return
        viewModelScope.launch {
            val result = profileRepository.updateProfile(mapOf("avatar_url" to path))
            if (result.isSuccess) {
                _isUpdateSuccess.value = true
                getProfile()
            } else {
                _error.value = result.exceptionOrNull()?.message
            }
            _isUploading.value = false
        }
    }

    fun logout() {
        viewModelScope.launch {
            authRepository.logout()
            _isLoggedOut.value = true
        }
    }

    fun isLoggedIn(): Boolean = authRepository.isLoggedIn()
}
