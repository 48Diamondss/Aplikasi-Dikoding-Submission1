package com.dicoding.aplikasi_dicoding_eventsubmission1.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.aplikasi_dicoding_eventsubmission1.MainViewModel
import com.dicoding.aplikasi_dicoding_eventsubmission1.data.adapter.Adapter
import com.dicoding.aplikasi_dicoding_eventsubmission1.data.adapter.CarouselAdapter
import com.dicoding.aplikasi_dicoding_eventsubmission1.databinding.FragmentHomeBinding
import com.dicoding.aplikasi_dicoding_eventsubmission1.ui.detail.DetailActivity

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding: FragmentHomeBinding
        get() = _binding ?: throw IllegalStateException("Binding is not initialized")

    private lateinit var homeViewModel: HomeViewModel
    private lateinit var mainViewModel: MainViewModel
    private var toastDisplayed = false
    private var toastMessage: String? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        // Inisialisasi ViewModel
        homeViewModel = ViewModelProvider(this)[HomeViewModel::class.java]
        mainViewModel = ViewModelProvider(requireActivity())[MainViewModel::class.java]

        // Mengamati perubahan pada koneksi internet
        mainViewModel.isConnected.observe(viewLifecycleOwner) { isConnected ->
            updateUI(isConnected)
        }

        // Setup RecyclerView
        setupRecyclerView()

        return binding.root
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean("toastDisplayed", toastDisplayed)
        outState.putString("toastMessage", toastMessage)
    }

    private fun setupRecyclerView() {
        // Inisialisasi RecyclerView untuk carousel
        binding.recyclerViewCarousel.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)

        // Inisialisasi RecyclerView untuk finished events
        binding.recyclerViewVertical.layoutManager = LinearLayoutManager(requireContext())

        homeViewModel.upcomingEvents.observe(viewLifecycleOwner) { events ->
            if (events.isNullOrEmpty()) {
                binding.recyclerViewCarousel.visibility = View.GONE
            } else {
                binding.recyclerViewCarousel.adapter = CarouselAdapter(events) { event ->
                    // Handle click and start DetailActivity
                    val intent = Intent(requireContext(), DetailActivity::class.java).apply {
                        putExtra("event_data", event) // Kirim data ke DetailActivity
                    }
                    startActivity(intent)
                }
                binding.recyclerViewCarousel.visibility = View.VISIBLE
            }
        }

        homeViewModel.finishedEvents.observe(viewLifecycleOwner) { events ->
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
        homeViewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        // Amati LiveData untuk pesan error
        homeViewModel.errorMessage.observe(viewLifecycleOwner) { errorMessage ->
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
        // Cek apakah data upcoming dan finished events sudah ada
        val isUpcomingEventsAvailable =
            homeViewModel.upcomingEvents.value != null && homeViewModel.upcomingEvents.value!!.isNotEmpty()

        val isFinishedEventsAvailable =
            homeViewModel.finishedEvents.value != null && homeViewModel.finishedEvents.value!!.isNotEmpty()

        if (isConnected) {
            // Jika terhubung ke internet dan data sudah ada, sembunyikan noInternetLayout dan progressBar
            if (isUpcomingEventsAvailable && isFinishedEventsAvailable) {
                binding.noInternetLayout.visibility = View.GONE
                binding.progressBar.visibility = View.GONE
            } else {
                // Ambil data jika belum ada
                homeViewModel.fetchEvents()
                binding.noInternetLayout.visibility = View.GONE
                binding.progressBar.visibility = View.VISIBLE
            }
            // Reset toast message ketika terhubung
            toastMessage = null
        } else {
            binding.noInternetLayout.visibility = View.VISIBLE
            binding.progressBar.visibility = View.GONE

            // Tampilkan toast hanya jika belum ditampilkan sebelumnya
            if (isUpcomingEventsAvailable && isFinishedEventsAvailable && toastMessage == null) {
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