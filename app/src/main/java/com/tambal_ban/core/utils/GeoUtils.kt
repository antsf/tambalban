package com.tambal_ban.core.utils

import java.util.Locale
import kotlin.math.*

/** Utility class for geographical calculations */
object GeoUtils {

    /**
     * Calculate distance between two points using Haversine formula Returns distance in kilometers
     */
    fun calculateDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val r = 6371 // Radius of the earth in km
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)
        val a =
                sin(dLat / 2).pow(2) +
                        cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) * sin(dLon / 2).pow(2)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        return r * c
    }

    /** Format distance for display */
    fun formatDistance(distanceKm: Double): String {
        return if (distanceKm < 1.0) {
            String.format(Locale.getDefault(), "%.0f m", distanceKm * 1000)
        } else {
            String.format(Locale.getDefault(), "%.1f km", distanceKm)
        }
    }
}
