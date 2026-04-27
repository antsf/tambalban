package com.tambal_ban.workshop.data
import com.tambal_ban.workshop.ui.* 
import com.tambal_ban.workshop.viewmodel.* 
import com.tambal_ban.workshop.data.* 

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class WorkshopSubmission(
    val id: String? = null,
    val name: String,
    val address: String,
    val latitude: Double,
    val longitude: Double,
    val phone: String,
    @SerialName("user_id")
    val userId: String? = null,
    val status: String = "pending"
)
