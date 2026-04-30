package com.tambal_ban.core.network

import com.tambal_ban.core.utils.AuthPrefs
import com.tambal_ban.core.utils.Constants
import com.tambal_ban.core.utils.SupabaseConfig
import okhttp3.Interceptor
import okhttp3.Response

/**
 * Interceptor that adds Supabase API Key and Bearer Token to outgoing requests.
 */
class AuthInterceptor(private val authPrefs: AuthPrefs) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val requestBuilder = originalRequest.newBuilder()
        
        // 1. Mandatory Supabase API Key
        requestBuilder.addHeader("apikey", SupabaseConfig.ANON_KEY)

        val token = authPrefs.getAccessToken()
        val isAuthRequest = originalRequest.url.toString().contains("/auth/v1/")

        // 2. Authorization Header logic
        if (!token.isNullOrEmpty()) {
            // Logged in: Use user's JWT
            requestBuilder.header("Authorization", "Bearer $token")
        } else if (!isAuthRequest) {
            // Not logged in: Use ANON_KEY as fallback for public RLS access
            requestBuilder.header("Authorization", "Bearer ${SupabaseConfig.ANON_KEY}")
        }

        val response = chain.proceed(requestBuilder.build())

        if (response.code == 401 && !isAuthRequest) {
            // Handle unauthorized error - clear invalid tokens
            authPrefs.clear()
        }

        return response
    }
}
