package com.tambal_ban.data.model

/** Data class representing a tire repair workshop */
data class Workshop(
    val id: String,
    val name: String,
    val latitude: Double,
    val longitude: Double,
    val phone: String?,
    val address: String?,
    val openTime: String?,
    val closeTime: String?,
    val is24h: Boolean = false,
    val ratingAvg: Double = 0.0,
    val ratingCount: Int = 0,
    val source: String?,
    val createdAt: String? = null,

    // UI fields
    var distance: Double? = null
)

/** Review data class */
data class Review(
    val id: String,
    val workshopId: String,
    val userId: String?,
    val rating: Int,
    val comment: String?,
    val createdAt: String?
)
