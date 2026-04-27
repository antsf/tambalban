package com.tambal_ban.map.viewmodel
import com.tambal_ban.map.ui.* 
import com.tambal_ban.map.viewmodel.* 

import android.app.Application
import android.location.Location
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.tambal_ban.TambalBanApp
import com.tambal_ban.workshop.data.Workshop
import com.tambal_ban.workshop.data.WorkshopRepository
import com.tambal_ban.core.location.LocationService
import com.tambal_ban.core.utils.Constants
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch

/**
 * T006: ViewModel for the modernized Home screen.
 * Handles nearby workshops fetching and search state.
 */
class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: WorkshopRepository = (application as TambalBanApp).workshopRepository
    private val locationService: LocationService = LocationService.getInstance(application)

    private val _workshops = MutableLiveData<List<Workshop>>()
    val workshops: LiveData<List<Workshop>> = _workshops

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _searchQuery = MutableLiveData<String>()
    val searchQuery: LiveData<String> = _searchQuery

    private val _userLocation = MutableLiveData<Location?>()
    val userLocation: LiveData<Location?> = _userLocation

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    private val _sheetTitle = MutableLiveData<String>("Tambal Ban Terdekat")
    val sheetTitle: LiveData<String> = _sheetTitle

    private val _searchFlow = MutableStateFlow("")

    init {
        viewModelScope.launch {
            @OptIn(FlowPreview::class)
            _searchFlow
                .debounce(500)
                .filter { it.length >= 3 }
                .distinctUntilChanged()
                .collect { query ->
                    searchWorkshops(query)
                }
        }
    }

    fun startLocationUpdates() {
        locationService.getLastLocation()
        locationService.requestLocationUpdates()
    }

    fun stopLocationUpdates() {
        locationService.stopLocationUpdates()
    }

    fun getLocationLiveData(): LiveData<Location?> = locationService.location

    fun onLocationUpdated(location: Location?) {
        _userLocation.value = location
        // Fetch workshops when location is updated if the workshop list is empty
        if (_workshops.value.isNullOrEmpty()) {
            location?.let {
                fetchNearbyWorkshops(it.latitude, it.longitude)
            }
        }
    }

    fun fetchNearbyWorkshops(lat: Double, lon: Double, radiusKm: Int = Constants.RADIUS_3KM) {
        _sheetTitle.value = "Tambal Ban Terdekat"
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val list = repository.getNearbyWorkshops(
                    lat, 
                    lon, 
                    radiusKm * 10000, 
                    getApplication()
                )
                _workshops.value = list
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun onSearchTextChanged(query: String) {
        _searchFlow.value = query
        if (query.isEmpty()) {
            _userLocation.value?.let {
                fetchNearbyWorkshops(it.latitude, it.longitude)
            }
        }
    }

    fun searchWorkshops(query: String) {
        _searchQuery.value = query
        _sheetTitle.value = "Hasil Pencarian"
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val list = repository.searchWorkshops(query, getApplication())
                _workshops.value = list
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }
}
