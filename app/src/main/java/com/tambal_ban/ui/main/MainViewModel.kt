package com.tambal_ban.ui.main

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
import com.tambal_ban.utils.Constants
import com.tambal_ban.utils.GeoUtils
import kotlinx.coroutines.launch

/** ViewModel for MainActivity (Map screen) using native components */
class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: WorkshopRepository = (application as TambalBanApp).workshopRepository
    private val locationService: LocationService = LocationService.getInstance(application)

    // UI State
    private val _workshops = MutableLiveData<List<Workshop>>()
    val workshops: LiveData<List<Workshop>> = _workshops

    private val _nearestWorkshops = MutableLiveData<List<Workshop>>()
    val nearestWorkshops: LiveData<List<Workshop>> = _nearestWorkshops

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    private val _userLocation = MutableLiveData<Location?>()
    val userLocation: LiveData<Location?> = _userLocation

    private val _searchRadius = MutableLiveData<Int>()
    val searchRadius: LiveData<Int> = _searchRadius

    private val _emergencyWorkshop = MutableLiveData<Workshop?>()
    val emergencyWorkshop: LiveData<Workshop?> = _emergencyWorkshop

    private val _isEmergencyMode = MutableLiveData<Boolean>()
    val isEmergencyMode: LiveData<Boolean> = _isEmergencyMode

    init {
        _searchRadius.value = Constants.RADIUS_3KM
        _isEmergencyMode.value = false
    }

    /** Start location tracking */
    fun startLocationUpdates() {
        locationService.getLastLocation()
        locationService.requestLocationUpdates()
    }

    /** Stop location tracking */
    fun stopLocationUpdates() {
        locationService.stopLocationUpdates()
    }

    /** Get the location service LiveData for observation in Activity */
    fun getLocationLiveData(): LiveData<Location?> = locationService.location
    fun getLocationErrorLiveData(): LiveData<String?> = locationService.locationError

    /** Update user location in ViewModel state */
    fun onLocationUpdated(location: Location?) {
        _userLocation.value = location
    }

    /** Load workshops by bounding box (Viewport-based) */
    fun loadWorkshopsByBounds(minLat: Double, maxLat: Double, minLng: Double, maxLng: Double) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val workshopList =
                        repository.getWorkshopsInBounds(
                                minLat,
                                maxLat,
                                minLng,
                                maxLng,
                                context = getApplication()
                        )

                // If user location is available, calculate distances
                val location = _userLocation.value
                val finalizedList =
                        if (location != null) {
                            workshopList
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
                            workshopList
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

    /** Find nearest workshops within specific radius */
    fun findNearestWorkshops(radius: Int) {
        val location = _userLocation.value ?: return

        viewModelScope.launch {
            _isLoading.value = true
            try {
                val workshopList =
                        repository.findNearestWorkshops(
                                userLat = location.latitude,
                                userLng = location.longitude,
                                radiusKm = radius
                        )
                _nearestWorkshops.value = workshopList
                _error.value = null
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }

    /** Set search radius */
    fun setSearchRadius(radius: Int) {
        _searchRadius.value = radius
    }

    /** Activate emergency mode (find closest immediately using SQL optimization) */
    fun activateEmergencyMode() {
        val location = _userLocation.value ?: return
        _isEmergencyMode.value = true

        viewModelScope.launch {
            _isLoading.value = true
            try {
                val closest = repository.getClosestWorkshop(location.latitude, location.longitude)
                if (closest != null) {
                    _nearestWorkshops.value = listOf(closest)
                } else {
                    // Fallback to broader radius if no local hit
                    findNearestWorkshops(Constants.EMERGENCY_RADIUS_KM)
                }
                _error.value = null
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }

    /** Deactivate emergency mode */
    fun deactivateEmergencyMode() {
        _isEmergencyMode.value = false
        _emergencyWorkshop.value = null
    }

    /** Set emergency workshop */
    fun setEmergencyWorkshop(workshop: Workshop?) {
        _emergencyWorkshop.value = workshop
    }

    /** Clear error */
    fun clearError() {
        _error.value = null
    }

    /** Search workshops by name */
    fun searchByName(query: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val results = repository.searchWorkshops(getApplication(), query)

                // Calculate distance if location is available
                val location = _userLocation.value
                val finalizedList =
                        if (location != null) {
                            results
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
                            results
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
}
