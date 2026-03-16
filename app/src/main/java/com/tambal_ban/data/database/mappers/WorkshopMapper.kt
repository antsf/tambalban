package com.tambal_ban.data.database.mappers

import android.content.ContentValues
import android.database.Cursor
import com.tambal_ban.data.database.WorkshopDbHelper
import com.tambal_ban.data.model.Workshop

object WorkshopMapper {

        fun fromCursor(cursor: Cursor): Workshop {
                val id = cursor.getString(cursor.getColumnIndexOrThrow(WorkshopDbHelper.COLUMN_ID))
                val name =
                        cursor.getString(cursor.getColumnIndexOrThrow(WorkshopDbHelper.COLUMN_NAME))
                val lat =
                        cursor.getDouble(cursor.getColumnIndexOrThrow(WorkshopDbHelper.COLUMN_LAT))
                val lng =
                        cursor.getDouble(cursor.getColumnIndexOrThrow(WorkshopDbHelper.COLUMN_LNG))
                val phone =
                        cursor.getString(
                                cursor.getColumnIndexOrThrow(WorkshopDbHelper.COLUMN_PHONE)
                        )
                val address =
                        cursor.getString(
                                cursor.getColumnIndexOrThrow(WorkshopDbHelper.COLUMN_ADDRESS)
                        )
                val openTime =
                        cursor.getString(
                                cursor.getColumnIndexOrThrow(WorkshopDbHelper.COLUMN_OPEN_TIME)
                        )
                val closeTime =
                        cursor.getString(
                                cursor.getColumnIndexOrThrow(WorkshopDbHelper.COLUMN_CLOSE_TIME)
                        )
                val is24h =
                        cursor.getInt(
                                cursor.getColumnIndexOrThrow(WorkshopDbHelper.COLUMN_IS_24H)
                        ) == 1
                val ratingAvg =
                        cursor.getDouble(
                                cursor.getColumnIndexOrThrow(WorkshopDbHelper.COLUMN_RATING_AVG)
                        )
                val ratingCount =
                        cursor.getInt(
                                cursor.getColumnIndexOrThrow(WorkshopDbHelper.COLUMN_RATING_COUNT)
                        )
                val source =
                        cursor.getString(
                                cursor.getColumnIndexOrThrow(WorkshopDbHelper.COLUMN_SOURCE)
                        )
                val createdAt =
                        cursor.getString(
                                cursor.getColumnIndexOrThrow(WorkshopDbHelper.COLUMN_CREATED_AT)
                        )
                val photoUrl =
                        cursor.getString(
                                cursor.getColumnIndexOrThrow(WorkshopDbHelper.COLUMN_PHOTO_URL)
                        )
                val verified =
                        cursor.getInt(
                                cursor.getColumnIndexOrThrow(WorkshopDbHelper.COLUMN_VERIFIED)
                        ) == 1

                return Workshop(
                        id = id,
                        name = name,
                        latitude = lat,
                        longitude = lng,
                        phone = phone,
                        address = address,
                        openTime = openTime,
                        closeTime = closeTime,
                        is24h = is24h,
                        ratingAvg = ratingAvg,
                        ratingCount = ratingCount,
                        source = source,
                        createdAt = createdAt,
                        photoUrl = photoUrl,
                        verified = verified
                )
        }

        fun toContentValues(workshop: Workshop): ContentValues {
                return ContentValues().apply {
                        put(WorkshopDbHelper.COLUMN_ID, workshop.id)
                        put(WorkshopDbHelper.COLUMN_NAME, workshop.name)
                        put(WorkshopDbHelper.COLUMN_LAT, workshop.latitude)
                        put(WorkshopDbHelper.COLUMN_LNG, workshop.longitude)
                        put(WorkshopDbHelper.COLUMN_PHONE, workshop.phone)
                        put(WorkshopDbHelper.COLUMN_ADDRESS, workshop.address)
                        put(WorkshopDbHelper.COLUMN_OPEN_TIME, workshop.openTime)
                        put(WorkshopDbHelper.COLUMN_CLOSE_TIME, workshop.closeTime)
                        put(WorkshopDbHelper.COLUMN_IS_24H, if (workshop.is24h) 1 else 0)
                        put(WorkshopDbHelper.COLUMN_RATING_AVG, workshop.ratingAvg)
                        put(WorkshopDbHelper.COLUMN_RATING_COUNT, workshop.ratingCount)
                        put(WorkshopDbHelper.COLUMN_SOURCE, workshop.source)
                        put(WorkshopDbHelper.COLUMN_CREATED_AT, workshop.createdAt)
                        put(WorkshopDbHelper.COLUMN_PHOTO_URL, workshop.photoUrl)
                        put(WorkshopDbHelper.COLUMN_VERIFIED, if (workshop.verified) 1 else 0)
                }
        }
}
