package com.tambal_ban.auth.data
import com.tambal_ban.auth.ui.*
import com.tambal_ban.auth.viewmodel.*
import com.tambal_ban.auth.data.*

import com.tambal_ban.core.network.TambalBanApiService
import com.tambal_ban.core.utils.CrashlyticsHelper
import com.tambal_ban.workshop.*
import com.tambal_ban.core.utils.AuthPrefs
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Repository for authentication-related data operations.
 */
class AuthRepository(
    private val apiService: TambalBanApiService,
    private val authPrefs: AuthPrefs
) {

    suspend fun login(email: String, password: String): Result<AuthResponse> =
        withContext(Dispatchers.IO) {
            try {
                val response = apiService.login(LoginRequest(email, password))
                if (response.isSuccessful && response.body() != null) {
                    val authResponse = response.body()!!
                    authPrefs.saveAccessToken(authResponse.token)
                    authPrefs.saveUserId(authResponse.user.id)
                    authResponse.user.email?.let { authPrefs.saveEmail(it) }
                    Result.success(authResponse)
                } else {
                    Result.failure(Exception("Login failed: ${response.message()}"))
                }
            } catch (e: Exception) {
                CrashlyticsHelper.logNonFatal(e, "login failed")
                Result.failure(e)
            }
        }

    suspend fun register(name: String, email: String, password: String): Result<AuthResponse> =
        withContext(Dispatchers.IO) {
            try {
                val response = apiService.register(RegisterRequest(email, password))
                if (response.isSuccessful && response.body() != null) {
                    val authResponse = response.body()!!
                    authPrefs.saveAccessToken(authResponse.token)
                    authPrefs.saveUserId(authResponse.user.id)
                    authPrefs.saveEmail(authResponse.user.email ?: email)
                    Result.success(authResponse)
                } else {
                    Result.failure(Exception("Registration failed: ${response.message()}"))
                }
            } catch (e: Exception) {
                CrashlyticsHelper.logNonFatal(e, "register failed")
                Result.failure(e)
            }
        }

    /** Revokes the session server-side before clearing local prefs — previously this only
     * cleared local prefs, leaving the D1 session row live until its 30-day expiry. */
    suspend fun logout(): Unit = withContext(Dispatchers.IO) {
        try {
            apiService.logout()
        } catch (e: Exception) {
            CrashlyticsHelper.logNonFatal(e, "logout failed")
        } finally {
            authPrefs.clear()
        }
    }

    fun isLoggedIn(): Boolean = authPrefs.isLoggedIn()

    fun getAccessToken(): String? = authPrefs.getAccessToken()

    fun getUserId(): String? = authPrefs.getUserId()

    fun getUserEmail(): String? = authPrefs.getEmail()
}
