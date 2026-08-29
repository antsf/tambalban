package com.tambal_ban.core.ui

import android.content.Context
import android.text.InputType
import android.text.TextWatcher
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
                editText.hint = hint

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

                // imeOptions (e.g. actionSearch)
                val imeOptions = getInt(R.styleable.TambalTextField_android_imeOptions, -1)
                if (imeOptions != -1) {
                    editText.imeOptions = imeOptions
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
                textInputLayout.setEndIconDrawable(R.drawable.selector_password_toggle)
            }
            3 -> { // Phone
                editText.inputType = InputType.TYPE_CLASS_PHONE
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

    fun addTextChangedListener(watcher: TextWatcher) {
        editText.addTextChangedListener(watcher)
    }

    fun setOnEditorActionListener(listener: android.widget.TextView.OnEditorActionListener) {
        editText.setOnEditorActionListener(listener)
    }

    val text: String get() = editText.text.toString()

    override fun setEnabled(enabled: Boolean) {
        super.setEnabled(enabled)
        editText.isEnabled = enabled
        textInputLayout.isEnabled = enabled
    }
}
