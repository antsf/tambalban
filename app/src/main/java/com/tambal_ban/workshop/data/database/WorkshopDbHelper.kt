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
        const val DATABASE_VERSION = 2

        const val TABLE_WORKSHOPS = "workshops"
        const val COLUMN_ID = "id"
        const val COLUMN_NAME = "name"
        const val COLUMN_LAT = "lat"
        const val COLUMN_LNG = "lon"
        const val COLUMN_PHONE = "phone"
        const val COLUMN_ADDRESS = "address"
        const val COLUMN_CITY = "city"
        const val COLUMN_PROVINCE = "province"
        const val COLUMN_OPENING_HOURS = "opening_hours"
        const val COLUMN_RATING = "rating"
        const val COLUMN_TOTAL_REVIEWS = "total_reviews"
        const val COLUMN_IMAGE_URL = "image_url"
        const val COLUMN_SOURCE = "source"
        const val COLUMN_VERIFIED = "verified"
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
                $COLUMN_CITY TEXT,
                $COLUMN_PROVINCE TEXT,
                $COLUMN_OPENING_HOURS TEXT,
                $COLUMN_RATING REAL DEFAULT 0.0,
                $COLUMN_TOTAL_REVIEWS INTEGER DEFAULT 0,
                $COLUMN_IMAGE_URL TEXT,
                $COLUMN_SOURCE TEXT,
                $COLUMN_VERIFIED INTEGER DEFAULT 0,
                $COLUMN_CREATED_AT TEXT
            )
        """

        private const val SQL_DELETE_WORKSHOPS = "DROP TABLE IF EXISTS $TABLE_WORKSHOPS"
    }

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(SQL_CREATE_WORKSHOPS)
        db.execSQL("CREATE INDEX idx_lat_lon ON $TABLE_WORKSHOPS ($COLUMN_LAT, $COLUMN_LNG)")
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL(SQL_DELETE_WORKSHOPS)
        onCreate(db)
    }

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
