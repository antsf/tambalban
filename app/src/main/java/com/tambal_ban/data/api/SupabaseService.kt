package com.tambal_ban.data.api

import com.tambal_ban.data.model.*
import retrofit2.Response
import retrofit2.http.*

/** Retrofit interface for Supabase REST API and Auth API. */
interface SupabaseService {

        // --- Authentication ---

        @POST("auth/v1/token?grant_type=password")
        suspend fun login(@Body request: LoginRequest): Response<AuthResponse>

        @POST("auth/v1/signup")
        suspend fun register(@Body request: LoginRequest): Response<AuthResponse>

        @POST("auth/v1/recover")
        suspend fun recover(@Body request: Map<String, String>): Response<Void>

        // --- Workshops ---

        @GET("rest/v1/workshops")
        suspend fun getWorkshops(
                @Query("verified") verified: String = "eq.true",
                @Query("latitude") minLat: String? = null,
                @Query("latitude") maxLat: String? = null,
                @Query("longitude") minLng: String? = null,
                @Query("longitude") maxLng: String? = null,
                @Query("select") select: String = "*",
                @Header("Range") range: String? = null
        ): Response<List<Workshop>>

        @GET("rest/v1/workshops")
        suspend fun getWorkshopById(@Query("id") id: String): Response<List<Workshop>>

        @GET("rest/v1/workshops")
        suspend fun searchWorkshops(
                @Query("name") name: String,
                @Query("verified") verified: String = "eq.true",
                @Query("select") select: String = "*",
                @Header("Range") range: String? = null
        ): Response<List<Workshop>>

        // --- Reviews ---

        @GET("rest/v1/reviews")
        suspend fun getReviews(@Query("workshop_id") workshopId: String): Response<List<Review>>

        @POST("rest/v1/reviews") suspend fun submitReview(@Body review: Review): Response<Void>

        // --- Submissions ---

        @POST("rest/v1/workshop_submissions")
        suspend fun submitWorkshop(@Body submission: WorkshopSubmission): Response<Void>

        @GET("rest/v1/workshop_submissions")
        suspend fun getUserSubmissions(
                @Query("user_id") userId: String
        ): Response<List<WorkshopSubmission>>

        // --- Storage ---

        @GET("rest/v1/workshop_photos")
        suspend fun getWorkshopPhotos(
                @Query("workshop_id") workshopId: String
        ): Response<List<WorkshopPhoto>>

        @POST("rest/v1/workshop_photos")
        suspend fun addWorkshopPhoto(@Body photo: WorkshopPhoto): Response<Void>

        @POST("storage/v1/object/{bucket}/{path}")
        suspend fun uploadPhoto(
                @Path("bucket") bucket: String,
                @Path("path") path: String,
                @Body file: okhttp3.RequestBody,
                @Header("Content-Type") contentType: String = "image/jpeg"
        ): Response<okhttp3.ResponseBody>
}
