package com.tambal_ban

import android.app.Application
import android.content.ComponentCallbacks2
import android.util.Log
import com.tambal_ban.ads.AdMobManager
import com.tambal_ban.data.api.NetworkModule
import com.tambal_ban.data.api.SupabaseService
import com.tambal_ban.data.database.WorkshopDbHelper
import com.tambal_ban.data.repository.AuthRepository
import com.tambal_ban.data.repository.ReviewRepository
import com.tambal_ban.data.repository.SubmissionRepository
import com.tambal_ban.data.repository.WorkshopRepository
import com.tambal_ban.utils.AuthPrefs
import org.osmdroid.config.Configuration

class TambalBanApp : Application() {

    private val TAG = "TambalBanApp"

    lateinit var dbHelper: WorkshopDbHelper
        private set

    lateinit var workshopRepository: WorkshopRepository
        private set

    lateinit var authRepository: AuthRepository
        private set

    lateinit var submissionRepository: SubmissionRepository
        private set

    lateinit var reviewRepository: ReviewRepository
        private set

    lateinit var adMobManager: AdMobManager
        private set

    lateinit var authPrefs: AuthPrefs
        private set

    lateinit var supabaseService: SupabaseService
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

        // Initialize SupabaseService
        supabaseService = NetworkModule.provideSupabaseService(authPrefs)

        // Initialize repository with native implementation
        workshopRepository = WorkshopRepository(dbHelper, supabaseService)

        // Initialize AuthRepository
        authRepository = AuthRepository(supabaseService, authPrefs)

        // Initialize SubmissionRepository
        submissionRepository = SubmissionRepository(supabaseService)

        // Initialize ReviewRepository
        reviewRepository = ReviewRepository(supabaseService)

        // T031: Initialize AdMob but don't load ads immediately
        adMobManager = AdMobManager(this)
        adMobManager.initialize()
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
