
package com.tambal_ban.ui.list

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.tambal_ban.R
import com.tambal_ban.data.model.Workshop
import com.tambal_ban.databinding.WorkshopItemBinding
import com.tambal_ban.utils.GeoUtils

class AllWorkshopsAdapter(
    private val onWorkshopClick: (Workshop) -> Unit
) : ListAdapter<Workshop, AllWorkshopsAdapter.WorkshopViewHolder>(WorkshopDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WorkshopViewHolder {
        val binding = WorkshopItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return WorkshopViewHolder(binding)
    }

    override fun onBindViewHolder(holder: WorkshopViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class WorkshopViewHolder(private val binding: WorkshopItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(workshop: Workshop) {
            binding.tvName.text = workshop.name
            binding.tvAddress.text = workshop.address
            binding.tvRating.text = workshop.ratingAvg?.toString() ?: "0.0"
            binding.tvDistance.text = workshop.distance?.let { GeoUtils.formatDistance(it) } ?: ""

            // T044: Integrate Glide
            val photoUrl = workshop.photoUrl ?: "" // Fallback handled by Glide
            Glide.with(binding.ivWorkshop.context)
                .load(photoUrl)
                .placeholder(R.drawable.default_workshop_image)
                .error(R.drawable.default_workshop_image)
                .into(binding.ivWorkshop)

            binding.root.setOnClickListener { onWorkshopClick(workshop) }
        }
    }

    class WorkshopDiffCallback : DiffUtil.ItemCallback<Workshop>() {
        override fun areItemsTheSame(oldItem: Workshop, newItem: Workshop): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Workshop, newItem: Workshop): Boolean {
            return oldItem == newItem
        }
    }
}
