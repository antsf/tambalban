package com.tambal_ban.utils

import org.json.JSONObject
import retrofit2.HttpException
import java.net.ConnectException
import java.net.UnknownHostException

/**
 * Utility to map Supabase Auth errors to user-friendly messages.
 */
object AuthErrorMapper {

    fun map(exception: Throwable): String {
        return when (exception) {
            is HttpException -> {
                val errorBody = exception.response()?.errorBody()?.string()
                parseSupabaseError(errorBody)
            }
            is UnknownHostException, is ConnectException -> {
                "Tidak ada koneksi internet. Silakan periksa jaringan Anda."
            }
            else -> {
                exception.message ?: "Terjadi kesalahan yang tidak diketahui"
            }
        }
    }

    private fun parseSupabaseError(errorBody: String?): String {
        if (errorBody.isNullOrEmpty()) return "Terjadi kesalahan pada server"
        
        return try {
            val json = JSONObject(errorBody)
            val message = json.optString("msg") ?: json.optString("error_description")
            
            when {
                message.contains("User already registered", ignoreCase = true) -> "Email sudah terdaftar"
                message.contains("Invalid login credentials", ignoreCase = true) -> "Email atau kata sandi salah"
                message.contains("Password is too short", ignoreCase = true) -> "Kata sandi terlalu pendek"
                else -> message
            }
        } catch (e: Exception) {
            "Terjadi kesalahan: $errorBody"
        }
    }
}
