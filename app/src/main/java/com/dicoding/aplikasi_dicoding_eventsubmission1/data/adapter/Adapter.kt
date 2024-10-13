package com.dicoding.aplikasi_dicoding_eventsubmission1.data.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.dicoding.aplikasi_dicoding_eventsubmission1.data.response.ListEventsItem
import com.dicoding.aplikasi_dicoding_eventsubmission1.databinding.ItemReviewBinding

class Adapter(
    private val events: List<ListEventsItem>,
    private val onItemClick: (ListEventsItem) -> Unit
) : RecyclerView.Adapter<Adapter.VerticalViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VerticalViewHolder {
        val binding = ItemReviewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return VerticalViewHolder(binding)
    }

    override fun onBindViewHolder(holder: VerticalViewHolder, position: Int) {
        val event = events[position]
        holder.bind(event)
        holder.itemView.setOnClickListener { onItemClick(event) }  // Set click listener
    }

    override fun getItemCount(): Int = events.size

    inner class VerticalViewHolder(private val binding: ItemReviewBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(event: ListEventsItem) {
            binding.textName.text = event.name

            // Menggunakan Glide untuk memuat gambar dengan placeholder dan error image
            Glide.with(binding.imageLogo.context)
                .load(event.imageLogo)
                .placeholder(ContextCompat.getDrawable(binding.imageLogo.context,
                    com.dicoding.aplikasi_dicoding_eventsubmission1.R.drawable.baseline_image_24))
                // Placeholder saat gambar sedang dimuat
                .error(ContextCompat.getDrawable(binding.imageLogo.context,
                    com.dicoding.aplikasi_dicoding_eventsubmission1.R.drawable.
                    baseline_signal_cellular_connected_no_internet_4_bar_24))

                // Gambar error jika gagal memuat
                .into(binding.imageLogo)
        }
    }
}