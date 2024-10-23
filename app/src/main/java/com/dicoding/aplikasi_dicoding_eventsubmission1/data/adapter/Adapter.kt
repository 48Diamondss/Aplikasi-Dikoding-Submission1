package com.dicoding.aplikasi_dicoding_eventsubmission1.data.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.dicoding.aplikasi_dicoding_eventsubmission1.R.drawable
import com.dicoding.aplikasi_dicoding_eventsubmission1.data.entitiy.EventEntitiy
import com.dicoding.aplikasi_dicoding_eventsubmission1.databinding.ItemReviewBinding

class Adapter(
    private val events: List<EventEntitiy>,
    private val onItemClick: (EventEntitiy) -> Unit
) : RecyclerView.Adapter<Adapter.VerticalViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VerticalViewHolder {
        val binding = ItemReviewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return VerticalViewHolder(binding)
    }

    override fun onBindViewHolder(holder: VerticalViewHolder, position: Int) {
        val event = events[position]
        holder.bind(event)
        holder.itemView.setOnClickListener {
            onItemClick(event)
        }
    }

    override fun getItemCount(): Int = events.size

    inner class VerticalViewHolder(private val binding: ItemReviewBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(event: EventEntitiy) {
            binding.textName.text = event.name

            // Menggunakan Glide untuk memuat gambar dengan placeholder dan error image
            Glide.with(binding.imageLogo.context)
                .load(event.imageLogo)
                .placeholder(
                    ContextCompat.getDrawable(
                        binding.imageLogo.context,
                        drawable.baseline_image_24
                    )
                )
                // Placeholder saat gambar sedang dimuat
                .error(
                    ContextCompat.getDrawable(
                        binding.imageLogo.context,
                        drawable.baseline_signal_cellular_connected_no_internet_4_bar_24
                    )
                )

                // Gambar error jika gagal memuat
                .into(binding.imageLogo)
        }
    }
}