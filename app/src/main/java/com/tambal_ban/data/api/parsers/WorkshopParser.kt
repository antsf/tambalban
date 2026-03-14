package com.tambal_ban.data.api.parsers

import com.tambal_ban.data.model.Workshop
import org.json.JSONArray
import org.json.JSONObject

object WorkshopParser {

    fun parseWorkshop(json: JSONObject): Workshop {
        return Workshop(
                id = json.optString("id"),
                name = json.optString("name"),
                latitude = json.optDouble("latitude"),
                longitude = json.optDouble("longitude"),
                phone = json.optString("phone", null),
                address = json.optString("address", null),
                openTime = json.optString("open_time", null),
                closeTime = json.optString("close_time", null),
                is24h = json.optBoolean("is_24h", false),
                ratingAvg = json.optDouble("rating_avg", 0.0),
                ratingCount = json.optInt("rating_count", 0),
                source = json.optString("source", null),
                createdAt = json.optString("created_at", null)
        )
    }

    fun parseWorkshops(jsonString: String): List<Workshop> {
        val workshops = mutableListOf<Workshop>()
        try {
            val jsonArray = JSONArray(jsonString)
            for (i in 0 until jsonArray.length()) {
                val workshopJson = jsonArray.getJSONObject(i)
                workshops.add(parseWorkshop(workshopJson))
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return workshops
    }

    fun toJson(workshop: Workshop): String {
        val json =
                JSONObject().apply {
                    put("id", workshop.id)
                    put("name", workshop.name)
                    put("latitude", workshop.latitude)
                    put("longitude", workshop.longitude)
                    put("phone", workshop.phone)
                    put("address", workshop.address)
                    put("open_time", workshop.openTime)
                    put("close_time", workshop.closeTime)
                    put("is_24h", workshop.is24h)
                    put("rating_avg", workshop.ratingAvg)
                    put("rating_count", workshop.ratingCount)
                    put("source", workshop.source)
                    put("created_at", workshop.createdAt)
                }
        return json.toString()
    }
}
