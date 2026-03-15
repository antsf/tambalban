package com.tambalban.data.api

import com.tambalban.data.model.*
import retrofit2.Response
import retrofit2.http.*

/**
 * Retrofit interface for Supabase REST API and Auth API.
 */
interface SupabaseService {

    // --- Authentication ---

    @POST("auth/v1/token?grant_type=password")
    suspend fun login(
        @Body request: LoginRequest
    ): Response<AuthResponse>

    @POST("auth/v1/signup")
    suspend fun register(
        @Body request: LoginRequest
    ): Response<AuthResponse>


    // --- Workshops ---

    @GET("rest/v1/workshops")
    suspend fun getWorkshops(
        @Query("verified") verified: String = "eq.true",
        @Query("latitude") minLat: String?,
        @Query("latitude") maxLat: String?,
        @Query("longitude") minLng: String?,
        @Query("longitude") maxLng: String?
    ): Response<List<Workshop>>
    
    @GET("rest/v1/workshops")
    suspend fun getWorkshopById(
        @Query("id") id: String
    ): Response<Workshop>


    // --- Reviews ---

    @GET("rest/v1/reviews")
    suspend fun getReviews(
        @Query("workshop_id") workshopId: String
    ): Response<List<Review>>

    @POST("rest/v1/reviews")
    suspend fun submitReview(
        @Body review: Review
    ): Response<Void>


    // --- Submissions ---

    @POST("rest/v1/workshop_submissions")
    suspend fun submitWorkshop(
        @Body submission: WorkshopSubmission
    ): Response<Void>

    @GET("rest/v1/workshop_submissions")
    suspend fun getUserSubmissions(
        @Query("user_id") userId: String
    ): Response<List<WorkshopSubmission>>
}
