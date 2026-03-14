package com.tambal_ban.data.api

import android.util.Log
import com.tambal_ban.utils.Constants
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

object NetworkClient {
    private const val TAG = "NetworkClient"

    fun get(endpoint: String, queryParams: List<Pair<String, String>>? = null): String? {
        var connection: HttpURLConnection? = null
        try {
            val urlString = StringBuilder("${Constants.SUPABASE_URL}/$endpoint")
            queryParams?.let {
                if (it.isNotEmpty()) {
                    urlString.append("?")
                    it.forEach { (key, value) -> urlString.append("$key=$value&") }
                }
            }

            val url = URL(urlString.toString())
            connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "GET"
            applyAuthHeaders(connection)

            val responseCode = connection.responseCode
            if (responseCode == HttpURLConnection.HTTP_OK) {
                return readStream(connection)
            } else {
                Log.e(TAG, "GET Error: $responseCode - ${connection.responseMessage}")
            }
        } catch (e: Exception) {
            Log.e(TAG, "GET Exception", e)
        } finally {
            connection?.disconnect()
        }
        return null
    }

    fun post(endpoint: String, jsonBody: String): Boolean {
        var connection: HttpURLConnection? = null
        try {
            val url = URL("${Constants.SUPABASE_URL}/$endpoint")
            connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "POST"
            connection.doOutput = true
            applyAuthHeaders(connection)
            connection.setRequestProperty("Content-Type", "application/json")
            connection.setRequestProperty("Prefer", "return=minimal")

            connection.outputStream.use { os ->
                val input = jsonBody.toByteArray(charset("utf-8"))
                os.write(input, 0, input.size)
            }

            val responseCode = connection.responseCode
            return responseCode == HttpURLConnection.HTTP_CREATED ||
                    responseCode == HttpURLConnection.HTTP_OK ||
                    responseCode == HttpURLConnection.HTTP_NO_CONTENT
        } catch (e: Exception) {
            Log.e(TAG, "POST Exception", e)
        } finally {
            connection?.disconnect()
        }
        return false
    }

    private fun applyAuthHeaders(connection: HttpURLConnection) {
        connection.setRequestProperty("apikey", Constants.SUPABASE_ANON_KEY)
        connection.setRequestProperty("Authorization", "Bearer ${Constants.SUPABASE_ANON_KEY}")
    }

    private fun readStream(connection: HttpURLConnection): String {
        val reader = BufferedReader(InputStreamReader(connection.inputStream))
        val response = StringBuilder()
        var line: String?
        while (reader.readLine().also { line = it } != null) {
            response.append(line)
        }
        reader.close()
        return response.toString()
    }
}
