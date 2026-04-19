package com.tambal_ban.data.api

import com.tambal_ban.data.model.*
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
    suspend fun getWorkshopsByName(
        @Query("name") nameQuery: String,
        @Query("verified") verified: String = "eq.true"
    ): Response<List<Workshop>>

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
    ): Response<List<Workshop>>

    @POST("rest/v1/rpc/nearby_workshops")
    suspend fun getNearbyWorkshops(
        @Body request: NearbyRequest
    ): Response<List<Workshop>>


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


    // --- User Profiles ---

    @GET("rest/v1/profiles")
    suspend fun getProfile(
        @Query("id") id: String = "eq.current_user",
        @Query("select") select: String = "*"
    ): Response<List<Profile>>

    @PATCH("rest/v1/profiles")
    suspend fun updateProfile(
        @Query("id") id: String,
        @Body profile: Map<String, String>
    ): Response<Void>


    // --- Storage ---

    @POST("storage/v1/object/avatars/{path}")
    suspend fun uploadAvatar(
        @Path("path") path: String,
        @Body body: okhttp3.RequestBody
    ): Response<Void>
}
