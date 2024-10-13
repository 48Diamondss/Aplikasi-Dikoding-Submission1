package com.dicoding.aplikasi_dicoding_eventsubmission1.ui.upcoming

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.aplikasi_dicoding_eventsubmission1.MainViewModel
import com.dicoding.aplikasi_dicoding_eventsubmission1.data.adapter.Adapter
import com.dicoding.aplikasi_dicoding_eventsubmission1.databinding.FragmentUpcomingBinding
import com.dicoding.aplikasi_dicoding_eventsubmission1.ui.detail.DetailActivity

class UpcomingFragment : Fragment() {

    private var _binding: FragmentUpcomingBinding? = null
    private val binding get() = _binding!!

    private lateinit var upcomingViewModel: UpcomingViewModel
    private lateinit var mainViewModel: MainViewModel
    private var toastDisplayed = false
    private var toastMessage: String? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        upcomingViewModel = ViewModelProvider(this)[UpcomingViewModel::class.java]
        mainViewModel = ViewModelProvider(requireActivity())[MainViewModel::class.java]

        _binding = FragmentUpcomingBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // Mengamati perubahan pada koneksi internet
        mainViewModel.isConnected.observe(viewLifecycleOwner) { isConnected ->
            updateUI(isConnected)
        }

        // Setup RecyclerView
        setupRecyclerView()

        // searchview
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (query.isNullOrEmpty()) {
                    // Kembalikan hasil ke semua acara dan muat ulang data jika query kosong
                    upcomingViewModel.fetchEvents(forceReload = true)
                } else {
                    // Panggil fungsi pencarian di ViewModel
                    upcomingViewModel.searchEvents(query)
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText.isNullOrEmpty() || newText.trim().isEmpty()) {
                    // Muat ulang data jika input kosong
                    upcomingViewModel.fetchEvents(forceReload = true)
                }
                return true
            }
        })
        return root
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean("toastDisplayed", toastDisplayed)
        outState.putString("toastMessage", toastMessage)
    }

    private fun setupRecyclerView() {
        // Inisialisasi RecyclerView untuk finished events
        binding.recyclerViewVertical.layoutManager = LinearLayoutManager(requireContext())

        upcomingViewModel.upcomingEvents.observe(viewLifecycleOwner) { events ->
            if (events.isNullOrEmpty()) {
                binding.recyclerViewVertical.visibility = View.GONE
            } else {
                binding.recyclerViewVertical.adapter = Adapter(events) { event ->
                    // Handle click and start DetailActivity
                    val intent = Intent(requireContext(), DetailActivity::class.java).apply {
                        putExtra("event_data", event) // Kirim data ke DetailActivity
                    }
                    startActivity(intent)
                }
                binding.recyclerViewVertical.visibility = View.VISIBLE
            }
        }

        // Amati isLoading dari HomeViewModel
        upcomingViewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        // Amati LiveData untuk pesan error
        upcomingViewModel.errorMessage.observe(viewLifecycleOwner) { errorMessage ->
            errorMessage?.let {
                // Hanya tampilkan toast jika belum pernah ditampilkan sebelumnya
                if (!toastDisplayed) {
                    Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
                    toastDisplayed = true // Set flag menjadi true
                    toastMessage = it // Simpan pesan toast
                }
            } ?: run {
                toastDisplayed = false // Reset flag jika tidak ada error
                toastMessage = null
            }
        }
    }

    private fun updateUI(isConnected: Boolean) {
        // Cek apakah data upcoming
        val isUpcomingEventsAvailable =
            upcomingViewModel.upcomingEvents.value != null && upcomingViewModel.upcomingEvents.value!!.isNotEmpty()

        if (isConnected) {
            // Jika terhubung ke internet dan data sudah ada, sembunyikan noInternetLayout dan progressBar
            if (isUpcomingEventsAvailable) {
                binding.noInternetLayout.visibility = View.GONE
                binding.progressBar.visibility = View.GONE
            } else {
                // Ambil data jika belum ada
                upcomingViewModel.fetchEvents()
                binding.noInternetLayout.visibility = View.GONE
                binding.progressBar.visibility = View.VISIBLE
            }
            // Reset toast message ketika terhubung
            toastMessage = null
        } else {
            binding.noInternetLayout.visibility = View.VISIBLE
            binding.progressBar.visibility = View.GONE

            // Tampilkan toast hanya jika belum ditampilkan sebelumnya
            if (isUpcomingEventsAvailable && toastMessage == null) {
                toastMessage = "No internet connection"
                Toast.makeText(requireContext(), toastMessage, Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}