package com.dicoding.aplikasi_dicoding_eventsubmission1.ui.detail

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
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
        event = intent.getParcelableExtra("event_data") ?: run {
            Log.e("DetailActivity", "Event data is missing.")
            finish() // Menghentikan aktivitas
            return
        }

        // Tampilkan data di UI
        displayEventDetails(event)
    }

    @SuppressLint("SetTextI18n")
    private fun displayEventDetails(event: ListEventsItem) {
        binding.apply {
            // Tampilkan data ke dalam View
            textEventName.text = event.name ?: "Nama Tidak Tersedia"
            textOwnerName.text = event.ownerName ?: "Penyelenggara Tidak Tersedia"
            textBeginTime.text = event.beginTime ?: "Waktu Tidak Tersedia"
            textQuota.text = "${event.quota?.minus(event.registrants ?: 0) ?: 0} Kuota Tersisa"

            textDescription.text = HtmlCompat.fromHtml(
                event.description ?: "Deskripsi Tidak Tersedia",
                HtmlCompat.FROM_HTML_MODE_LEGACY
            )

            // Memuat gambar menggunakan Glide
            Glide.with(this@DetailActivity) // Ganti YourActivity dengan nama kelas aktivitasmu
                .load(event.imageLogo ?: event.mediaCover) // Menggunakan imageLogo atau mediaCover
                .into(imageEvent)

            // Set up listener untuk tombol membuka link
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
}
