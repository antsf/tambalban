package com.tambal_ban.ui.main

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.tambal_ban.data.model.Workshop

/**
 * T023: Adapter for real-time search suggestions in the overlay list.
 */
class SearchSuggestionAdapter(
    private val onSuggestionClick: (Workshop) -> Unit
) : ListAdapter<Workshop, SearchSuggestionAdapter.ViewHolder>(WorkshopDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(android.R.layout.simple_list_item_1, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val workshop = getItem(position)
        holder.bind(workshop)
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val textView: TextView = view.findViewById(android.R.id.text1)

        fun bind(workshop: Workshop) {
            textView.text = workshop.name
            itemView.setOnClickListener { onSuggestionClick(workshop) }
        }
    }

    private class WorkshopDiffCallback : DiffUtil.ItemCallback<Workshop>() {
        override fun areItemsTheSame(oldItem: Workshop, newItem: Workshop): Boolean =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: Workshop, newItem: Workshop): Boolean =
            oldItem == newItem
    }
}
