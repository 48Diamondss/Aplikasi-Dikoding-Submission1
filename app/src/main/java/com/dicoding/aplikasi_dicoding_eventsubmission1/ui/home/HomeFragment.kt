package com.dicoding.aplikasi_dicoding_eventsubmission1.ui.home

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

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var homeViewModel: HomeViewModel
    private lateinit var mainViewModel: MainViewModel
    private var toastDisplayed = false
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

    private fun setupRecyclerView() {
        // Inisialisasi RecyclerView untuk carousel
        binding.recyclerViewCarousel.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)

        // Inisialisasi RecyclerView untuk finished events
        binding.recyclerViewVertical.layoutManager = LinearLayoutManager(requireContext())

        // Amati data upcoming events dari HomeViewModel
        homeViewModel.upcomingEvents.observe(viewLifecycleOwner) { events ->
            if (events.isNullOrEmpty()) {
                binding.recyclerViewCarousel.visibility = View.GONE
            } else {
                binding.recyclerViewCarousel.adapter = CarouselAdapter(events)
                binding.recyclerViewCarousel.visibility = View.VISIBLE
            }
            // Kamu bisa hapus checkLoadingComplete() di sini, karena akan dikelola oleh isLoading
        }

        // Amati data finished events dari HomeViewModel
        homeViewModel.finishedEvents.observe(viewLifecycleOwner) { events ->
            if (events.isNullOrEmpty()) {
                binding.recyclerViewVertical.visibility = View.GONE
            } else {
                binding.recyclerViewVertical.adapter = Adapter(events)
                binding.recyclerViewVertical.visibility = View.VISIBLE
            }
            // Kamu bisa hapus checkLoadingComplete() di sini, karena akan dikelola oleh isLoading
        }

        // Amati isLoading dari HomeViewModel
        homeViewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            if (isLoading) {
                binding.progressBar.visibility = View.VISIBLE
            } else {
                binding.progressBar.visibility = View.GONE
            }
        }

        // Amati LiveData untuk pesan error
        homeViewModel.errorMessage.observe(viewLifecycleOwner) { errorMessage ->
            errorMessage?.let {
                // Hanya tampilkan toast jika belum pernah ditampilkan sebelumnya
                if (!toastDisplayed) {
                    Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
                    toastDisplayed = true // Set flag menjadi true
                }
            } ?: run {
                toastDisplayed = false // Reset flag jika tidak ada error
            }
        }
    }


    private fun updateUI(isConnected: Boolean) {
        if (isConnected) {
            // Cek apakah data upcoming dan finished events sudah ada
            val isUpcomingEventsAvailable = homeViewModel.upcomingEvents.value != null && homeViewModel.upcomingEvents.value!!.isNotEmpty()
            val isFinishedEventsAvailable = homeViewModel.finishedEvents.value != null && homeViewModel.finishedEvents.value!!.isNotEmpty()

            // Jika data sudah ada, tidak perlu mengambil ulang
            if (isUpcomingEventsAvailable && isFinishedEventsAvailable) {
                binding.noInternetLayout.visibility = View.GONE
                binding.progressBar.visibility = View.GONE
            } else {
                // Ambil data jika belum ada
                homeViewModel.fetchEvents()
                binding.noInternetLayout.visibility = View.GONE
                binding.progressBar.visibility = View.VISIBLE
            }
        } else {
            binding.noInternetLayout.visibility = View.VISIBLE
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}