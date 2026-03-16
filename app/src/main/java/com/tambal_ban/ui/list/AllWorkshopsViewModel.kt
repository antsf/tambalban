package com.tambal_ban.ui.list

import android.app.Application
import android.location.Location
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.tambal_ban.TambalBanApp
import com.tambal_ban.data.model.Workshop
import com.tambal_ban.data.repository.WorkshopRepository
import com.tambal_ban.location.LocationService
import com.tambal_ban.utils.GeoUtils
import kotlinx.coroutines.launch

class AllWorkshopsViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: WorkshopRepository = (application as TambalBanApp).workshopRepository
    private val locationService: LocationService = LocationService.getInstance(application)

    private val _workshops = MutableLiveData<List<Workshop>>()
    val workshops: LiveData<List<Workshop>> = _workshops

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    private val _userLocation = MutableLiveData<Location?>()

    init {
        // Get current location if available
        locationService.location.value?.let { _userLocation.value = it }
        loadWorkshops()
    }

    fun loadWorkshops() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                // Fetch all workshops (T041: Pagination could be added here by passing page/limit)
                val allWorkshops = repository.getAllWorkshops(getApplication())

                // Calculate distance if location is available
                val location = _userLocation.value
                val finalizedList =
                        if (location != null) {
                            allWorkshops
                                    .map { workshop ->
                                        workshop.distance =
                                                GeoUtils.calculateDistance(
                                                        location.latitude,
                                                        location.longitude,
                                                        workshop.latitude,
                                                        workshop.longitude
                                                )
                                        workshop
                                    }
                                    .sortedBy { it.distance }
                        } else {
                            allWorkshops
                        }

                _workshops.value = finalizedList
                _error.value = null
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun refresh() {
        loadWorkshops()
    }

    fun sortWorkshops(criteria: String) {
        val currentList = _workshops.value ?: return
        val sortedList =
                when (criteria) {
                    "name" -> currentList.sortedBy { it.name }
                    "distance" -> currentList.sortedBy { it.distance }
                    "rating" -> currentList.sortedByDescending { it.ratingAvg }
                    else -> currentList
                }
        _workshops.value = sortedList
    }
}
