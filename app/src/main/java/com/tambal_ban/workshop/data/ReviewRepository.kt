package com.tambal_ban.workshop.data
import com.tambal_ban.workshop.ui.*
import com.tambal_ban.workshop.viewmodel.*
import com.tambal_ban.workshop.data.*

import com.tambal_ban.core.network.TambalBanApiService
import com.tambal_ban.core.utils.CrashlyticsHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ReviewRepository(private val apiService: TambalBanApiService) {

    suspend fun getReviews(workshopId: String): Result<List<Review>> =
        withContext(Dispatchers.IO) {
            try {
                val response = apiService.getReviews(workshopId)
                if (response.isSuccessful) {
                    Result.success(response.body() ?: emptyList())
                } else {
                    Result.failure(Exception("Failed to fetch reviews: ${response.message()}"))
                }
            } catch (e: Exception) {
                CrashlyticsHelper.logNonFatal(e, "getReviews failed")
                Result.failure(e)
            }
        }

    suspend fun submitReview(workshopId: String, rating: Int, comment: String?): Result<Review> =
        withContext(Dispatchers.IO) {
            try {
                val response = apiService.submitReview(workshopId, ReviewRequest(rating, comment))
                if (response.isSuccessful && response.body() != null) {
                    Result.success(response.body()!!)
                } else {
                    Result.failure(Exception("Failed to submit review: ${response.message()}"))
                }
            } catch (e: Exception) {
                CrashlyticsHelper.logNonFatal(e, "submitReview failed")
                Result.failure(e)
            }
        }
}
