package com.tambal_ban.map.ui
import com.tambal_ban.map.ui.* 
import com.tambal_ban.map.viewmodel.* 

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

/**
 * T011: Specialized adapter for the nearby workshops list in the Home Bottom Sheet.
 */
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
                tvWorkshopName.text = workshop.name
                
                // Rating and Distance subtext
                val ratingStr = if (workshop.ratingCount > 0) String.format("%.1f", workshop.ratingAvg) else "0.0"
                val distanceStr = workshop.distance?.let { GeoUtils.formatDistance(it) } ?: "?? km"
                tvWorkshopSubtext.text = "$ratingStr • $distanceStr"

                // Status Badge Logic
                tvWorkshopStatus.text = if (workshop.is24h) "24 JAM" else "BUKA"
                
                // T010: Workshop images loaded via Coil
                ivWorkshopImage.load(workshop.imageUrl) {
                    crossfade(true)
                    placeholder(R.color.divider)
                    error(R.color.divider)
                }
            }
        }
    }

    private class WorkshopDiffCallback : DiffUtil.ItemCallback<Workshop>() {
        override fun areItemsTheSame(oldItem: Workshop, newItem: Workshop): Boolean = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: Workshop, newItem: Workshop): Boolean = oldItem == newItem
    }
}
