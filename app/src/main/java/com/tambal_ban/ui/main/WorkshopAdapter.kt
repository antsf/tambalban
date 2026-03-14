package com.tambal_ban.ui.main

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.tambal_ban.R
import com.tambal_ban.data.model.Workshop
import com.tambal_ban.databinding.ItemWorkshopBinding
import com.tambal_ban.utils.GeoUtils

/** RecyclerView adapter for workshop list (optimized) */
class WorkshopAdapter(private val onItemClick: (Workshop) -> Unit) :
        ListAdapter<Workshop, WorkshopAdapter.WorkshopViewHolder>(WorkshopDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WorkshopViewHolder {
        val binding =
                ItemWorkshopBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return WorkshopViewHolder(binding)
    }

    override fun onBindViewHolder(holder: WorkshopViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class WorkshopViewHolder(private val binding: ItemWorkshopBinding) :
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
                tvWorkshopAddress.text = workshop.address ?: ""

                // Distance
                workshop.distance?.let { distance ->
                    tvDistance.text = GeoUtils.formatDistance(distance)
                }
                        ?: run { tvDistance.text = "" }

                // Rating
                if (workshop.ratingCount > 0) {
                    tvRating.text = String.format("%.1f", workshop.ratingAvg)
                    tvRatingCount.text = "(${workshop.ratingCount})"
                } else {
                    tvRating.text = root.context.getString(R.string.no_rating)
                    tvRatingCount.text = ""
                }

                // 24h indicator
                if (workshop.is24h) {
                    tvOpenTime.text = root.context.getString(R.string.open_24h)
                } else if (!workshop.openTime.isNullOrEmpty()) {
                    tvOpenTime.text = root.context.getString(R.string.open_time, workshop.openTime)
                } else {
                    tvOpenTime.text = ""
                }
            }
        }
    }

    private class WorkshopDiffCallback : DiffUtil.ItemCallback<Workshop>() {
        override fun areItemsTheSame(oldItem: Workshop, newItem: Workshop): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Workshop, newItem: Workshop): Boolean {
            return oldItem == newItem
        }
    }
}
