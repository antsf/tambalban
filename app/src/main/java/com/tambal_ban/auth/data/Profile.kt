package com.tambal_ban.auth.data
import com.tambal_ban.auth.ui.*
import com.tambal_ban.auth.viewmodel.*
import com.tambal_ban.auth.data.*

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Data model for User Profile — matches D1's `users` row shape
 * (tambalban-web/worker/src/lib/d1.ts's UserRow), never includes password_hash.
 */
@Serializable
data class Profile(
    val id: String,

    val username: String? = null,

    @SerialName("full_name")
    val fullName: String? = null,

    val email: String? = null,

    val phone: String? = null,

    @SerialName("avatar_url")
    val avatarUrl: String? = null,

    @SerialName("created_at")
    val createdAt: String? = null,

    @SerialName("updated_at")
    val updatedAt: String? = null
)
