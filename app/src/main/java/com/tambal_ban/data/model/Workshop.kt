package com.tambal_ban.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/** Data class representing a tire repair workshop */
@Serializable
data class Workshop(
        val id: String,
        val name: String,
        val latitude: Double,
        val longitude: Double,
        val phone: String?,
        val address: String?,
        @SerialName("open_time") val openTime: String?,
        @SerialName("close_time") val closeTime: String?,
        @SerialName("is_24h") val is24h: Boolean = false,
        @SerialName("rating_avg") val ratingAvg: Double = 0.0,
        @SerialName("rating_count") val ratingCount: Int = 0,
        val source: String? = null,
        @SerialName("created_at") val createdAt: String? = null,
        @SerialName("photo_url") val photoUrl: String? = null,
        val verified: Boolean = false,

        // UI fields
        @kotlinx.serialization.Transient var distance: Double? = null,
        @kotlinx.serialization.Transient var photos: List<String> = emptyList()
)
