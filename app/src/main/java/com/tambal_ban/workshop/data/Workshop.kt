package com.tambal_ban.workshop.data
import com.tambal_ban.workshop.ui.* 
import com.tambal_ban.workshop.viewmodel.* 
import com.tambal_ban.workshop.data.* 

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
    @SerialName("open_time")
    val openTime: String?,
    @SerialName("close_time")
    val closeTime: String?,
    @SerialName("is_24h")
    val is24h: Boolean = false,
    @SerialName("rating_avg")
    val ratingAvg: Double = 0.0,
    @SerialName("rating_count")
    val ratingCount: Int = 0,
    val source: String? = null,
    @SerialName("created_at")
    val createdAt: String? = null,
    @SerialName("image_url")
    val imageUrl: String? = null,

    // UI fields
    @kotlinx.serialization.Transient
    var distance: Double? = null
)


