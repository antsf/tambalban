package com.tambal_ban.data.repository

import com.tambal_ban.data.api.SupabaseService
import com.tambal_ban.data.model.Review
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ReviewRepository(private val supabaseService: SupabaseService) {

    suspend fun getReviews(workshopId: String): Result<List<Review>> =
        withContext(Dispatchers.IO) {
            try {
                val response = supabaseService.getReviews(workshopId)
                if (response.isSuccessful) {
                    Result.success(response.body() ?: emptyList())
                } else {
                    Result.failure(Exception("Failed to fetch reviews: ${response.message()}"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }

    suspend fun submitReview(review: Review): Result<Unit> =
        withContext(Dispatchers.IO) {
            try {
                val response = supabaseService.submitReview(review)
                if (response.isSuccessful) {
                    Result.success(Unit)
                } else {
                    Result.failure(Exception("Failed to submit review: ${response.message()}"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
}
