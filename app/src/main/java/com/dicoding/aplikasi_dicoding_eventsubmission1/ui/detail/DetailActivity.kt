package com.dicoding.aplikasi_dicoding_eventsubmission1.ui.detail

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.text.HtmlCompat
import com.bumptech.glide.Glide
import com.dicoding.aplikasi_dicoding_eventsubmission1.data.response.ListEventsItem
import com.dicoding.aplikasi_dicoding_eventsubmission1.databinding.ActivityDetailBinding

@Suppress("DEPRECATION")
class DetailActivity : AppCompatActivity() {

    private lateinit var event: ListEventsItem
    private lateinit var binding: ActivityDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Ambil data dari Intent
        event = intent.getParcelableExtra("event_data") ?: return

        // Tampilkan data di UI
        displayEventDetails(event)
    }

    @SuppressLint("SetTextI18n")
    private fun displayEventDetails(event: ListEventsItem) {
        // Tampilkan data ke dalam View
        binding.textEventName.text = event.name ?: "Nama Tidak Tersedia"
        binding.textOwnerName.text = event.ownerName ?: "Penyelenggara Tidak Tersedia"
        binding.textBeginTime.text = event.beginTime ?: "Waktu Tidak Tersedia"
        binding.textQuota.text = "${event.quota?.minus(event.registrants ?: 0) ?: 0} Kuota Tersisa"
       // binding.textDescription.text = event.description ?: "Deskripsi Tidak Tersedia"

        binding.textDescription.text  = HtmlCompat.fromHtml(
            event.description?: "Deskripsi Tidak Tersedia".toString(),
            HtmlCompat.FROM_HTML_MODE_LEGACY
        )

        // Memuat gambar menggunakan Glide
        Glide.with(this)
            .load(event.imageLogo ?: event.mediaCover) // Menggunakan imageLogo atau mediaCover
            .into(binding.imageEvent)

        // Set up listener untuk tombol membuka link
        binding.buttonOpenLink.setOnClickListener {
            val link = event.link
            if (!link.isNullOrEmpty()) {
                val intent = Intent(Intent.ACTION_VIEW)
                intent.data = Uri.parse(link)
                startActivity(intent)
            }
        }
    }
}
