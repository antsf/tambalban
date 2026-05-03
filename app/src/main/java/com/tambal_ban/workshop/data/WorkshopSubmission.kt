package com.tambal_ban.workshop.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class WorkshopSubmission(
    val name: String,
    val address: String,
    val city: String,
    val lat: Double,
    val lon: Double,
    val phone: String,
    val province: String? = null,
    @SerialName("opening_hours")
    val openingHours: String? = null,
    @SerialName("image_url")
    val imageUrl: String? = null,
    val source: String = "user",
    val verified: Boolean = false
)
