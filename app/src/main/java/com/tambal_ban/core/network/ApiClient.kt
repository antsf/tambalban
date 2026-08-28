package com.tambal_ban.core.network

import com.tambal_ban.core.utils.AuthPrefs

/**
 * ApiClient provides a single point of access to the TambalBanApiService.
 * Following Section III: API-Driven Development.
 */
object ApiClient {
    private var service: TambalBanApiService? = null

    /**
     * Initializes and returns the TambalBanApiService instance.
     * Uses Manual Dependency Injection as per Section II.
     */
    fun getService(authPrefs: AuthPrefs): TambalBanApiService {
        if (service == null) {
            service = NetworkModule.provideApiService(authPrefs)
        }
        return service!!
    }
}
