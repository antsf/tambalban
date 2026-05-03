package com.tambal_ban.workshop.viewmodel

import android.net.Uri

data class AddWorkshopFormState(
    val name: String = "",
    val address: String = "",
    val city: String = "",
    val phone: String = "",
    val province: String = "",
    val openingHours: String = "",
    val lat: Double = -6.2,  // Jakarta default
    val lon: Double = 106.8,
    val selectedImageUri: Uri? = null,
    val isLoadingLocation: Boolean = false,
    val locationError: String? = null
)
