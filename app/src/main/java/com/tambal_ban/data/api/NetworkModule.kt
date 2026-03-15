package com.tambal_ban.data.api

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import com.tambal_ban.utils.AuthPrefs
import com.tambal_ban.utils.SupabaseConfig
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit

/**
 * Module providing Network-related instances.
 */
object NetworkModule {

    private val json = Json {
        ignoreUnknownKeys = true
        coerceInputValues = true
        isLenient = true
    }

    fun provideSupabaseService(authPrefs: AuthPrefs): SupabaseService {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        val client = OkHttpClient.Builder()
            .addInterceptor(AuthInterceptor(authPrefs))
            .addInterceptor(loggingInterceptor)
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl(SupabaseConfig.URL)
            .client(client)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()

        return retrofit.create(SupabaseService::class.java)
    }
}
