package com.tambal_ban.auth.data
import com.tambal_ban.auth.ui.* 
import com.tambal_ban.auth.viewmodel.* 
import com.tambal_ban.auth.data.* 

import android.util.Log
import com.tambal_ban.core.network.SupabaseService
import com.tambal_ban.core.utils.CrashlyticsHelper
import com.tambal_ban.auth.data.Profile
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

/**
 * Repository for user profile data operations.
 */
class ProfileRepository(
    private val supabaseService: SupabaseService
) {

    suspend fun getProfile(userId: String): Result<Profile> =
        withContext(Dispatchers.IO) {
            try {
                val response = supabaseService.getProfile("eq.$userId")
                if (response.isSuccessful && response.body() != null) {
                    val profileList = response.body()!!
                    Log.d("Profile", "response: ${profileList}")

                    if (profileList.isNotEmpty()) {
                        val profile = profileList[0]
                        // Transform avatar path to full URL
                        val fullProfile = if (!profile.avatarUrl.isNullOrEmpty() && !profile.avatarUrl.startsWith("http")) {
                            profile.copy(avatarUrl = getPublicUrl("avatars", profile.avatarUrl))
                        } else {
                            profile
                        }
                        Result.success(fullProfile)
                    } else {
                        Result.failure(Exception("Profile not found"))
                    }
                } else {
                    Result.failure(Exception("Failed to fetch profile: ${response.message()}"))
                }
            } catch (e: Exception) {
                CrashlyticsHelper.logNonFatal(e, "getProfile failed")
                Result.failure(e)
            }
        }

    suspend fun updateProfile(userId: String, updates: Map<String, String>): Result<Unit> =
        withContext(Dispatchers.IO) {
            try {
                val response = supabaseService.updateProfile("eq.$userId", updates)
                if (response.isSuccessful) {
                    Result.success(Unit)
                } else {
                    Result.failure(Exception("Failed to update profile: ${response.message()}"))
                }
            } catch (e: Exception) {
                CrashlyticsHelper.logNonFatal(e, "updateProfile failed")
                Result.failure(e)
            }
        }

    suspend fun uploadAvatar(userId: String, bytes: ByteArray, mimeType: String): Result<String> =
        withContext(Dispatchers.IO) {
            try {
                val filename = "avatar_${System.currentTimeMillis()}.png"
                val path = "$userId/$filename"
                val requestBody = bytes.toRequestBody(mimeType.toMediaType())
                
                val response = supabaseService.uploadFile("avatars", path, requestBody)
                if (response.isSuccessful) {
                    val publicUrl = getPublicUrl("avatars", path)
                    Result.success(publicUrl)
                } else {
                    Result.failure(Exception("Failed to upload avatar: ${response.message()}"))
                }
            } catch (e: Exception) {
                CrashlyticsHelper.logNonFatal(e, "uploadAvatar failed")
                Result.failure(e)
            }
        }

    private fun getPublicUrl(bucket: String = "avatars", path: String): String {
        return "${com.tambal_ban.core.utils.SupabaseConfig.URL}storage/v1/object/public/$bucket/$path"
    }
}
