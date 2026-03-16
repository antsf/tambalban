package com.tambal_ban.data.repository

import com.tambal_ban.data.api.SupabaseService
import com.tambal_ban.data.model.WorkshopSubmission
import com.tambal_ban.utils.SupabaseConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody

class SubmissionRepository(private val supabaseService: SupabaseService) {

    suspend fun submitWorkshop(submission: WorkshopSubmission): Result<Unit> =
            withContext(Dispatchers.IO) {
                try {
                    val response = supabaseService.submitWorkshop(submission)
                    if (response.isSuccessful) {
                        Result.success(Unit)
                    } else {
                        Result.failure(Exception("Submission failed: ${response.message()}"))
                    }
                } catch (e: Exception) {
                    Result.failure(e)
                }
            }

    suspend fun getUserSubmissions(userId: String): Result<List<WorkshopSubmission>> =
            withContext(Dispatchers.IO) {
                try {
                    val response = supabaseService.getUserSubmissions(userId)
                    if (response.isSuccessful) {
                        Result.success(response.body() ?: emptyList())
                    } else {
                        Result.failure(
                                Exception("Failed to fetch submissions: ${response.message()}")
                        )
                    }
                } catch (e: Exception) {
                    Result.failure(e)
                }
            }

    suspend fun uploadPhoto(fileName: String, bytes: ByteArray): Result<String> =
            withContext(Dispatchers.IO) {
                try {
                    val requestBody = bytes.toRequestBody("image/jpeg".toMediaTypeOrNull())
                    val response = supabaseService.uploadPhoto("workshops", fileName, requestBody)
                    if (response.isSuccessful) {
                        // Return the public URL. In Supabase:
                        // base_url/storage/v1/object/public/bucket/path
                        // For now, let's just return the path to be used in some way,
                        // or hardcode the public URL if we know the bucket is public.
                        val publicUrl =
                                "${SupabaseConfig.URL}storage/v1/object/public/workshops/$fileName"
                        Result.success(publicUrl)
                    } else {
                        Result.failure(Exception("Upload failed: ${response.message()}"))
                    }
                } catch (e: Exception) {
                    Result.failure(e)
                }
            }
}
