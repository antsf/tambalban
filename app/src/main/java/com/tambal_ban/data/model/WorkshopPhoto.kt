
package com.tambal_ban.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class WorkshopPhoto(
    val id: String? = null,
    @SerialName("workshop_id") val workshopId: String,
    @SerialName("image_url") val imageUrl: String,
    @SerialName("created_at") val createdAt: String? = null
)
