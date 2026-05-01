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

    private val _workshops = MutableLiveData<List<Workshop>>(emptyList())
    val workshops: LiveData<List<Workshop>> = _workshops

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _isMoreLoading = MutableLiveData<Boolean>()
    val isMoreLoading: LiveData<Boolean> = _isMoreLoading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    private var currentPage = 0
    private val pageSize = 10
    private var isLastPage = false
    private var currentLat: Double? = null
    private var currentLon: Double? = null
    private var currentQuery: String? = null

    fun fetchNearbyWorkshops(lat: Double, lon: Double, radiusMeters: Int, refresh: Boolean = true) {
        if (refresh) {
            currentPage = 0
            isLastPage = false
            _workshops.value = emptyList()
            _isLoading.value = true
        } else {
            if (isLastPage || _isMoreLoading.value == true) return
            _isMoreLoading.value = true
        }

        currentLat = lat
        currentLon = lon
        currentQuery = null
        _error.value = null
        
        viewModelScope.launch {
            try {
                val result = repository.getNearbyWorkshops(
                    lat, lon, radiusMeters, getApplication(),
                    limit = pageSize,
                    offset = currentPage * pageSize
                )
                
                val currentList = _workshops.value ?: emptyList()
                if (result.isEmpty()) {
                    isLastPage = true
                } else {
                    _workshops.value = currentList + result
                    currentPage++
                }
            } catch (e: Exception) {
                _error.value = e.message ?: "An unknown error occurred"
            } finally {
                _isLoading.value = false
                _isMoreLoading.value = false
            }
        }
    }

    fun fetchAllWorkshops(refresh: Boolean = true) {
        if (refresh) {
            currentPage = 0
            isLastPage = false
            _workshops.value = emptyList()
            _isLoading.value = true
        } else {
            if (isLastPage || _isMoreLoading.value == true) return
            _isMoreLoading.value = true
        }

        currentQuery = null
        currentLat = null
        currentLon = null
        _error.value = null

        viewModelScope.launch {
            try {
                val result = repository.getAllWorkshops(
                    getApplication(),
                    limit = pageSize,
                    offset = currentPage * pageSize
                )

                val currentList = _workshops.value ?: emptyList()
                if (result.isEmpty()) {
                    isLastPage = true
                } else {
                    _workshops.value = currentList + result
                    currentPage++
                }
            } catch (e: Exception) {
                _error.value = e.message ?: "An unknown error occurred"
            } finally {
                _isLoading.value = false
                _isMoreLoading.value = false
            }
        }
    }

    fun searchWorkshops(query: String, refresh: Boolean = true) {
        if (query.isEmpty()) {
            fetchAllWorkshops(refresh)
            return
        }

        if (refresh) {
            currentPage = 0
            isLastPage = false
            _workshops.value = emptyList()
            _isLoading.value = true
        } else {
            if (isLastPage || _isMoreLoading.value == true) return
            _isMoreLoading.value = true
        }

        currentQuery = query
        _error.value = null

        viewModelScope.launch {
            try {
                val result = repository.searchWorkshops(
                    query, getApplication(),
                    limit = pageSize,
                    offset = currentPage * pageSize
                )

                val currentList = _workshops.value ?: emptyList()
                if (result.isEmpty()) {
                    isLastPage = true
                } else {
                    _workshops.value = currentList + result
                    currentPage++
                }
            } catch (e: Exception) {
                _error.value = e.message ?: "An unknown error occurred"
            } finally {
                _isLoading.value = false
                _isMoreLoading.value = false
            }
        }
    }

    fun loadMore() {
        val query = currentQuery
        if (query != null) {
            searchWorkshops(query, refresh = false)
        } else {
            val lat = currentLat
            val lon = currentLon
            if (lat != null && lon != null) {
                fetchNearbyWorkshops(lat, lon, 5000, refresh = false)
            } else {
                fetchAllWorkshops(refresh = false)
            }
        }
    }
}
