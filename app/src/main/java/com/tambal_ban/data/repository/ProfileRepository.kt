package com.tambal_ban.data.repository

import com.tambal_ban.data.api.SupabaseService
import com.tambal_ban.data.model.Profile
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
                    if (profileList.isNotEmpty()) {
                        Result.success(profileList[0])
                    } else {
                        Result.failure(Exception("Profile not found"))
                    }
                } else {
                    Result.failure(Exception("Failed to fetch profile: ${response.message()}"))
                }
            } catch (e: Exception) {
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
                Result.failure(e)
            }
        }

    suspend fun uploadAvatar(userId: String, bytes: ByteArray, mimeType: String): Result<String> =
        withContext(Dispatchers.IO) {
            try {
                val filename = "avatar_${System.currentTimeMillis()}.png"
                val path = "$userId/$filename"
                val requestBody = bytes.toRequestBody(mimeType.toMediaType())
                
                val response = supabaseService.uploadAvatar(path, requestBody)
                if (response.isSuccessful) {
                    // In Supabase, the public URL is usually:
                    // https://{project_id}.supabase.co/storage/v1/object/public/avatars/{path}
                    // For simplicity, we'll return the path and let the UI/Service handle the base URL or 
                    // assume we get the URL from the backend update.
                    // Actually, we should return the final URL.
                    // For now, let's just return the path to update the profile table.
                    Result.success(path)
                } else {
                    Result.failure(Exception("Failed to upload avatar: ${response.message()}"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
}
