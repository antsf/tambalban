package com.tambal_ban.data.api

import com.tambal_ban.utils.AuthPrefs
import com.tambal_ban.utils.Constants
import okhttp3.Interceptor
import okhttp3.Response

/**
 * Interceptor that adds Supabase API Key and Bearer Token to outgoing requests.
 */
class AuthInterceptor(private val authPrefs: AuthPrefs) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val requestBuilder = originalRequest.newBuilder()
            .addHeader("apikey", Constants.SUPABASE_ANON_KEY)

        val token = authPrefs.getAccessToken()
        if (token != null) {
            requestBuilder.addHeader("Authorization", "Bearer $token")
        } else {
            requestBuilder.addHeader("Authorization", "Bearer ${Constants.SUPABASE_ANON_KEY}")
        }

        val response = chain.proceed(requestBuilder.build())

        if (response.code == 401) {
            // Handle unauthorized error - could trigger token refresh or logout
            authPrefs.clear()
        }

        return response
    }
}
