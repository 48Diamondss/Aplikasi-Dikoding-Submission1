package com.dicoding.aplikasi_dicoding_eventsubmission1.data.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.dicoding.aplikasi_dicoding_eventsubmission1.data.response.ListEventsItem
import com.dicoding.aplikasi_dicoding_eventsubmission1.databinding.ItemReviewBinding

class Adapter(private val events: List<ListEventsItem>) :
    RecyclerView.Adapter<Adapter.VerticalViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VerticalViewHolder {
        val binding = ItemReviewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return VerticalViewHolder(binding)
    }

    override fun onBindViewHolder(holder: VerticalViewHolder, position: Int) {
        val event = events[position]
        holder.bind(event)
    }

    override fun getItemCount(): Int = events.size

    inner class VerticalViewHolder(private val binding: ItemReviewBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(event: ListEventsItem) {
            binding.textName.text = event.name

            Glide.with(binding.imageLogo.context)
                .load(event.imageLogo)

                .into(binding.imageLogo)
        }
    }
}