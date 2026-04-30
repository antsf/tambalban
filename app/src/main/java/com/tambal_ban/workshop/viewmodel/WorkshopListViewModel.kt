package com.tambal_ban.workshop.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tambal_ban.workshop.data.Workshop
import com.tambal_ban.workshop.data.WorkshopRepository
import kotlinx.coroutines.launch

class WorkshopListViewModel(application: android.app.Application) : androidx.lifecycle.AndroidViewModel(application) {

    private val repository: com.tambal_ban.workshop.data.WorkshopRepository = 
        (application as com.tambal_ban.TambalBanApp).workshopRepository

    private val _workshops = MutableLiveData<List<Workshop>>()
    val workshops: LiveData<List<Workshop>> = _workshops

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    fun fetchNearbyWorkshops(lat: Double, lon: Double, radiusMeters: Int) {
        _isLoading.value = true
        _error.value = null
        
        viewModelScope.launch {
            try {
                val result = repository.getNearbyWorkshops(lat, lon, radiusMeters, getApplication())
                _workshops.value = result
            } catch (e: Exception) {
                _error.value = e.message ?: "An unknown error occurred"
            } finally {
                _isLoading.value = false
            }
        }
    }
}
