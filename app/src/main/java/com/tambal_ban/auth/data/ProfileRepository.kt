package com.tambal_ban.auth.data
import com.tambal_ban.auth.ui.*
import com.tambal_ban.auth.viewmodel.*
import com.tambal_ban.auth.data.*

import com.tambal_ban.core.network.TambalBanApiService
import com.tambal_ban.core.utils.CrashlyticsHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody

/**
 * Repository for user profile data operations. The bearer token identifies the caller
 * server-side — unlike Supabase's RLS-scoped `users_profile` table, there's no `userId`
 * parameter to pass; a request always operates on the token's own account.
 */
class ProfileRepository(
    private val apiService: TambalBanApiService
) {

    suspend fun getProfile(): Result<Profile> =
        withContext(Dispatchers.IO) {
            try {
                val response = apiService.getProfile()
                if (response.isSuccessful && response.body() != null) {
                    Result.success(response.body()!!)
                } else {
                    Result.failure(Exception("Failed to fetch profile: ${response.message()}"))
                }
            } catch (e: Exception) {
                CrashlyticsHelper.logNonFatal(e, "getProfile failed")
                Result.failure(e)
            }
        }

    suspend fun updateProfile(updates: Map<String, String>): Result<Profile> =
        withContext(Dispatchers.IO) {
            try {
                val response = apiService.updateProfile(updates)
                if (response.isSuccessful && response.body() != null) {
                    Result.success(response.body()!!)
                } else {
                    Result.failure(Exception("Failed to update profile: ${response.message()}"))
                }
            } catch (e: Exception) {
                CrashlyticsHelper.logNonFatal(e, "updateProfile failed")
                Result.failure(e)
            }
        }

    suspend fun uploadAvatar(bytes: ByteArray, mimeType: String): Result<String> =
        withContext(Dispatchers.IO) {
            try {
                val body = bytes.toRequestBody(mimeType.toMediaType())
                val part = MultipartBody.Part.createFormData("file", "avatar", body)
                val response = apiService.uploadAvatarImage(part)
                if (response.isSuccessful && response.body() != null) {
                    Result.success(response.body()!!.url)
                } else {
                    Result.failure(Exception("Failed to upload avatar: ${response.message()}"))
                }
            } catch (e: Exception) {
                CrashlyticsHelper.logNonFatal(e, "uploadAvatar failed")
                Result.failure(e)
            }
        }
}
