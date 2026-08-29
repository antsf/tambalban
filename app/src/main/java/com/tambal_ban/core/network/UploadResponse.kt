package com.tambal_ban.core.network

import kotlinx.serialization.Serializable

/** Response shape for POST /api/v2/upload/{workshop,avatar} — the URL is already
 * fully-qualified and public, no client-side URL construction needed. */
@Serializable
data class UploadResponse(
    val url: String
)
