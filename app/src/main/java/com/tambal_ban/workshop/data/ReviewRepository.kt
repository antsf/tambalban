package com.tambal_ban.workshop.data
import com.tambal_ban.workshop.ui.* 
import com.tambal_ban.workshop.viewmodel.* 
import com.tambal_ban.workshop.data.* 

import com.tambal_ban.core.network.SupabaseService
import com.tambal_ban.workshop.data.Review
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
