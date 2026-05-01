package com.tambal_ban.workshop.data

import androidx.annotation.ColorRes

/**
 * UI State for the Workshop Detail screen.
 */
data class WorkshopDetailUIState(
    val id: String,
    val name: String,
    val imageUrl: String?,
    val fullAddress: String,
    val phoneNumber: String,
    val businessHours: String,
    val ratingAvg: String,
    val reviewCountText: String,
    val statusText: String,
    @ColorRes val statusColorRes: Int,
    val is24h: Boolean
)
