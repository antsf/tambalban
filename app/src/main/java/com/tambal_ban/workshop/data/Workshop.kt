package com.tambal_ban.workshop.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Workshop(
    val id: String,
    val name: String,
    val lat: Double,
    val lon: Double,
    val phone: String? = null,
    val address: String? = null,
    val city: String? = null,
    val province: String? = null,
    @SerialName("opening_hours")
    val openingHours: String? = null,
    val rating: Double = 0.0,
    @SerialName("total_reviews")
    val totalReviews: Int = 0,
    @SerialName("image_url")
    val imageUrl: String? = null,
    val source: String? = null,
    val verified: Boolean = false,
    @SerialName("created_at")
    val createdAt: String? = null,

    @kotlinx.serialization.Transient
    var distance: Double? = null
)
