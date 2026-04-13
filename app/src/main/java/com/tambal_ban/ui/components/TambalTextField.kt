package com.tambal_ban.ui.components

import android.content.Context
import android.text.InputType
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.tambal_ban.R

class TambalTextField @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    private val textInputLayout: TextInputLayout
    private val editText: TextInputEditText

    init {
        LayoutInflater.from(context).inflate(R.layout.view_tambal_text_field, this, true)
        textInputLayout = findViewById(R.id.textInputLayout)
        editText = findViewById(R.id.editText)

        context.obtainStyledAttributes(attrs, R.styleable.TambalTextField).apply {
            try {
                // Hint
                val hint = getString(R.styleable.TambalTextField_fieldHint) ?: getString(R.styleable.TambalTextField_android_hint)
                textInputLayout.hint = hint

                // Icon
                val iconRes = getResourceId(R.styleable.TambalTextField_fieldIcon, -1)
                if (iconRes != -1) {
                    textInputLayout.startIconDrawable = context.getDrawable(iconRes)
                }

                // Variant / InputType
                val variant = getInt(R.styleable.TambalTextField_fieldVariant, 0)
                setupVariant(variant)

                // Override manual inputType if provided
                val manualInputType = getInt(R.styleable.TambalTextField_android_inputType, -1)
                if (manualInputType != -1) {
                    editText.inputType = manualInputType
                }

            } finally {
                recycle()
            }
        }
    }

    private fun setupVariant(variant: Int) {
        when (variant) {
            1 -> { // Email
                editText.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
            }
            2 -> { // Password
                editText.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                textInputLayout.endIconMode = TextInputLayout.END_ICON_PASSWORD_TOGGLE
            }
            else -> { // Text
                editText.inputType = InputType.TYPE_CLASS_TEXT
            }
        }
    }

    fun setText(text: String) {
        editText.setText(text)
    }

    fun setError(error: String?) {
        textInputLayout.error = error
    }
    
    val text: String get() = editText.text.toString()
}
