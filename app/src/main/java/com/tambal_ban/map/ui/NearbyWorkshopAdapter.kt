package com.tambal_ban.map.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import coil.transform.RoundedCornersTransformation
import com.tambal_ban.workshop.data.Workshop
import com.tambal_ban.databinding.ItemWorkshopNearbyBinding
import com.tambal_ban.core.utils.GeoUtils
import com.tambal_ban.R

class NearbyWorkshopAdapter(private val onItemClick: (Workshop) -> Unit) :
    ListAdapter<Workshop, NearbyWorkshopAdapter.ViewHolder>(WorkshopDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemWorkshopNearbyBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ViewHolder(private val binding: ItemWorkshopNearbyBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                @Suppress("DEPRECATION") val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onItemClick(getItem(position))
                }
            }
        }

        fun bind(workshop: Workshop) {
            binding.apply {
                tvName.text = workshop.name
                tvAddress.text = workshop.address ?: workshop.city ?: "-"
                tvDistance.text = workshop.distance?.let { GeoUtils.formatDistance(it) } ?: "?? km"
                tvRating.text = String.format("%.1f", workshop.rating)
                tvRatingCount.text = "(${workshop.totalReviews})"
                tvStatus.text = "Buka"

                ivWorkshop.load(workshop.imageUrl) {
                    crossfade(true)
                    placeholder(R.drawable.bg_ambient_shadow)
                    error(R.drawable.bg_ambient_shadow)
                    transformations(RoundedCornersTransformation(16f))
                }
            }
        }
    }

    private class WorkshopDiffCallback : DiffUtil.ItemCallback<Workshop>() {
        override fun areItemsTheSame(oldItem: Workshop, newItem: Workshop): Boolean = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: Workshop, newItem: Workshop): Boolean = oldItem == newItem
    }
}
