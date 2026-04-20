package com.tambal_ban.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Data model for User Profile.
 */
@Serializable
data class Profile(
    @SerialName("id")
    val id: String,
    
    @SerialName("full_name")
    val fullName: String? = null,
    
    @SerialName("email")
    val email: String? = null,
    
    @SerialName("phone")
    val phone: String? = null,
    
    @SerialName("avatar_url")
    val avatarUrl: String? = null,
    
    @SerialName("updated_at")
    val updatedAt: String? = null
)
