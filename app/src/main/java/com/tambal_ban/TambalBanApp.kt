package com.tambal_ban
import com.tambal_ban.auth.data.* 
import com.tambal_ban.workshop.data.* 

import android.app.Application
import android.content.ComponentCallbacks2
import android.util.Log
import com.google.firebase.ktx.Firebase
import com.google.firebase.analytics.ktx.analytics
import com.tambal_ban.core.ads.AdMobManager
import com.tambal_ban.core.network.NetworkModule
import com.tambal_ban.core.network.TambalBanApiService
import com.tambal_ban.core.update.InAppUpdateManager
import com.tambal_ban.core.utils.AnalyticsHelper
import com.tambal_ban.core.utils.CrashlyticsHelper
import com.tambal_ban.workshop.data.database.WorkshopDbHelper
import com.tambal_ban.auth.data.AuthRepository
import com.tambal_ban.auth.data.ProfileRepository
import com.tambal_ban.workshop.data.ReviewRepository
import com.tambal_ban.workshop.data.WorkshopRepository
import com.tambal_ban.core.utils.AuthPrefs
import org.osmdroid.config.Configuration

class TambalBanApp : Application() {

    private val TAG = "TambalBanApp"

    lateinit var dbHelper: WorkshopDbHelper
        private set

    lateinit var workshopRepository: WorkshopRepository
        private set

    lateinit var authRepository: AuthRepository
        private set

    lateinit var reviewRepository: ReviewRepository
        private set

    lateinit var profileRepository: ProfileRepository
        private set

    lateinit var adMobManager: AdMobManager
        private set

    lateinit var authPrefs: AuthPrefs
        private set

    lateinit var apiService: TambalBanApiService
        private set

    lateinit var inAppUpdateManager: InAppUpdateManager
        private set

    override fun onCreate() {
        super.onCreate()
        instance = this

        // Initialize OSMDroid configuration with memory optimizations
        Configuration.getInstance().apply {
            userAgentValue = packageName
            // T014: Set tile cache limit for 2GB RAM devices (Strict 50MB max)
            tileFileSystemCacheTrimBytes = 30 * 1024 * 1024 // 30MB
            tileFileSystemCacheMaxBytes = 50 * 1024 * 1024 // 50MB
            load(this@TambalBanApp, getSharedPreferences("osmdroid", MODE_PRIVATE))
        }

        // Initialize native database helper
        dbHelper = WorkshopDbHelper(this)

        // Initialize AuthPrefs
        authPrefs = AuthPrefs(this)

        // Initialize TambalBanApiService
        apiService = NetworkModule.provideApiService(authPrefs)

        // Initialize repository with native implementation
        workshopRepository = WorkshopRepository(dbHelper, apiService)

        // Initialize AuthRepository
        authRepository = AuthRepository(apiService, authPrefs)

        // Initialize ReviewRepository
        reviewRepository = ReviewRepository(apiService)

        // Initialize ProfileRepository
        profileRepository = ProfileRepository(apiService)

        // T031: Initialize AdMob but don't load ads immediately
        adMobManager = AdMobManager(this)
        adMobManager.initialize()

        // 023: Initialize Firebase Analytics & Crashlytics
        Firebase.analytics
        AnalyticsHelper.initialize()
        CrashlyticsHelper.initialize()

        // 023: Set Crashlytics user ID if logged in
        if (authPrefs.isLoggedIn()) {
            authPrefs.getUserId()?.let { userId ->
                CrashlyticsHelper.setUserId(userId)
            }
        }

        // 021: Initialize In-App Update Manager
        inAppUpdateManager = InAppUpdateManager(authPrefs)
    }

    /** T029: Global low-memory handling for 2GB RAM devices */
    override fun onTrimMemory(level: Int) {
        super.onTrimMemory(level)
        Log.d(TAG, "onTrimMemory level: $level")

        when (level) {
            ComponentCallbacks2.TRIM_MEMORY_RUNNING_CRITICAL,
            ComponentCallbacks2.TRIM_MEMORY_RUNNING_LOW,
            ComponentCallbacks2.TRIM_MEMORY_UI_HIDDEN -> {
                // Reduce OSMDroid tile cache to minimum
                Configuration.getInstance().apply {
                    tileFileSystemCacheTrimBytes = 5 * 1024 * 1024 // 5MB minimum
                }
            }
            ComponentCallbacks2.TRIM_MEMORY_COMPLETE, ComponentCallbacks2.TRIM_MEMORY_MODERATE -> {
                // Extreme memory pressure - release as much as possible
                Configuration.getInstance().apply {
                    tileFileSystemCacheTrimBytes = 1 * 1024 * 1024 // 1MB minimum
                    tileFileSystemCacheMaxBytes = 5 * 1024 * 1024 // 5MB max
                }
            }
        }
    }

    companion object {
        lateinit var instance: TambalBanApp
            private set
    }
}
