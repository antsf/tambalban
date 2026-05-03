package com.tambal_ban.workshop.data

import androidx.annotation.ColorRes

data class WorkshopDetailUIState(
    val id: String,
    val name: String,
    val imageUrl: String?,
    val fullAddress: String,
    val city: String?,
    val province: String?,
    val phoneNumber: String,
    val openingHours: String,
    val rating: String,
    val reviewCountText: String,
    val statusText: String,
    @ColorRes val statusColorRes: Int
)
