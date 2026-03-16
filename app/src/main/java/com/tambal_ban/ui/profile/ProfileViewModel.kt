package com.tambal_ban.ui.profile

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.tambal_ban.TambalBanApp
import com.tambal_ban.data.model.WorkshopSubmission
import com.tambal_ban.data.repository.AuthRepository
import com.tambal_ban.utils.AuthPrefs
import kotlinx.coroutines.launch

class ProfileViewModel(application: Application) : AndroidViewModel(application) {

    private val authRepository: AuthRepository = (application as TambalBanApp).authRepository
    private val authPrefs = AuthPrefs(application)
    private val supabaseService = (application as TambalBanApp).supabaseService

    private val _submissions = MutableLiveData<List<WorkshopSubmission>>()
    val submissions: LiveData<List<WorkshopSubmission>> = _submissions

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _userProfile = MutableLiveData<Pair<String, String>>() // Name, Email
    val userProfile: LiveData<Pair<String, String>> = _userProfile

    init {
        loadUserProfile()
        loadSubmissions()
    }

    private fun loadUserProfile() {
        val name = authPrefs.getUserName() ?: "User"
        val email = authPrefs.getEmail() ?: ""
        _userProfile.value = Pair(name, email)
    }

    fun loadSubmissions() {
        val userId = authPrefs.getUserId() ?: return
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = supabaseService.getUserSubmissions(userId)
                if (response.isSuccessful) {
                    _submissions.value = response.body() ?: emptyList()
                }
            } catch (e: Exception) {
                // Handle error
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun logout() {
        authRepository.logout()
    }
}
