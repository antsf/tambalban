package com.tambal_ban.ui.detail

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.tambal_ban.R
import com.tambal_ban.databinding.ItemGalleryBinding

class GalleryAdapter(private val photos: List<String>) :
        RecyclerView.Adapter<GalleryAdapter.GalleryViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GalleryViewHolder {
        val binding = ItemGalleryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return GalleryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: GalleryViewHolder, position: Int) {
        holder.bind(photos[position])
    }

    override fun getItemCount(): Int = photos.size

    class GalleryViewHolder(private val binding: ItemGalleryBinding) :
            RecyclerView.ViewHolder(binding.root) {
        fun bind(url: String) {
            Glide.with(binding.ivGalleryItem.context)
                    .load(url)
                    .placeholder(R.drawable.default_workshop_image)
                    .error(R.drawable.default_workshop_image)
                    .into(binding.ivGalleryItem)
        }
    }
}
