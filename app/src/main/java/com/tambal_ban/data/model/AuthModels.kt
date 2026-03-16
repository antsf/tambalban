package com.tambal_ban.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LoginRequest(
        val email: String,
        val password: String,
        val data: Map<String, String>? = null
)

@Serializable
data class AuthResponse(
        @SerialName("access_token") val accessToken: String,
        @SerialName("refresh_token") val refreshToken: String,
        val user: User
)

@Serializable
data class User(
        val id: String,
        val email: String? = null,
        @SerialName("user_metadata") val userMetadata: UserMetadata? = null
)

@Serializable data class UserMetadata(@SerialName("full_name") val fullName: String? = null)
