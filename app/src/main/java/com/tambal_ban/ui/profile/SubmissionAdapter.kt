package com.tambal_ban.ui.profile

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.tambal_ban.R
import com.tambal_ban.data.model.WorkshopSubmission
import com.tambal_ban.databinding.ItemSubmissionBinding

class SubmissionAdapter :
        ListAdapter<WorkshopSubmission, SubmissionAdapter.ViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
                ItemSubmissionBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class ViewHolder(private val binding: ItemSubmissionBinding) :
            RecyclerView.ViewHolder(binding.root) {
        fun bind(submission: WorkshopSubmission) {
            binding.tvName.text = submission.name
            binding.tvAddress.text = submission.address

            val statusRes =
                    when (submission.status?.lowercase()) {
                        "approved" -> R.string.status_approved
                        "rejected" -> R.string.status_rejected
                        else -> R.string.status_pending
                    }

            val colorRes =
                    when (submission.status?.lowercase()) {
                        "approved" -> R.color.success
                        "rejected" -> R.color.error
                        else -> R.color.secondary
                    }

            binding.tvStatus.setText(statusRes)
            binding.tvStatus.setTextColor(binding.root.context.getColor(colorRes))
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<WorkshopSubmission>() {
        override fun areItemsTheSame(
                oldItem: WorkshopSubmission,
                newItem: WorkshopSubmission
        ): Boolean = oldItem.id == newItem.id
        override fun areContentsTheSame(
                oldItem: WorkshopSubmission,
                newItem: WorkshopSubmission
        ): Boolean = oldItem == newItem
    }
}
