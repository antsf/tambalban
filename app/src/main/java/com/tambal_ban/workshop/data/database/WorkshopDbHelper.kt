package com.tambal_ban.workshop.data.database
import com.tambal_ban.workshop.ui.* 
import com.tambal_ban.workshop.viewmodel.* 
import com.tambal_ban.workshop.data.* 

import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class WorkshopDbHelper(context: Context) :
        SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        const val DATABASE_NAME = "tambal_ban.db"
        const val DATABASE_VERSION = 1

        const val TABLE_WORKSHOPS = "workshops"
        const val COLUMN_ID = "id"
        const val COLUMN_NAME = "name"
        const val COLUMN_LAT = "latitude"
        const val COLUMN_LNG = "longitude"
        const val COLUMN_PHONE = "phone"
        const val COLUMN_ADDRESS = "address"
        const val COLUMN_OPEN_TIME = "open_time"
        const val COLUMN_CLOSE_TIME = "close_time"
        const val COLUMN_IS_24H = "is_24h"
        const val COLUMN_RATING_AVG = "rating_avg"
        const val COLUMN_RATING_COUNT = "rating_count"
        const val COLUMN_SOURCE = "source"
        const val COLUMN_CREATED_AT = "created_at"

        private const val SQL_CREATE_WORKSHOPS =
                """
            CREATE TABLE $TABLE_WORKSHOPS (
                $COLUMN_ID TEXT PRIMARY KEY,
                $COLUMN_NAME TEXT NOT NULL,
                $COLUMN_LAT REAL NOT NULL,
                $COLUMN_LNG REAL NOT NULL,
                $COLUMN_PHONE TEXT,
                $COLUMN_ADDRESS TEXT,
                $COLUMN_OPEN_TIME TEXT,
                $COLUMN_CLOSE_TIME TEXT,
                $COLUMN_IS_24H INTEGER DEFAULT 0,
                $COLUMN_RATING_AVG REAL DEFAULT 0.0,
                $COLUMN_RATING_COUNT INTEGER DEFAULT 0,
                $COLUMN_SOURCE TEXT,
                $COLUMN_CREATED_AT TEXT
            )
        """

        private const val SQL_DELETE_WORKSHOPS = "DROP TABLE IF EXISTS $TABLE_WORKSHOPS"
    }

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(SQL_CREATE_WORKSHOPS)
        // Add indexes for spatial performance
        db.execSQL("CREATE INDEX idx_lat_lng ON $TABLE_WORKSHOPS ($COLUMN_LAT, $COLUMN_LNG)")
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL(SQL_DELETE_WORKSHOPS)
        onCreate(db)
    }

    /** T018/T025: Get workshops within bounding box */
    fun getWorkshopsInBounds(
            minLat: Double,
            maxLat: Double,
            minLng: Double,
            maxLng: Double
    ): Cursor? {
        val db = readableDatabase
        val selection = "$COLUMN_LAT BETWEEN ? AND ? AND $COLUMN_LNG BETWEEN ? AND ?"
        val selectionArgs =
                arrayOf(minLat.toString(), maxLat.toString(), minLng.toString(), maxLng.toString())
        return db.query(TABLE_WORKSHOPS, null, selection, selectionArgs, null, null, null)
    }

    /** T018: Closest workshop query using SQL approximation (Pythagorean distance) */
    fun getClosestWorkshop(lat: Double, lng: Double, radiusDegrees: Double): Cursor? {
        val db = readableDatabase
        val minLat = lat - radiusDegrees
        val maxLat = lat + radiusDegrees
        val minLng = lng - radiusDegrees
        val maxLng = lng + radiusDegrees

        val selection = "$COLUMN_LAT BETWEEN ? AND ? AND $COLUMN_LNG BETWEEN ? AND ?"
        val selectionArgs =
                arrayOf(minLat.toString(), maxLat.toString(), minLng.toString(), maxLng.toString())
        val orderBy =
                "(($COLUMN_LAT - $lat) * ($COLUMN_LAT - $lat) + ($COLUMN_LNG - $lng) * ($COLUMN_LNG - $lng)) ASC"

        return db.query(TABLE_WORKSHOPS, null, selection, selectionArgs, null, null, orderBy, "1")
    }

    fun clearAllWorkshops() {
        writableDatabase.execSQL("DELETE FROM $TABLE_WORKSHOPS")
    }
}
