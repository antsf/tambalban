package com.tambal_ban.core.location

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.os.Looper
import androidx.core.content.ContextCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.location.*
import com.tambal_ban.core.utils.Constants
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

/**
 * Service for handling location updates
 */
class LocationService(private val context: Context) {

    private val fusedLocationClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(context)

    private val _location = MutableLiveData<Location?>()
    val location: LiveData<Location?> = _location

    private val _locationError = MutableLiveData<String?>()
    val locationError: LiveData<String?> = _locationError

    private var locationCallback: LocationCallback? = null

    /**
     * Check if location permissions are granted
     */
    fun hasLocationPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED ||
        ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    /**
     * Get last known location
     */
    @SuppressLint("MissingPermission")
    fun getLastLocation() {
        if (!hasLocationPermission()) {
            _locationError.value = "Location permission not granted"
            return
        }

        fusedLocationClient.lastLocation
            .addOnSuccessListener { location ->
                if (location != null) {
                    _location.value = location
                } else {
                    // Try to get fresh location
                    requestLocationUpdates()
                }
            }
            .addOnFailureListener { e ->
                _locationError.value = e.message
            }
    }

    /**
     * Request location updates
     */
    @SuppressLint("MissingPermission")
    fun requestLocationUpdates() {
        if (!hasLocationPermission()) {
            _locationError.value = "Location permission not granted"
            return
        }

        val locationRequest = LocationRequest.Builder(
            Priority.PRIORITY_HIGH_ACCURACY,
            Constants.LOCATION_UPDATE_INTERVAL
        )
            .setMinUpdateIntervalMillis(Constants.LOCATION_FASTEST_INTERVAL)
            .setMinUpdateDistanceMeters(Constants.LOCATION_MIN_DISTANCE)
            .build()

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(result: LocationResult) {
                result.lastLocation?.let { location ->
                    _location.value = location
                }
            }

            override fun onLocationAvailability(availability: LocationAvailability) {
                if (!availability.isLocationAvailable) {
                    _locationError.value = "Location not available"
                }
            }
        }

        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            locationCallback!!,
            Looper.getMainLooper()
        )
    }

    /**
     * Stop location updates
     */
    fun stopLocationUpdates() {
        locationCallback?.let {
            fusedLocationClient.removeLocationUpdates(it)
            locationCallback = null
        }
    }

    /**
     * Clear error state
     */
    fun clearError() {
        _locationError.value = null
    }

    /**
     * Get last known location asynchronously
     */
    @SuppressLint("MissingPermission")
    suspend fun getLastKnownLocation(): Location? = suspendCancellableCoroutine { continuation ->
        if (!hasLocationPermission()) {
            continuation.resume(null)
            return@suspendCancellableCoroutine
        }

        fusedLocationClient.lastLocation
            .addOnSuccessListener { location ->
                continuation.resume(location)
            }
            .addOnFailureListener {
                continuation.resume(null)
            }
    }

    companion object {
        @Volatile
        private var INSTANCE: LocationService? = null

        fun getInstance(context: Context): LocationService {
            return INSTANCE ?: synchronized(this) {
                val instance = LocationService(context.applicationContext)
                INSTANCE = instance
                instance
            }
        }
    }
}

