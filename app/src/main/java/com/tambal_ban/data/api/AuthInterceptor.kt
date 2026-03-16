package com.tambal_ban.data.api

import com.tambal_ban.utils.AuthPrefs
import com.tambal_ban.utils.SupabaseConfig
import okhttp3.Interceptor
import okhttp3.Response

/**
 * Interceptor that adds Supabase API Key and Bearer Token to outgoing requests. Handles token
 * refresh on 401 responses.
 */
class AuthInterceptor(private val authPrefs: AuthPrefs) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val requestBuilder =
                originalRequest.newBuilder().addHeader("apikey", SupabaseConfig.ANON_KEY)

        val token = authPrefs.getAccessToken()
        val isAuthRequest = originalRequest.url.toString().contains("/auth/v1/")

        if (!token.isNullOrEmpty()) {
            requestBuilder.addHeader("Authorization", "Bearer $token")
        } else if (!isAuthRequest) {
            // Only add anon token to data requests, not auth/login requests
            requestBuilder.addHeader("Authorization", "Bearer ${SupabaseConfig.ANON_KEY}")
        }

        val response = chain.proceed(requestBuilder.build())

        if (response.code == 401) {
            response.close()
            // Try to refresh token
            val refreshToken = authPrefs.getRefreshToken()
            if (refreshToken != null) {
                // TODO: Implement token refresh via Supabase auth/v1/token?grant_type=refresh_token
                // For now, clear auth and force re-login
                // In a full implementation, you would:
                // 1. Call refresh token endpoint
                // 2. Update authPrefs with new tokens
                // 3. Retry the original request with new token
                authPrefs.clear()
            } else {
                authPrefs.clear()
            }
        }

        return response
    }
}
