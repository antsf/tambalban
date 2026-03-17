package com.tambal_ban.data.repository

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.util.Log
import com.tambal_ban.data.api.SupabaseService
import com.tambal_ban.data.database.WorkshopDbHelper
import com.tambal_ban.data.database.mappers.WorkshopMapper
import com.tambal_ban.data.model.Workshop
import com.tambal_ban.utils.GeoUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/** Repository for workshop data using native components */
class WorkshopRepository(
        private val dbHelper: WorkshopDbHelper,
        private val supabaseService: SupabaseService
) {
    private val TAG = "WorkshopRepository"

    /** Get workshops within bounding box - Try API, then Cache */
    suspend fun getWorkshopsInBounds(
            minLat: Double,
            maxLat: Double,
            minLng: Double,
            maxLng: Double,
            context: Context
    ): List<Workshop> =
            withContext(Dispatchers.IO) {
                if (isNetworkAvailable(context)) {
                    try {
                        val response =
                                supabaseService.getWorkshops(
                                        minLat = "gte.$minLat",
                                        maxLat = "lte.$maxLat",
                                        minLng = "gte.$minLng",
                                        maxLng = "lte.$maxLng"
                                )
                        if (response.isSuccessful) {
                            val workshops = response.body() ?: emptyList()
                            saveWorkshopsToDb(workshops)
                            return@withContext workshops
                        }
                    } catch (e: Exception) {
                        Log.e(TAG, "Error fetching workshops", e)
                    }
                }

                val workshops = mutableListOf<Workshop>()
                val cursor = dbHelper.getWorkshopsInBounds(minLat, maxLat, minLng, maxLng)
                cursor?.use {
                    while (it.moveToNext()) {
                        workshops.add(WorkshopMapper.fromCursor(it))
                    }
                }
                return@withContext workshops
            }

    /**
     * T026 Optimization: Find nearest workshops within radius using SQL-level bounding box first
     */
    suspend fun findNearestWorkshops(
            userLat: Double,
            userLng: Double,
            radiusKm: Int
    ): List<Workshop> =
            withContext(Dispatchers.IO) {
                // Approximate 1km = 0.009 degrees
                val radiusDegrees = radiusKm * 0.01
                val minLat = userLat - radiusDegrees
                val maxLat = userLat + radiusDegrees
                val minLng = userLng - radiusDegrees
                val maxLng = userLng + radiusDegrees

                val workshops = mutableListOf<Workshop>()
                val cursor = dbHelper.getWorkshopsInBounds(minLat, maxLat, minLng, maxLng)
                cursor?.use {
                    while (it.moveToNext()) {
                        val workshop = WorkshopMapper.fromCursor(it)
                        workshop.distance =
                                GeoUtils.calculateDistance(
                                        userLat,
                                        userLng,
                                        workshop.latitude,
                                        workshop.longitude
                                )
                        if (workshop.distance!! <= radiusKm) {
                            workshops.add(workshop)
                        }
                    }
                }
                return@withContext workshops.sortedBy { it.distance }
            }

    suspend fun getClosestWorkshop(userLat: Double, userLng: Double): Workshop? =
            withContext(Dispatchers.IO) {
                val cursor = dbHelper.getClosestWorkshop(userLat, userLng, 0.05)
                cursor?.use {
                    if (it.moveToFirst()) {
                        val workshop = WorkshopMapper.fromCursor(it)
                        workshop.distance =
                                GeoUtils.calculateDistance(
                                        userLat,
                                        userLng,
                                        workshop.latitude,
                                        workshop.longitude
                                )
                        return@withContext workshop
                    }
                }
                return@withContext null
            }

    /** Get all workshops (paginated) - Try API, then local DB */
    suspend fun getAllWorkshops(
            context: Context,
            page: Int = 0,
            pageSize: Int = 20
    ): List<Workshop> =
            withContext(Dispatchers.IO) {
                if (isNetworkAvailable(context)) {
                    try {
                        val from = page * pageSize
                        val to = from + pageSize - 1
                        val response = supabaseService.getWorkshops(range = "$from-$to")
                        if (response.isSuccessful) {
                            val workshops = response.body() ?: emptyList()
                            saveWorkshopsToDb(workshops)
                            return@withContext workshops
                        }
                    } catch (e: Exception) {
                        Log.e(TAG, "Error fetching all workshops", e)
                    }
                }

                // Fallback to local DB
                val db = dbHelper.readableDatabase
                val workshops = mutableListOf<Workshop>()
                val cursor =
                        db.query(
                                WorkshopDbHelper.TABLE_WORKSHOPS,
                                null,
                                null,
                                null,
                                null,
                                null,
                                "${WorkshopDbHelper.COLUMN_NAME} ASC",
                                "${page * pageSize},$pageSize"
                        )
                cursor.use {
                    while (it.moveToNext()) {
                        workshops.add(WorkshopMapper.fromCursor(it))
                    }
                }
                return@withContext workshops
            }

    suspend fun searchWorkshops(
            context: Context,
            query: String,
            page: Int = 0,
            pageSize: Int = 20
    ): List<Workshop> =
            withContext(Dispatchers.IO) {
                if (isNetworkAvailable(context)) {
                    try {
                        val from = page * pageSize
                        val to = from + pageSize - 1
                        val response =
                                supabaseService.searchWorkshops(
                                        name = "ilike.*$query*",
                                        range = "$from-$to"
                                )
                        if (response.isSuccessful) {
                            val workshops = response.body() ?: emptyList()
                            saveWorkshopsToDb(workshops)
                            return@withContext workshops
                        }
                    } catch (e: Exception) {
                        Log.e(TAG, "Error searching workshops", e)
                    }
                }

                // Fallback to local DB
                val db = dbHelper.readableDatabase
                val workshops = mutableListOf<Workshop>()
                val cursor =
                        db.query(
                                WorkshopDbHelper.TABLE_WORKSHOPS,
                                null,
                                "${WorkshopDbHelper.COLUMN_NAME} LIKE ?",
                                arrayOf("%$query%"),
                                null,
                                null,
                                "${WorkshopDbHelper.COLUMN_NAME} ASC",
                                "${page * pageSize},$pageSize"
                        )
                cursor.use {
                    while (it.moveToNext()) {
                        workshops.add(WorkshopMapper.fromCursor(it))
                    }
                }
                return@withContext workshops
            }

    suspend fun getWorkshopById(id: String): Workshop? =
            withContext(Dispatchers.IO) {
                try {
                    val response = supabaseService.getWorkshopById("eq.$id")
                    if (response.isSuccessful) {
                        return@withContext response.body()?.firstOrNull()
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "Error fetching workshop by ID", e)
                }

                val db = dbHelper.readableDatabase
                val cursor =
                        db.query(
                                WorkshopDbHelper.TABLE_WORKSHOPS,
                                null,
                                "${WorkshopDbHelper.COLUMN_ID} = ?",
                                arrayOf(id),
                                null,
                                null,
                                null
                        )
                cursor.use {
                    if (it.moveToFirst()) {
                        return@withContext WorkshopMapper.fromCursor(it)
                    }
                }
                return@withContext null
            }

    private fun saveWorkshopsToDb(workshops: List<Workshop>) {
        val db = dbHelper.writableDatabase
        db.beginTransaction()
        try {
            workshops.forEach { workshop ->
                db.insertWithOnConflict(
                        WorkshopDbHelper.TABLE_WORKSHOPS,
                        null,
                        WorkshopMapper.toContentValues(workshop),
                        android.database.sqlite.SQLiteDatabase.CONFLICT_REPLACE
                )
            }
            db.setTransactionSuccessful()
        } catch (e: Exception) {
            Log.e(TAG, "Error saving to DB", e)
        } finally {
            db.endTransaction()
        }
    }

    private fun isNetworkAvailable(context: Context): Boolean {
        val connectivityManager =
                context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
        return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }
}
