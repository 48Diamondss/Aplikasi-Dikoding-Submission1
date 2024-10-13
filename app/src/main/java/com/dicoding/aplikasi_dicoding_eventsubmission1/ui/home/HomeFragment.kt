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
    private lateinit var mainViewModel: MainViewModel // Tambahkan ini

    private var toastDisplayed = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        homeViewModel = ViewModelProvider(this)[HomeViewModel::class.java]

        // Inisialisasi MainViewModel
        mainViewModel = ViewModelProvider(requireActivity())[MainViewModel::class.java] // Ambil instance MainViewModel dari activity

        // Mengamati LiveData dari MainViewModel
        mainViewModel.isConnected.observe(viewLifecycleOwner) { isConnected ->
            updateUI(isConnected)
        }

        // Inisialisasi RecyclerView untuk carousel
        binding.recyclerViewCarousel.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)

        // Inisialisasi RecyclerView untuk finished events
        binding.recyclerViewVertical.layoutManager = LinearLayoutManager(requireContext())

        return binding.root
    }

    private fun updateUI(isConnected: Boolean) {
        if (isConnected) {
            fetchData() // Ambil data jika terhubung
            binding.noInternetLayout.visibility = View.GONE // Sembunyikan layout no internet
            binding.progressBar.visibility = View.VISIBLE // Tampilkan loading saat mengambil data

            // Tampilkan TextView ketika terhubung
            binding.textView.visibility = View.VISIBLE
            binding.textView2.visibility = View.VISIBLE
        } else {

            binding.recyclerViewCarousel.visibility = View.VISIBLE
            binding.recyclerViewVertical.visibility = View.VISIBLE


            binding.progressBar.visibility = View.GONE // Sembunyikan progress bar saat tidak ada koneksi

        }
    }

    private fun fetchData() {
        binding.progressBar.visibility = View.VISIBLE // Tampilkan loading sebelum fetch data

        // Fetch data dari ViewModel
        homeViewModel.fetchEvents()

        // Amati LiveData untuk upcoming events
        homeViewModel.upcomingEvents.observe(viewLifecycleOwner) { events ->
            if (events.isNullOrEmpty()) {
                binding.recyclerViewCarousel.visibility = View.GONE
            } else {
                binding.recyclerViewCarousel.adapter = CarouselAdapter(events)
                binding.recyclerViewCarousel.visibility = View.VISIBLE
            }
            // Cek jika sudah menerima data untuk upcomingEvents
            checkLoadingComplete()
        }

        // Amati LiveData untuk finished events
        homeViewModel.finishedEvents.observe(viewLifecycleOwner) { events ->
            if (events.isNullOrEmpty()) {
                binding.recyclerViewVertical.visibility = View.GONE
            } else {
                binding.recyclerViewVertical.adapter = Adapter(events)
                binding.recyclerViewVertical.visibility = View.VISIBLE
            }
            // Cek jika sudah menerima data untuk finishedEvents
            checkLoadingComplete()
        }

        // Observe LiveData for error messages
        homeViewModel.errorMessage.observe(viewLifecycleOwner) { errorMessage ->
            errorMessage?.let {
                // Show toast only if it hasn't been displayed yet
                if (!toastDisplayed) {
                    Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
                    toastDisplayed = true // Set the flag to true
                }
            } ?: run {
                toastDisplayed = false // Reset the flag if no error
            }
        }
    }

    private fun checkLoadingComplete() {
        // Jika kedua LiveData telah menerima data, sembunyikan ProgressBar
        if (homeViewModel.upcomingEvents.value != null && homeViewModel.finishedEvents.value != null) {
            binding.progressBar.visibility = View.GONE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
