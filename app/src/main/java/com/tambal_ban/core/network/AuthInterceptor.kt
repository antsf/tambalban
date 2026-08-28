package com.tambal_ban.core.network

import com.tambal_ban.core.utils.AuthPrefs
import okhttp3.Interceptor
import okhttp3.Response

/**
 * Adds the session Bearer token to outgoing requests. Unlike Supabase (which needed an
 * `apikey` header plus an anon-key fallback for RLS on unauthenticated requests), the D1
 * bearer API needs no header at all for public routes — anonymous requests go through
 * untouched.
 */
class AuthInterceptor(private val authPrefs: AuthPrefs) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val token = authPrefs.getAccessToken()
        val isAuthRequest = originalRequest.url.toString().contains("/api/v2/auth/")

        val request = if (!token.isNullOrEmpty()) {
            originalRequest.newBuilder().header("Authorization", "Bearer $token").build()
        } else {
            originalRequest
        }

        val response = chain.proceed(request)

        if (response.code == 401 && !isAuthRequest) {
            authPrefs.clear()
        }

        return response
    }
}
