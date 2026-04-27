package com.tambal_ban.workshop.ui
import com.tambal_ban.workshop.ui.* 
import com.tambal_ban.workshop.viewmodel.* 
import com.tambal_ban.workshop.data.* 

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.tambal_ban.workshop.data.Review
import com.tambal_ban.databinding.ItemReviewBinding

class ReviewAdapter : ListAdapter<Review, ReviewAdapter.ReviewViewHolder>(ReviewDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReviewViewHolder {
        val binding = ItemReviewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ReviewViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ReviewViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class ReviewViewHolder(private val binding: ItemReviewBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(review: Review) {
            binding.tvRating.text = review.rating.toString()
            binding.tvComment.text = review.comment
            binding.tvDate.text = review.createdAt?.take(10) ?: ""
        }
    }

    class ReviewDiffCallback : DiffUtil.ItemCallback<Review>() {
        override fun areItemsTheSame(oldItem: Review, newItem: Review): Boolean = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: Review, newItem: Review): Boolean = oldItem == newItem
    }
}
