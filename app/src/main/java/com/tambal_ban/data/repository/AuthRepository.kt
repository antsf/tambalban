package com.tambal_ban.data.repository

import com.tambal_ban.data.api.SupabaseService
import com.tambal_ban.data.model.*
import com.tambal_ban.utils.AuthPrefs
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Repository for authentication-related data operations.
 */
class AuthRepository(
    private val supabaseService: SupabaseService,
    private val authPrefs: AuthPrefs
) {

    suspend fun login(email: String, password: String): Result<AuthResponse> =
        withContext(Dispatchers.IO) {
            try {
                val response = supabaseService.login(LoginRequest(email, password))
                if (response.isSuccessful && response.body() != null) {
                    val authResponse = response.body()!!
                    authPrefs.saveAccessToken(authResponse.accessToken)
                    authPrefs.saveRefreshToken(authResponse.refreshToken)
                    authPrefs.saveUserId(authResponse.user.id)
                    authResponse.user.email?.let { authPrefs.saveEmail(it) }
                    Result.success(authResponse)
                } else {
                    Result.failure(Exception("Login failed: ${response.message()}"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }

    suspend fun register(name: String, email: String, password: String): Result<AuthResponse> =
        withContext(Dispatchers.IO) {
            try {
                val metadata = mapOf("full_name" to name)
                val response = supabaseService.register(RegisterRequest(email, password, metadata))
                if (response.isSuccessful && response.body() != null) {
                    val authResponse = response.body()!!
                    authPrefs.saveAccessToken(authResponse.accessToken)
                    authPrefs.saveRefreshToken(authResponse.refreshToken)
                    authPrefs.saveUserId(authResponse.user.id)
                    authPrefs.saveEmail(authResponse.user.email ?: email)
                    Result.success(authResponse)
                } else {
                    Result.failure(Exception("Registration failed: ${response.message()}"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }

    fun logout() {
        authPrefs.clear()
    }

    fun isLoggedIn(): Boolean = authPrefs.isLoggedIn()

    fun getAccessToken(): String? = authPrefs.getAccessToken()

    fun getUserId(): String? = authPrefs.getUserId()

    fun getUserEmail(): String? = authPrefs.getEmail()
}
