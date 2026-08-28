package com.tambal_ban.workshop.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * `source`/`verified` are deliberately absent — the v2 API always sets these from the
 * caller's session (source='user', verified=false), silently ignoring anything sent here.
 */
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
    val imageUrl: String? = null
)
