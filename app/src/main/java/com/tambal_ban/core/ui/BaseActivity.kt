package com.tambal_ban.core.ui

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import com.tambal_ban.TambalBanApp

/**
 * Base activity to handle Edge-to-Edge and Safe Area (Window Insets).
 */
abstract class BaseActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        applyThemeFromPrefs()
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
    }

    private fun applyThemeFromPrefs() {
        val prefs = try {
            (application as TambalBanApp).authPrefs
        } catch (_: Exception) {
            null
        }
        AppCompatDelegate.setDefaultNightMode(prefs?.getNightMode() ?: AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
    }

    /**
     * Apply window insets to the root view to respect Safe Area.
     * Call this in onCreate after setContentView.
     */
    protected fun applySafeArea(rootView: android.view.View) {
        ViewCompat.setOnApplyWindowInsetsListener(rootView) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.updatePadding(
                left = systemBars.left,
                top = systemBars.top,
                right = systemBars.right,
                bottom = systemBars.bottom
            )
            insets
        }
    }
}
