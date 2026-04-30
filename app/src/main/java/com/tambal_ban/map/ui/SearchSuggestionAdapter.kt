package com.tambal_ban.map.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.tambal_ban.databinding.ItemSearchSuggestionBinding
import com.tambal_ban.workshop.data.Workshop

class SearchSuggestionAdapter(
    private val onSuggestionClick: (Workshop) -> Unit
) : RecyclerView.Adapter<SearchSuggestionAdapter.ViewHolder>() {

    private var suggestions: List<Workshop> = emptyList()

    fun submitList(newList: List<Workshop>) {
        suggestions = newList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemSearchSuggestionBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(suggestions[position])
    }

    override fun getItemCount(): Int = suggestions.size

    inner class ViewHolder(private val binding: ItemSearchSuggestionBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(workshop: Workshop) {
            binding.tvSuggestionName.text = workshop.name
            binding.tvSuggestionAddress.text = workshop.address ?: "No address"
            binding.root.setOnClickListener { onSuggestionClick(workshop) }
        }
    }
}
