package com.tambal_ban.workshop.data.database.mappers
import com.tambal_ban.workshop.ui.*
import com.tambal_ban.workshop.viewmodel.*
import com.tambal_ban.workshop.data.*

import android.content.ContentValues
import android.database.Cursor
import com.tambal_ban.workshop.data.database.WorkshopDbHelper
import com.tambal_ban.workshop.data.Workshop

object WorkshopMapper {

    fun fromCursor(cursor: Cursor): Workshop {
        val id = cursor.getString(cursor.getColumnIndexOrThrow(WorkshopDbHelper.COLUMN_ID))
        val name = cursor.getString(cursor.getColumnIndexOrThrow(WorkshopDbHelper.COLUMN_NAME))
        val lat = cursor.getDouble(cursor.getColumnIndexOrThrow(WorkshopDbHelper.COLUMN_LAT))
        val lon = cursor.getDouble(cursor.getColumnIndexOrThrow(WorkshopDbHelper.COLUMN_LNG))
        val phone = cursor.getString(cursor.getColumnIndexOrThrow(WorkshopDbHelper.COLUMN_PHONE))
        val address = cursor.getString(cursor.getColumnIndexOrThrow(WorkshopDbHelper.COLUMN_ADDRESS))
        val city = cursor.getString(cursor.getColumnIndexOrThrow(WorkshopDbHelper.COLUMN_CITY))
        val province = cursor.getString(cursor.getColumnIndexOrThrow(WorkshopDbHelper.COLUMN_PROVINCE))
        val openingHours = cursor.getString(cursor.getColumnIndexOrThrow(WorkshopDbHelper.COLUMN_OPENING_HOURS))
        val rating = cursor.getDouble(cursor.getColumnIndexOrThrow(WorkshopDbHelper.COLUMN_RATING))
        val totalReviews = cursor.getInt(cursor.getColumnIndexOrThrow(WorkshopDbHelper.COLUMN_TOTAL_REVIEWS))
        val imageUrl = cursor.getString(cursor.getColumnIndexOrThrow(WorkshopDbHelper.COLUMN_IMAGE_URL))
        val source = cursor.getString(cursor.getColumnIndexOrThrow(WorkshopDbHelper.COLUMN_SOURCE))
        val verified = cursor.getInt(cursor.getColumnIndexOrThrow(WorkshopDbHelper.COLUMN_VERIFIED)) == 1
        val createdAt = cursor.getString(cursor.getColumnIndexOrThrow(WorkshopDbHelper.COLUMN_CREATED_AT))

        return Workshop(
                id = id,
                name = name,
                lat = lat,
                lon = lon,
                phone = phone,
                address = address,
                city = city,
                province = province,
                openingHours = openingHours,
                rating = rating,
                totalReviews = totalReviews,
                imageUrl = imageUrl,
                source = source,
                verified = verified,
                createdAt = createdAt
        )
    }

    fun toContentValues(workshop: Workshop): ContentValues {
        return ContentValues().apply {
            put(WorkshopDbHelper.COLUMN_ID, workshop.id)
            put(WorkshopDbHelper.COLUMN_NAME, workshop.name)
            put(WorkshopDbHelper.COLUMN_LAT, workshop.lat)
            put(WorkshopDbHelper.COLUMN_LNG, workshop.lon)
            put(WorkshopDbHelper.COLUMN_PHONE, workshop.phone)
            put(WorkshopDbHelper.COLUMN_ADDRESS, workshop.address)
            put(WorkshopDbHelper.COLUMN_CITY, workshop.city)
            put(WorkshopDbHelper.COLUMN_PROVINCE, workshop.province)
            put(WorkshopDbHelper.COLUMN_OPENING_HOURS, workshop.openingHours)
            put(WorkshopDbHelper.COLUMN_RATING, workshop.rating)
            put(WorkshopDbHelper.COLUMN_TOTAL_REVIEWS, workshop.totalReviews)
            put(WorkshopDbHelper.COLUMN_IMAGE_URL, workshop.imageUrl)
            put(WorkshopDbHelper.COLUMN_SOURCE, workshop.source)
            put(WorkshopDbHelper.COLUMN_VERIFIED, if (workshop.verified) 1 else 0)
            put(WorkshopDbHelper.COLUMN_CREATED_AT, workshop.createdAt)
        }
    }
}
