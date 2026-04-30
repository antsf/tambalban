package com.tambal_ban.core.network

import com.tambal_ban.core.utils.AuthPrefs

/**
 * ApiClient provides a single point of access to the SupabaseService.
 * Following Section III: API-Driven Development.
 */
object ApiClient {
    private var service: SupabaseService? = null

    /**
     * Initializes and returns the SupabaseService instance.
     * Uses Manual Dependency Injection as per Section II.
     */
    fun getService(authPrefs: AuthPrefs): SupabaseService {
        if (service == null) {
            service = NetworkModule.provideSupabaseService(authPrefs)
        }
        return service!!
    }
}
