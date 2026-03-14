package com.tambal_ban.utils

object Constants {
    // Supabase Configuration - Replace with your actual values
    const val SUPABASE_URL = "https://your-project.supabase.co"
    const val SUPABASE_ANON_KEY = "your-anon-key-here"

    // API Endpoints
    const val WORKSHOPS_ENDPOINT = "rest/v1/workshops"
    const val WORKSHOP_SUBMISSIONS_ENDPOINT = "rest/v1/workshop_submissions"
    const val REVIEWS_ENDPOINT = "rest/v1/reviews"

    // Default map settings
    val DEFAULT_LATITUDE = -6.2088 // Jakarta
    val DEFAULT_LONGITUDE = 106.8456
    val DEFAULT_ZOOM = 14.0

    // Search radius options (in km)
    const val RADIUS_1KM = 1
    const val RADIUS_3KM = 3
    const val RADIUS_5KM = 5

    // Emergency mode radius (in km)
    const val EMERGENCY_RADIUS_KM = 3

    // Map viewport buffer (to load markers slightly outside visible area)
    val MAP_VIEWPORT_BUFFER = 0.01

    // Clustering
    const val CLUSTER_MIN_ZOOM = 10
    const val CLUSTER_MAX_ZOOM = 18

    // Pagination
    const val MAX_MARKERS_PER_REQUEST = 200

    // Location
    val LOCATION_UPDATE_INTERVAL = 10000L // 10 seconds
    val LOCATION_FASTEST_INTERVAL = 5000L // 5 seconds
    val LOCATION_MIN_DISTANCE = 10f // 10 meters

    // Database
    const val DATABASE_NAME = "tambalban_database"
    const val DATABASE_VERSION = 1

    // Intent extras
    const val EXTRA_WORKSHOP_ID = "extra_workshop_id"
    const val EXTRA_WORKSHOP = "extra_workshop"

    // Preferences
    const val PREFS_NAME = "tambalban_prefs"
    const val PREF_LAST_LATITUDE = "last_latitude"
    const val PREF_LAST_LONGITUDE = "last_longitude"
    const val PREF_SEARCH_RADIUS = "search_radius"

    // AdMob - Replace with your actual ad unit IDs
    const val ADMOB_BANNER_AD_UNIT = "ca-app-pub-xxxxxxxxxxxxxxxxxxx/yyyyyyyyyyyyyy"
    const val ADMOB_NATIVE_AD_UNIT = "ca-app-pub-xxxxxxxxxxxxxxxxxxx/yyyyyyyyyyyyyy"
}

