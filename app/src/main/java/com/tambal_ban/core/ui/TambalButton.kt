package com.tambal_ban.core.ui

import android.content.Context
import android.content.res.ColorStateList
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import com.google.android.material.button.MaterialButton
import com.google.android.material.progressindicator.CircularProgressIndicator
import com.tambal_ban.R

class TambalButton @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    private val button: MaterialButton
    private val progressBar: CircularProgressIndicator

    private var originalText: String? = null
    private var originalIcon: Int = -1

    init {
        LayoutInflater.from(context).inflate(R.layout.view_tambal_button, this, true)
        button = findViewById(R.id.button)
        progressBar = findViewById(R.id.progressBar)

        context.obtainStyledAttributes(attrs, R.styleable.TambalButton).apply {
            try {
                // Text
                originalText =
                    getString(R.styleable.TambalButton_btnText) ?: getString(R.styleable.TambalButton_android_text)
                button.text = originalText

                // Icon
                originalIcon = getResourceId(R.styleable.TambalButton_btnIcon, -1)
                if (originalIcon != -1) {
                    button.setIconResource(originalIcon)
                }

                // Variant
                val variant = getInt(R.styleable.TambalButton_btnVariant, 0)
                setupVariant(variant)

                // Loading
                val isLoading = getBoolean(R.styleable.TambalButton_btnLoading, false)
                setLoading(isLoading)

                // Enabled
                val isEnabled = getBoolean(R.styleable.TambalButton_android_enabled, true)
                isEnabled(isEnabled)

                // Custom Colors (Override variants)
                val bgColor = getColor(R.styleable.TambalButton_btnBackgroundColor, -1)
                if (bgColor != -1) {
                    button.backgroundTintList = ColorStateList.valueOf(bgColor)
                }

                val contentColor = getColor(R.styleable.TambalButton_btnContentColor, -1)
                if (contentColor != -1) {
                    button.setTextColor(contentColor)
                    button.iconTint = ColorStateList.valueOf(contentColor)
                    progressBar.setIndicatorColor(contentColor)
                    // For outlined variant, also set stroke color
                    button.setStrokeColor(ColorStateList.valueOf(contentColor))
                }

            } finally {
                recycle()
            }
        }
    }

    private fun setupVariant(variant: Int) {
        when (variant) {
            1 -> { // Secondary
                button.setBackgroundTintList(ColorStateList.valueOf(context.getColor(R.color.secondary)))
                button.setTextColor(context.getColor(R.color.white))
                progressBar.setIndicatorColor(context.getColor(R.color.white))
            }

            2 -> { // Outlined
                button.backgroundTintList = ColorStateList.valueOf(android.graphics.Color.TRANSPARENT)
                button.setStrokeColorResource(R.color.primary)
                button.strokeWidth = (2 * context.resources.displayMetrics.density).toInt()
                button.setTextColor(context.getColor(R.color.primary))
                progressBar.setIndicatorColor(context.getColor(R.color.primary))
            }

            else -> { // Primary
                button.backgroundTintList = ColorStateList.valueOf(context.getColor(R.color.primary))
                button.setTextColor(context.getColor(R.color.white))
                progressBar.setIndicatorColor(context.getColor(R.color.white))
            }
        }
    }

    fun setLoading(loading: Boolean) {
        if (loading) {
            button.text = ""
            button.icon = null
            button.isEnabled = false
            progressBar.visibility = View.VISIBLE
        } else {
            button.text = originalText
            if (originalIcon != -1) {
                button.setIconResource(originalIcon)
            }
            button.isEnabled = true
            progressBar.visibility = View.GONE
        }
    }

    fun isEnabled(enabled: Boolean) {
        button.isEnabled = enabled
        this.alpha = if (enabled) 1.0f else 0.5f
    }

    override fun setOnClickListener(l: OnClickListener?) {
        button.setOnClickListener(l)
    }

    fun setText(text: String) {
        originalText = text
        button.text = text
    }
}
