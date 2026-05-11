package com.tambal_ban.workshop.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import coil.transform.RoundedCornersTransformation
import com.google.android.gms.ads.nativead.NativeAd
import com.tambal_ban.R
import com.tambal_ban.core.ads.AdMobManager
import com.tambal_ban.databinding.AdNativeBinding
import com.tambal_ban.databinding.ItemWorkshopCardBinding
import com.tambal_ban.workshop.data.Workshop

class WorkshopListAdapter(
    private val onWorkshopClick: (Workshop) -> Unit,
    private val adMobManager: AdMobManager
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val VIEW_TYPE_WORKSHOP = 0
        private const val VIEW_TYPE_NATIVE_AD = 1
        private const val AD_INTERVAL = 5
    }

    private var workshops: List<Workshop> = emptyList()
    val nativeAds: MutableList<NativeAd?> = mutableListOf()

    fun submitList(newList: List<Workshop>) {
        workshops = newList
        val slotCount = workshops.size / AD_INTERVAL
        // Resize nativeAds list to match slot count, preserving loaded ads
        while (nativeAds.size < slotCount) nativeAds.add(null)
        while (nativeAds.size > slotCount) nativeAds.removeAt(nativeAds.lastIndex)
        notifyDataSetChanged()
    }

    fun updateNativeAd(slotIndex: Int, ad: NativeAd?) {
        if (slotIndex < nativeAds.size) {
            nativeAds[slotIndex] = ad
            // Ad is inserted every AD_INTERVAL items; slot 0 is at position AD_INTERVAL - 1, etc.
            val adapterPosition = (slotIndex + 1) * AD_INTERVAL + slotIndex
            notifyItemChanged(adapterPosition)
        }
    }

    fun destroyNativeAds() {
        nativeAds.forEach { it?.destroy() }
        nativeAds.clear()
    }

    override fun getItemViewType(position: Int): Int {
        // A native ad slot appears at positions where (position % (AD_INTERVAL + 1)) == AD_INTERVAL
        // i.e., every 6th item starting at index 4: positions 4, 10, 16, ...
        return if ((position + 1) % (AD_INTERVAL + 1) == 0) VIEW_TYPE_NATIVE_AD else VIEW_TYPE_WORKSHOP
    }

    override fun getItemCount(): Int {
        val adSlotCount = workshops.size / AD_INTERVAL
        return workshops.size + adSlotCount
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_TYPE_NATIVE_AD -> {
                val binding = AdNativeBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                NativeAdViewHolder(binding)
            }
            else -> {
                val binding = ItemWorkshopCardBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                WorkshopViewHolder(binding)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is WorkshopViewHolder -> {
                // Map adapter position to workshop index, skipping ad slots
                val workshopIndex = position - position / (AD_INTERVAL + 1)
                if (workshopIndex < workshops.size) {
                    holder.bind(workshops[workshopIndex])
                }
            }
            is NativeAdViewHolder -> {
                // Slot index: which ad position this is (0-based)
                val slotIndex = position / (AD_INTERVAL + 1)
                holder.bind(nativeAds.getOrNull(slotIndex))
            }
        }
    }

    inner class WorkshopViewHolder(private val binding: ItemWorkshopCardBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(workshop: Workshop) {
            binding.apply {
                tvName.text = workshop.name
                tvAddress.text = workshop.address ?: "Alamat tidak tersedia"
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

    inner class NativeAdViewHolder(private val binding: AdNativeBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(ad: NativeAd?) {
            if (ad == null) {
                binding.root.visibility = View.GONE
                return
            }
            binding.root.visibility = View.VISIBLE
            adMobManager.populateNativeAdView(ad, binding.root)
        }
    }
}
