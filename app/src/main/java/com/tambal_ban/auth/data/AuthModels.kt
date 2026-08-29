package com.tambal_ban.auth.data
import com.tambal_ban.auth.ui.*
import com.tambal_ban.auth.viewmodel.*
import com.tambal_ban.auth.data.*

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LoginRequest(
    val email: String,
    val password: String
)

@Serializable
data class RegisterRequest(
    val email: String,
    val password: String
)

/** D1 sessions are a single opaque token with an expiry — no separate refresh token
 * (unlike Supabase Auth's access/refresh pair this replaced). */
@Serializable
data class AuthResponse(
    val token: String,
    @SerialName("expires_at")
    val expiresAt: String,
    val user: User
)

@Serializable
data class User(
    val id: String,
    val email: String? = null
)
