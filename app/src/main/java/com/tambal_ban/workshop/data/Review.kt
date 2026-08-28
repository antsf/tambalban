package com.tambal_ban.workshop.data
import com.tambal_ban.workshop.ui.*
import com.tambal_ban.workshop.viewmodel.*
import com.tambal_ban.workshop.data.*

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Review(
    val id: String? = null,
    @SerialName("workshop_id")
    val workshopId: String? = null,
    @SerialName("user_id")
    val userId: String? = null,
    val rating: Int,
    val comment: String?,
    @SerialName("created_at")
    val createdAt: String? = null
)

/** Submission payload for POST /api/v2/workshops/:id/reviews — workshop_id comes from the
 * URL path, user_id from the session; neither is sent in the body. */
@Serializable
data class ReviewRequest(
    val rating: Int,
    val comment: String? = null
)
