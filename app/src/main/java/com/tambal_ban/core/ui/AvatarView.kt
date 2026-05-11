package com.tambal_ban.core.ui

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import coil.load
import com.google.android.material.imageview.ShapeableImageView
import com.tambal_ban.R

/**
 * Custom Avatar View with integrated edit button.
 */
class AvatarView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    private val ivAvatar: ShapeableImageView
    private val btnEditAvatar: View
    private val progressAvatar: View

    init {
        LayoutInflater.from(context).inflate(R.layout.view_avatar, this, true)
        ivAvatar = findViewById(R.id.ivAvatar)
        btnEditAvatar = findViewById(R.id.btnEditAvatar)
        progressAvatar = findViewById(R.id.progressAvatar)

        context.obtainStyledAttributes(attrs, R.styleable.AvatarView).apply {
            try {
                val showEdit = getBoolean(R.styleable.AvatarView_showEditButton, false)
                btnEditAvatar.visibility = if (showEdit) View.VISIBLE else View.GONE
                
                val size = getDimensionPixelSize(R.styleable.AvatarView_avatarSize, -1)
                if (size != -1) {
                    ivAvatar.layoutParams.width = size
                    ivAvatar.layoutParams.height = size
                }
            } finally {
                recycle()
            }
        }
    }

    fun loadAvatar(url: String?) {
        if (url.isNullOrEmpty()) {
            ivAvatar.setImageResource(R.drawable.ic_person)
            progressAvatar.visibility = View.GONE
            return
        }
        progressAvatar.visibility = View.VISIBLE
        ivAvatar.load(url) {
            placeholder(R.drawable.ic_person)
            error(R.drawable.ic_person)
            listener(
                onSuccess = { _, _ -> progressAvatar.visibility = View.GONE },
                onError = { _, _ -> progressAvatar.visibility = View.GONE }
            )
        }
    }

    fun setOnEditClickListener(listener: OnClickListener) {
        btnEditAvatar.setOnClickListener(listener)
    }

    fun setLoading(loading: Boolean) {
        progressAvatar.visibility = if (loading) View.VISIBLE else View.GONE
        btnEditAvatar.isEnabled = !loading
    }
}
