package com.tambal_ban.data.repository

import com.tambal_ban.data.api.SupabaseService
import com.tambal_ban.data.model.WorkshopSubmission
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

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
                    Result.failure(Exception("Failed to fetch submissions: ${response.message()}"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
}
