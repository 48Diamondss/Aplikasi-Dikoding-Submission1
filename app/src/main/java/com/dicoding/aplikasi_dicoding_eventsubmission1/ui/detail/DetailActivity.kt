package com.dicoding.aplikasi_dicoding_eventsubmission1.ui.detail

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.text.HtmlCompat
import com.bumptech.glide.Glide
import com.dicoding.aplikasi_dicoding_eventsubmission1.EventViewModel
import com.dicoding.aplikasi_dicoding_eventsubmission1.R
import com.dicoding.aplikasi_dicoding_eventsubmission1.ViewModelFactory
import com.dicoding.aplikasi_dicoding_eventsubmission1.data.entitiy.EventEntitiy
import com.dicoding.aplikasi_dicoding_eventsubmission1.databinding.ActivityDetailBinding


@Suppress("DEPRECATION")
class DetailActivity : AppCompatActivity() {

    private lateinit var event: EventEntitiy
    private lateinit var binding: ActivityDetailBinding

    private val viewModel: EventViewModel by viewModels {
        ViewModelFactory.getInstance(this) // Use the ViewModelFactory
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_back)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(true)

        event = intent.getParcelableExtra("event_data") ?: run {
            Log.e("DetailActivity", "Event data is missing.")
            finish()
            return
        }

        displayEventDetails(event)
        checkIfEventIsFavorite(event.id)

        // Set up FAB click listener to toggle favorite status
        binding.fabFavorite.setOnClickListener {
            toggleFavoriteEvent(event.id)
        }
    }

    private fun checkIfEventIsFavorite(eventId: String) {
        viewModel.isEventFavorite(eventId).observe(this) { isFavorite ->
            updateFavoriteFab(isFavorite)
        }
    }

    private fun toggleFavoriteEvent(eventId: String) {
        viewModel.isEventFavorite(eventId).observe(this) { isFavorite ->
            if (isFavorite) {
                viewModel.deleteFavoriteEvent(eventId)
                updateFavoriteFab(false)
            } else {
                viewModel.addFavoriteEvent(eventId)
                updateFavoriteFab(true)
            }

            viewModel.fetchFavoriteEvents() // Pastikan ini ada di EventViewModel
            Toast.makeText(this, "Favorite status updated", Toast.LENGTH_SHORT).show()
        }
    }


    private fun updateFavoriteFab(isFavorite: Boolean) {
        if (isFavorite) {
            binding.fabFavorite.setImageResource(R.drawable.baseline_yes_favorite_24)
        } else {
            binding.fabFavorite.setImageResource(R.drawable.baseline_favorite_24)
        }
    }

    @SuppressLint("SetTextI18n")
    private fun displayEventDetails(event: EventEntitiy) {
        binding.apply {
            textEventName.text = event.name ?: "Nama Tidak Tersedia"
            textOwnerName.text = event.ownerName ?: "Penyelenggara Tidak Tersedia"
            textBeginTime.text = event.beginTime ?: "Waktu Tidak Tersedia"
            textQuota.text = "${event.quota?.minus(event.registrants ?: 0) ?: 0} Kuota Tersisa"

            textDescription.text = HtmlCompat.fromHtml(
                event.description ?: "Deskripsi Tidak Tersedia",
                HtmlCompat.FROM_HTML_MODE_LEGACY
            )

            Glide.with(this@DetailActivity)
                .load(event.imageLogo ?: event.mediaCover)
                .into(imageEvent)

            buttonOpenLink.setOnClickListener {
                val link = event.link
                if (!link.isNullOrEmpty()) {
                    val intent = Intent(Intent.ACTION_VIEW)
                    intent.data = Uri.parse(link)
                    startActivity(intent)
                }
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}