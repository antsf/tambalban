package com.tambal_ban.ui.auth

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.tambal_ban.TambalBanApp
import com.tambal_ban.data.model.AuthResponse
import com.tambal_ban.data.repository.AuthRepository
import kotlinx.coroutines.launch

class AuthViewModel(application: Application) : AndroidViewModel(application) {

    private val authRepository: AuthRepository = (application as TambalBanApp).authRepository

    private val _authResult = MutableLiveData<Result<AuthResponse>>()
    val authResult: LiveData<Result<AuthResponse>> = _authResult

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _isLoading.value = true
            val result = authRepository.login(email, password)
            _authResult.value = result
            _isLoading.value = false
        }
    }

    fun register(email: String, password: String, fullName: String) {
        viewModelScope.launch {
            _isLoading.value = true
            val result = authRepository.register(email, password, fullName)
            _authResult.value = result
            _isLoading.value = false
        }
    }

    fun clearResult() {
        _authResult.value = null
    }
}
