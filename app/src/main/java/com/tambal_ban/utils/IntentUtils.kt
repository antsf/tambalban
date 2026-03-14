package com.tambal_ban.utils

import android.content.Context
import android.content.Intent
import android.net.Uri

/**
 * Utility class for creating intents
 */
object IntentUtils {

    /**
     * Create intent to dial phone number
     */
    fun dialPhoneNumber(context: Context, phoneNumber: String) {
        val intent = Intent(Intent.ACTION_DIAL).apply {
            data = Uri.parse("tel:$phoneNumber")
        }
        if (intent.resolveActivity(context.packageManager) != null) {
            context.startActivity(intent)
        }
    }

    /**
     * Create intent to call phone number directly (requires CALL_PHONE permission)
     */
    fun callPhoneNumber(context: Context, phoneNumber: String) {
        val intent = Intent(Intent.ACTION_CALL).apply {
            data = Uri.parse("tel:$phoneNumber")
        }
        try {
            context.startActivity(intent)
        } catch (e: SecurityException) {
            // Fall back to dial if permission not granted
            dialPhoneNumber(context, phoneNumber)
        }
    }

    /**
     * Create intent to open navigation to a location
     */
    fun openNavigation(context: Context, latitude: Double, longitude: Double, label: String? = null) {
        // Try Google Maps first
        val gmmUri = Uri.parse("google.navigation:q=$latitude,$longitude&mode=d")

        try {
            val mapIntent = Intent(Intent.ACTION_VIEW, gmmUri).apply {
                setPackage("com.google.android.apps.maps")
            }
            if (mapIntent.resolveActivity(context.packageManager) != null) {
                context.startActivity(mapIntent)
                return
            }
        } catch (e: Exception) {
            // Google Maps not installed
        }

        // Try Waze
        try {
            val wazeUri = Uri.parse("waze://?ll=$latitude,$longitude&navigate=yes")
            val wazeIntent = Intent(Intent.ACTION_VIEW, wazeUri)
            if (wazeIntent.resolveActivity(context.packageManager) != null) {
                context.startActivity(wazeIntent)
                return
            }
        } catch (e: Exception) {
            // Waze not installed
        }

        // Fall back to web browser
        val webUri = Uri.parse("https://www.google.com/maps/dir/?api=1&destination=$latitude,$longitude")
        val webIntent = Intent(Intent.ACTION_VIEW, webUri)
        context.startActivity(webIntent)
    }

    /**
     * Open settings app
     */
    fun openSettings(context: Context) {
        val intent = Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            data = Uri.fromParts("package", context.packageName, null)
        }
        context.startActivity(intent)
    }
}

