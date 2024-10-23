package com.dicoding.aplikasi_dicoding_eventsubmission1.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.aplikasi_dicoding_eventsubmission1.EventViewModel
import com.dicoding.aplikasi_dicoding_eventsubmission1.NetworkViewModel
import com.dicoding.aplikasi_dicoding_eventsubmission1.ViewModelFactory
import com.dicoding.aplikasi_dicoding_eventsubmission1.data.Result
import com.dicoding.aplikasi_dicoding_eventsubmission1.data.adapter.Adapter
import com.dicoding.aplikasi_dicoding_eventsubmission1.data.adapter.CarouselAdapter
import com.dicoding.aplikasi_dicoding_eventsubmission1.data.entitiy.EventEntitiy
import com.dicoding.aplikasi_dicoding_eventsubmission1.databinding.FragmentHomeBinding
import com.dicoding.aplikasi_dicoding_eventsubmission1.ui.detail.DetailActivity

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding: FragmentHomeBinding
        get() = _binding ?: throw IllegalStateException("Binding is not initialized")

    private val viewModel: EventViewModel by viewModels {
        ViewModelFactory.getInstance(requireActivity())
    }
    private lateinit var adapter: Adapter
    private lateinit var carouselAdapter: CarouselAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Setup RecyclerView
        setupRecyclerView()

        // Observe network connection
        observeNetworkConnection()

        // Observe data dari ViewModel
        observeViewModel()
    }

    private fun setupRecyclerView() {
        // Setup vertical RecyclerView
        adapter = Adapter(emptyList()) { event -> onEventClick(event) }
        binding.recyclerViewVertical.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerViewVertical.adapter = adapter

        // Setup carousel RecyclerView
        carouselAdapter = CarouselAdapter(emptyList()) { event -> onEventClick(event) }
        binding.recyclerViewCarousel.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.recyclerViewCarousel.adapter = carouselAdapter
    }

    private fun onEventClick(event: EventEntitiy) {
        val intent = Intent(requireContext(), DetailActivity::class.java).apply {
            putExtra("event_data", event)
        }
        startActivity(intent)
    }

    private fun observeNetworkConnection() {
        val networkViewModel: NetworkViewModel by viewModels({ requireActivity() })

        networkViewModel.isConnected.observe(viewLifecycleOwner) { isConnected ->
            if (isConnected) {
                binding.noInternetLayout.visibility = View.GONE
                if (networkViewModel.hasShownNoInternetToast) {
                    Toast.makeText(requireContext(), "Internet kembali tersedia", Toast.LENGTH_SHORT).show()
                }
                networkViewModel.setHasShownNoInternetToast(false) // Reset flag saat internet tersambung
            } else {
                binding.noInternetLayout.visibility = View.VISIBLE
                if (!networkViewModel.hasShownNoInternetToast) {
                    Toast.makeText(requireContext(), "Internet terputus", Toast.LENGTH_SHORT).show()
                    networkViewModel.setHasShownNoInternetToast(true) // Set flag agar tidak tampil berulang kali
                }
            }
        }
    }

    private fun observeViewModel() {
        // Observe upcoming events for the carousel
        viewModel.getUpcomingEvents().observe(viewLifecycleOwner) { result ->
            when (result) {
                is Result.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                    binding.recyclerViewCarousel.visibility = View.GONE
                }
                is Result.Success -> {
                    binding.progressBar.visibility = View.GONE
                    val events = result.data
                    if (events.isEmpty()) {
                        binding.recyclerViewCarousel.visibility = View.GONE
                        Toast.makeText(requireContext(), "Tidak ada acara mendatang yang ditemukan", Toast.LENGTH_SHORT).show()
                    } else {
                        // Update adapter untuk carousel
                        carouselAdapter = CarouselAdapter(events.take(5)) { event -> onEventClick(event) } // Ambil beberapa acara untuk carousel
                        binding.recyclerViewCarousel.adapter = carouselAdapter
                        binding.recyclerViewCarousel.visibility = View.VISIBLE
                    }
                }
                is Result.Error -> {
                    binding.progressBar.visibility = View.GONE
                    binding.recyclerViewCarousel.visibility = View.GONE
                    Toast.makeText(requireContext(), "Error: ${result.error}", Toast.LENGTH_SHORT).show()
                }
            }
        }

        // Observe finished events for the vertical RecyclerView
        viewModel.getFinishedEvents().observe(viewLifecycleOwner) { result ->
            when (result) {
                is Result.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                    binding.recyclerViewVertical.visibility = View.GONE
                }
                is Result.Success -> {
                    binding.progressBar.visibility = View.GONE
                    val events = result.data
                    if (events.isEmpty()) {
                        binding.recyclerViewVertical.visibility = View.GONE
                        Toast.makeText(requireContext(), "Tidak ada acara yang sudah selesai", Toast.LENGTH_SHORT).show()
                    } else {
                        // Update adapter untuk vertical RecyclerView
                        adapter = Adapter(events.take(5)) { event -> onEventClick(event) } // Ambil hanya 5 acara selesai
                        binding.recyclerViewVertical.adapter = adapter
                        binding.recyclerViewVertical.visibility = View.VISIBLE
                    }
                }
                is Result.Error -> {
                    binding.progressBar.visibility = View.GONE
                    binding.recyclerViewVertical.visibility = View.GONE
                    Toast.makeText(requireContext(), "Error: ${result.error}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}