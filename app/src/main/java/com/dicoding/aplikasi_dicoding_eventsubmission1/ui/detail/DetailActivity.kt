package com.dicoding.aplikasi_dicoding_eventsubmission1.ui.detail

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.text.HtmlCompat
import com.bumptech.glide.Glide
import com.dicoding.aplikasi_dicoding_eventsubmission1.R
import com.dicoding.aplikasi_dicoding_eventsubmission1.data.entitiy.EventEntitiy
import com.dicoding.aplikasi_dicoding_eventsubmission1.databinding.ActivityDetailBinding


@Suppress("DEPRECATION")
class DetailActivity : AppCompatActivity() {

    private lateinit var event: EventEntitiy
    private lateinit var binding: ActivityDetailBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Tampilkan tombol back di kiri toolbar (menggunakan ActionBar dari tema)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_back)

        // Tangani aksi klik tombol back
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(true)

        // Ambil data dari Intent
        event = intent.getParcelableExtra("event_data") ?: run {
            Log.e("DetailActivity", "Event data is missing.")
            finish() // Menghentikan aktivitas
            return
        }

        // Tampilkan data di UI
        displayEventDetails(event)
    }

    // tombol back di toolbar
    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }

    @SuppressLint("SetTextI18n")
    private fun displayEventDetails(event: EventEntitiy) {
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
            Glide.with(this@DetailActivity)
                .load(event.imageLogo ?: event.mediaCover)
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