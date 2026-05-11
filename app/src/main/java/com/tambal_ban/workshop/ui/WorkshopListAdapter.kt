package com.tambal_ban.workshop.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import coil.transform.RoundedCornersTransformation
import com.tambal_ban.R
import com.tambal_ban.databinding.ItemWorkshopCardBinding
import com.tambal_ban.workshop.data.Workshop

class WorkshopListAdapter(
    private val onWorkshopClick: (Workshop) -> Unit
) : RecyclerView.Adapter<WorkshopListAdapter.ViewHolder>() {

    private var workshops: List<Workshop> = emptyList()

    fun submitList(newList: List<Workshop>) {
        workshops = newList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemWorkshopCardBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(workshops[position])
    }

    override fun getItemCount(): Int = workshops.size

    inner class ViewHolder(private val binding: ItemWorkshopCardBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(workshop: Workshop) {
            binding.apply {
                tvName.text = workshop.name
                tvAddress.text = workshop.address ?: "-"
                tvDistance.text = workshop.distance?.let { String.format("%.1f km", it) } ?: ""
                tvRating.text = String.format("%.1f", workshop.rating)
                tvRatingCount.text = "(${workshop.totalReviews})"

                tvStatus.text = "Buka"

                ivWorkshop.load(workshop.imageUrl) {
                    crossfade(true)
                    placeholder(R.drawable.bg_ambient_shadow)
                    error(R.drawable.bg_ambient_shadow)
                    transformations(RoundedCornersTransformation(16f))
                }

                root.setOnClickListener { onWorkshopClick(workshop) }
            }
        }
    }
}
