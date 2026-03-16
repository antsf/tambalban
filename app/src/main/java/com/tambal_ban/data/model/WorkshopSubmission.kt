package com.tambal_ban.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class WorkshopSubmission(
        val id: String? = null,
        val name: String,
        val address: String,
        val latitude: Double,
        val longitude: Double,
        val phone: String,
        @SerialName("user_id") val userId: String? = null,
        @SerialName("photo_url") val photoUrl: String? = null,
        val status: String = "pending"
)
