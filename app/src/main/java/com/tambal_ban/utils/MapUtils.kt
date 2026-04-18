package com.tambal_ban.utils

import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter

/**
 * T008: Map utility for osmdroid styling and geometry operations.
 */
object MapUtils {
    /**
     * Creates a ColorMatrixColorFilter for "Standard Day Mode".
     * Preserves original colors but reduces intensity (saturation) for better readability.
     */
    fun getColorFilter(): ColorMatrixColorFilter {
        val colorMatrix = ColorMatrix()

        // 1. Soft saturation (60%) to keep it natural but "less intense" (Option C)
        colorMatrix.setSaturation(0.6f)

        // 2. Precise brightness offset (low) to maintain highlight detail
        val brightnessMatrix = floatArrayOf(
            1f, 0f, 0f, 0f, 10f,
            0f, 1f, 0f, 0f, 10f,
            0f, 0f, 1f, 0f, 10f,
            0f, 0f, 0f, 1f, 0f
        )

        colorMatrix.postConcat(ColorMatrix(brightnessMatrix))

        return ColorMatrixColorFilter(colorMatrix)
    }
}
