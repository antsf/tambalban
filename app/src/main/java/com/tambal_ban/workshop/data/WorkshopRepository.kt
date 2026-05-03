package com.tambal_ban.workshop.data
import com.tambal_ban.workshop.ui.*
import com.tambal_ban.workshop.viewmodel.*
import com.tambal_ban.workshop.data.*

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.util.Log
import com.tambal_ban.core.network.SupabaseService
import com.tambal_ban.core.utils.GeoUtils
import com.tambal_ban.core.utils.SupabaseConfig
import com.tambal_ban.workshop.data.database.WorkshopDbHelper
import com.tambal_ban.workshop.data.database.mappers.WorkshopMapper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import java.util.UUID

class WorkshopRepository(
    private val dbHelper: WorkshopDbHelper,
    private val supabaseService: SupabaseService
) {
    private val TAG = "WorkshopRepository"

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
                        val response = supabaseService.getWorkshopsInBounds(
                            minLat = "gte.$minLat",
                            maxLat = "lte.$maxLat",
                            minLon = "gte.$minLng",
                            maxLon = "lte.$maxLng"
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

    suspend fun getNearbyWorkshops(
        lat: Double,
        lon: Double,
        radiusMeters: Int,
        context: Context,
        limit: Int? = null,
        offset: Int? = null
    ): List<Workshop> =
        withContext(Dispatchers.IO) {
            val radiusKm = radiusMeters / 1000.0
            val radiusDegrees = radiusKm / 111.0
            val minLat = lat - radiusDegrees
            val maxLat = lat + radiusDegrees
            val minLng = lon - radiusDegrees
            val maxLng = lon + radiusDegrees

            if (isNetworkAvailable(context)) {
                try {
                    val response = supabaseService.getWorkshopsInBounds(
                        minLat = "gte.$minLat",
                        maxLat = "lte.$maxLat",
                        minLon = "gte.$minLng",
                        maxLon = "lte.$maxLng",
                        limit = limit,
                        offset = offset
                    )
                    if (response.isSuccessful) {
                        val workshops = response.body() ?: emptyList()
                        saveWorkshopsToDb(workshops)
                        return@withContext workshops.onEach {
                            it.distance = GeoUtils.calculateDistance(lat, lon, it.lat, it.lon)
                        }.filter { it.distance!! <= radiusKm }
                         .sortedBy { it.distance }
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "Error fetching nearby workshops", e)
                }
            }
            return@withContext findNearestWorkshops(lat, lon, radiusKm.toInt())
        }

    suspend fun findNearestWorkshops(
            userLat: Double,
            userLng: Double,
            radiusKm: Int
    ): List<Workshop> =
            withContext(Dispatchers.IO) {
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
                        workshop.distance = GeoUtils.calculateDistance(
                            userLat, userLng, workshop.lat, workshop.lon
                        )
                        if (workshop.distance!! <= radiusKm) {
                            workshops.add(workshop)
                        }
                    }
                }
                return@withContext workshops.sortedBy { it.distance }
            }

    suspend fun getAllWorkshops(
        context: Context,
        limit: Int? = null,
        offset: Int? = null
    ): List<Workshop> =
        withContext(Dispatchers.IO) {
            if (isNetworkAvailable(context)) {
                try {
                    val response = supabaseService.getAllWorkshops(limit = limit, offset = offset)
                    if (response.isSuccessful) {
                        val workshops = response.body() ?: emptyList()
                        saveWorkshopsToDb(workshops)
                        return@withContext workshops
                    } else {
                        Log.e(TAG, "getAllWorkshops error: ${response.errorBody()?.string()}")
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "Error fetching all workshops", e)
                }
            }

            val workshops = mutableListOf<Workshop>()
            val db = dbHelper.readableDatabase
            val cursor = db.query(
                WorkshopDbHelper.TABLE_WORKSHOPS,
                null, null, null, null, null, null,
                if (limit != null) "$limit" else null
            )
            cursor.use {
                while (it.moveToNext()) {
                    workshops.add(WorkshopMapper.fromCursor(it))
                }
            }
            return@withContext workshops
        }

    suspend fun getClosestWorkshop(userLat: Double, userLng: Double): Workshop? =
            withContext(Dispatchers.IO) {
                val cursor = dbHelper.getClosestWorkshop(userLat, userLng, 0.05)
                cursor?.use {
                    if (it.moveToFirst()) {
                        val workshop = WorkshopMapper.fromCursor(it)
                        workshop.distance = GeoUtils.calculateDistance(
                            userLat, userLng, workshop.lat, workshop.lon
                        )
                        return@withContext workshop
                    }
                }
                return@withContext null
            }

    suspend fun searchWorkshops(
        query: String,
        context: Context,
        limit: Int? = null,
        offset: Int? = null
    ): List<Workshop> =
        withContext(Dispatchers.IO) {
            if (isNetworkAvailable(context)) {
                try {
                    val orFilter = "(name.ilike.*$query*,city.ilike.*$query*)"
                    val response = supabaseService.searchWorkshops(
                        or = orFilter,
                        limit = limit,
                        offset = offset
                    )
                    if (response.isSuccessful) {
                        return@withContext response.body() ?: emptyList()
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "Error searching workshops", e)
                }
            }

            val workshops = mutableListOf<Workshop>()
            val db = dbHelper.readableDatabase
            val cursor = db.query(
                WorkshopDbHelper.TABLE_WORKSHOPS,
                null,
                "${WorkshopDbHelper.COLUMN_NAME} LIKE ? OR ${WorkshopDbHelper.COLUMN_CITY} LIKE ?",
                arrayOf("%$query%", "%$query%"),
                null, null, null
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
                val cursor = db.query(
                    WorkshopDbHelper.TABLE_WORKSHOPS,
                    null,
                    "${WorkshopDbHelper.COLUMN_ID} = ?",
                    arrayOf(id),
                    null, null, null
                )
                cursor.use {
                    if (it.moveToFirst()) {
                        return@withContext WorkshopMapper.fromCursor(it)
                    }
                }
                return@withContext null
            }

    suspend fun addWorkshop(
        submission: WorkshopSubmission,
        imageUri: Uri? = null,
        userId: String? = null,
        context: Context
    ): Result<Workshop> = withContext(Dispatchers.IO) {
        try {
            var finalSubmission = submission
            if (imageUri != null && !userId.isNullOrBlank()) {
                val imageUrl = uploadImage(imageUri, userId, context)
                finalSubmission = submission.copy(imageUrl = imageUrl)
            }

            val response = supabaseService.addWorkshop(finalSubmission)
            if (response.isSuccessful) {
                val workshop = response.body()?.firstOrNull()
                    ?: return@withContext Result.failure(Exception("Empty response from server"))
                Result.success(workshop)
            } else {
                Result.failure(Exception("Submit failed: ${response.code()} ${response.message()}"))
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error adding workshop", e)
            Result.failure(e)
        }
    }

    private suspend fun uploadImage(imageUri: Uri, userId: String, context: Context): String {
        val uuid = UUID.randomUUID().toString()
        val path = "$userId/$uuid.jpg"
        val bytes = context.contentResolver.openInputStream(imageUri)?.readBytes()
            ?: throw Exception("Cannot read image from URI")
        val requestBody = bytes.toRequestBody("image/jpeg".toMediaType())
        val response = supabaseService.uploadFile("workshops", path, requestBody)
        if (!response.isSuccessful) {
            throw Exception("Image upload failed: ${response.code()}")
        }
        return "${SupabaseConfig.STORAGE_PUBLIC_URL}/workshops/$path"
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
