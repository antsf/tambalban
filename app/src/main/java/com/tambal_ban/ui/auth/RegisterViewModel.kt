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

/**
 * ViewModel for the Registration screen.
 */
class RegisterViewModel(application: Application) : AndroidViewModel(application) {

    private val authRepository: AuthRepository = (application as TambalBanApp).authRepository

    private val _registerResult = MutableLiveData<Result<AuthResponse>>()
    val registerResult: LiveData<Result<AuthResponse>> = _registerResult

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _validationError = MutableLiveData<Pair<String, String>?>()
    val validationError: LiveData<Pair<String, String>?> = _validationError

    fun validateAndRegister(name: String, email: String, password: String) {
        _validationError.value = null

        if (name.isEmpty()) {
            _validationError.value = "name" to "Nama wajib diisi"
            return
        }
        if (email.isEmpty()) {
            _validationError.value = "email" to "Email wajib diisi"
            return
        }
        if (!isEmailValid(email)) {
            _validationError.value = "email" to "Format email tidak valid"
            return
        }
        if (password.length < 8) {
            _validationError.value = "password" to "Kata sandi minimal 8 karakter"
            return
        }
        
        register(name, email, password)
    }

    private fun isEmailValid(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    fun register(name: String, email: String, password: String) {
        viewModelScope.launch {
            _isLoading.value = true
            val result = authRepository.register(name, email, password)
            _registerResult.value = result
            _isLoading.value = false
        }
    }
}
