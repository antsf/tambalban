package com.tambal_ban.core.network
import com.tambal_ban.auth.data.*
import com.tambal_ban.workshop.data.*

import com.tambal_ban.auth.*
import com.tambal_ban.workshop.*
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.*

interface TambalBanApiService {

    // --- Authentication ---

    @POST("api/v2/auth/register")
    suspend fun register(@Body request: RegisterRequest): Response<AuthResponse>

    @POST("api/v2/auth/login")
    suspend fun login(@Body request: LoginRequest): Response<AuthResponse>

    @POST("api/v2/auth/logout")
    suspend fun logout(): Response<Unit>


    // --- Workshops ---

    @GET("api/v2/workshops")
    suspend fun getWorkshops(
        @Query("minLat") minLat: Double? = null,
        @Query("maxLat") maxLat: Double? = null,
        @Query("minLng") minLng: Double? = null,
        @Query("maxLng") maxLng: Double? = null,
        @Query("search") search: String? = null,
    ): Response<List<Workshop>>

    @GET("api/v2/workshops/{id}")
    suspend fun getWorkshopById(@Path("id") id: String): Response<Workshop>

    @GET("api/v2/workshops/{id}/reviews")
    suspend fun getReviews(@Path("id") workshopId: String): Response<List<Review>>

    @POST("api/v2/workshops/{id}/reviews")
    suspend fun submitReview(@Path("id") workshopId: String, @Body review: ReviewRequest): Response<Review>

    @POST("api/v2/workshops")
    suspend fun addWorkshop(@Body submission: WorkshopSubmission): Response<Workshop>


    // --- User Profile ---

    @GET("api/v2/profile")
    suspend fun getProfile(): Response<Profile>

    @PATCH("api/v2/profile")
    suspend fun updateProfile(@Body updates: Map<String, String>): Response<Profile>


    // --- Uploads (R2) ---

    @Multipart
    @POST("api/v2/upload/workshop")
    suspend fun uploadWorkshopImage(@Part file: MultipartBody.Part): Response<UploadResponse>

    @Multipart
    @POST("api/v2/upload/avatar")
    suspend fun uploadAvatarImage(@Part file: MultipartBody.Part): Response<UploadResponse>
}
