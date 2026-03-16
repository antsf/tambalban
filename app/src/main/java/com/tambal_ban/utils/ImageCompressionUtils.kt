package com.tambal_ban.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

object ImageCompressionUtils {

    private const val MAX_WIDTH = 1280
    private const val QUALITY = 70

    /**
     * Compresses and resizes an image from a Uri. Returns a File pointing to the compressed image.
     * Uses sampling to avoid OOM errors with large images.
     */
    fun compressImage(context: Context, uri: Uri, targetFileName: String): File? {
        var inputStream: InputStream? = null
        var originalBitmap: Bitmap? = null
        var resizedBitmap: Bitmap? = null

        try {
            inputStream = context.contentResolver.openInputStream(uri) ?: return null

            // First, decode bounds only to calculate sample size
            val options = BitmapFactory.Options().apply { inJustDecodeBounds = true }
            BitmapFactory.decodeStream(inputStream, null, options)
            inputStream.close()

            // Calculate sample size for efficient memory usage
            val sampleSize = calculateInSampleSize(options, MAX_WIDTH, MAX_WIDTH)

            // Reopen stream and decode with sample size
            inputStream = context.contentResolver.openInputStream(uri)
            options.apply {
                inJustDecodeBounds = false
                inSampleSize = sampleSize
            }
            originalBitmap = BitmapFactory.decodeStream(inputStream, null, options)
            inputStream?.close()

            if (originalBitmap == null) return null

            // 1. Resize if necessary (after sampling, might still need resize)
            resizedBitmap =
                    if (originalBitmap.width > MAX_WIDTH) {
                        val aspectRatio =
                                originalBitmap.height.toFloat() / originalBitmap.width.toFloat()
                        val targetHeight = (MAX_WIDTH * aspectRatio).toInt()
                        Bitmap.createScaledBitmap(originalBitmap, MAX_WIDTH, targetHeight, true)
                    } else {
                        originalBitmap
                    }

            // 2. Compress
            val outputStream = ByteArrayOutputStream()
            resizedBitmap.compress(Bitmap.CompressFormat.JPEG, QUALITY, outputStream)
            val byteArray = outputStream.toByteArray()

            // 3. Save to temporary file
            val compressedFile = File(context.cacheDir, targetFileName)
            FileOutputStream(compressedFile).use { fileOutputStream ->
                fileOutputStream.write(byteArray)
                fileOutputStream.flush()
            }

            return compressedFile
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        } finally {
            // Clean up bitmaps
            if (resizedBitmap != null && resizedBitmap != originalBitmap) {
                resizedBitmap.recycle()
            }
            originalBitmap?.recycle()
            inputStream?.close()
        }
    }

    /** Calculate sample size for efficient bitmap loading. */
    private fun calculateInSampleSize(
            options: BitmapFactory.Options,
            reqWidth: Int,
            reqHeight: Int
    ): Int {
        val (height: Int, width: Int) = options.run { outHeight to outWidth }
        var inSampleSize = 1

        if (height > reqHeight || width > reqWidth) {
            val halfHeight: Int = height / 2
            val halfWidth: Int = width / 2

            while (halfHeight / inSampleSize >= reqHeight && halfWidth / inSampleSize >= reqWidth) {
                inSampleSize *= 2
            }
        }
        return inSampleSize
    }
}
