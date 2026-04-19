package com.tambal_ban.data.model

import com.google.gson.annotations.SerializedName

/**
 * Data model for User Profile.
 */
data class Profile(
    @SerializedName("id")
    val id: String,
    
    @SerializedName("full_name")
    val fullName: String,
    
    @SerializedName("email")
    val email: String,
    
    @SerializedName("phone")
    val phone: String,
    
    @SerializedName("avatar_url")
    val avatarUrl: String?,
    
    @SerializedName("updated_at")
    val updatedAt: String? = null
)
