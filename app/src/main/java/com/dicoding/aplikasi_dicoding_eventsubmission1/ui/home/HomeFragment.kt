package com.dicoding.aplikasi_dicoding_eventsubmission1.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.aplikasi_dicoding_eventsubmission1.MainActivity
import com.dicoding.aplikasi_dicoding_eventsubmission1.data.adapter.Adapter
import com.dicoding.aplikasi_dicoding_eventsubmission1.data.adapter.CarouselAdapter
import com.dicoding.aplikasi_dicoding_eventsubmission1.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var homeViewModel: HomeViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        homeViewModel = ViewModelProvider(this)[HomeViewModel::class.java]

        // Mengamati LiveData dari MainActivity
        (activity as MainActivity).isConnected.observe(viewLifecycleOwner) { isConnected ->
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
            binding.noInternetLayout.visibility = View.VISIBLE // Tampilkan layout no internet
            binding.recyclerViewCarousel.visibility = View.GONE
            binding.recyclerViewVertical.visibility = View.GONE
            binding.progressBar.visibility = View.GONE // Sembunyikan progress bar saat tidak ada koneksi

            // Sembunyikan TextView saat tidak terhubung
            binding.textView.visibility = View.GONE
            binding.textView2.visibility = View.GONE
        }
    }

    private fun fetchData() {
        binding.progressBar.visibility = View.VISIBLE // Tampilkan loading sebelum fetch data

        // Fetch data dari ViewModel
        homeViewModel.fetchEvents()

        // Amati LiveData untuk upcoming events
        homeViewModel.upcomingEvents.observe(viewLifecycleOwner) { events ->
            binding.progressBar.visibility = View.GONE // Sembunyikan loading saat data diterima
            if (events.isNullOrEmpty()) {
                binding.recyclerViewCarousel.visibility = View.GONE
            } else {
                binding.recyclerViewCarousel.adapter = CarouselAdapter(events)
                binding.recyclerViewCarousel.visibility = View.VISIBLE
            }
        }

        // Amati LiveData untuk finished events
        homeViewModel.finishedEvents.observe(viewLifecycleOwner) { events ->
            binding.progressBar.visibility = View.GONE // Sembunyikan loading saat data diterima
            if (events.isNullOrEmpty()) {
                binding.recyclerViewVertical.visibility = View.GONE
            } else {
                binding.recyclerViewVertical.adapter = Adapter(events)
                binding.recyclerViewVertical.visibility = View.VISIBLE
            }
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
