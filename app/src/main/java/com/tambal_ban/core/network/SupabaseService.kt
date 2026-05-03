package com.tambal_ban.core.network
import com.tambal_ban.auth.data.*
import com.tambal_ban.workshop.data.*

import com.tambal_ban.auth.*
import com.tambal_ban.workshop.*
import retrofit2.Response
import retrofit2.http.*

interface SupabaseService {

    // --- Authentication ---

    @POST("auth/v1/token?grant_type=password")
    suspend fun login(@Body request: LoginRequest): Response<AuthResponse>

    @POST("auth/v1/signup")
    suspend fun register(@Body request: RegisterRequest): Response<AuthResponse>


    // --- Workshops ---

    @GET("rest/v1/tambal_ban")
    suspend fun getWorkshopsInBounds(
        @Query("lat") minLat: String,
        @Query("lat") maxLat: String,
        @Query("lon") minLon: String,
        @Query("lon") maxLon: String,
        @Query("verified") verified: String = "eq.true",
        @Query("limit") limit: Int? = null,
        @Query("offset") offset: Int? = null
    ): Response<List<Workshop>>

    @GET("rest/v1/tambal_ban")
    suspend fun searchWorkshops(
        @Query("or") or: String, // format: (name.ilike.*query*,city.ilike.*query*)
        @Query("verified") verified: String = "eq.true",
        @Query("limit") limit: Int? = null,
        @Query("offset") offset: Int? = null
    ): Response<List<Workshop>>

    @GET("rest/v1/tambal_ban")
    suspend fun getAllWorkshops(
        @Query("verified") verified: String = "eq.true",
        @Query("limit") limit: Int? = null,
        @Query("offset") offset: Int? = null
    ): Response<List<Workshop>>

    @GET("rest/v1/tambal_ban")
    suspend fun getWorkshopById(
        @Query("id") id: String // format: eq.uuid
    ): Response<List<Workshop>>

    @GET("rest/v1/reviews")
    suspend fun getReviews(@Query("workshop_id") workshopId: String): Response<List<Review>>

    @POST("rest/v1/reviews")
    suspend fun submitReview(@Body review: Review): Response<Void>

    @Headers("Prefer: return=representation")
    @POST("rest/v1/tambal_ban")
    suspend fun addWorkshop(@Body submission: WorkshopSubmission): Response<List<Workshop>>


    // --- User Profiles ---

    @GET("rest/v1/users_profile")
    suspend fun getProfile(
        @Query("id") id: String = "eq.current_user",
        @Query("select") select: String = "*"
    ): Response<List<Profile>>

    @PATCH("rest/v1/users_profile")
    suspend fun updateProfile(
        @Query("id") id: String,
        @Body profile: Map<String, String>
    ): Response<Void>


    // --- Storage ---

    @POST("storage/v1/object/{bucket}/{path}")
    suspend fun uploadFile(
        @Path("bucket") bucket: String,
        @Path("path", encoded = true) path: String,
        @Body body: okhttp3.RequestBody
    ): Response<Void>
}
