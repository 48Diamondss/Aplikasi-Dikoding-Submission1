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

    private val networkViewModel: NetworkViewModel by viewModels({ requireActivity() })

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
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
        binding.recyclerViewCarousel.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.recyclerViewCarousel.adapter = carouselAdapter
    }

    private fun onEventClick(event: EventEntitiy) {
        val intent = Intent(requireContext(), DetailActivity::class.java).apply {
            putExtra("event_data", event)
        }
        startActivity(intent)
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    private fun observeNetworkConnection() {
        networkViewModel.isConnected.observe(viewLifecycleOwner) { isConnected ->
            if (isConnected) {
                binding.noInternetLayout.visibility = View.GONE
                if (networkViewModel.hasShownNoInternetToast) {
                    showToast("Internet kembali tersedia")
                }
                networkViewModel.setHasShownNoInternetToast(false)
            } else {
                binding.noInternetLayout.visibility = View.VISIBLE
                if (!networkViewModel.hasShownNoInternetToast) {
                    showToast("Tidak ada koneksi internet")
                    networkViewModel.setHasShownNoInternetToast(true)
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
                    binding.recyclerViewCarousel.visibility = if (events.isEmpty()) {
                        if (!networkViewModel.hasShownNoInternetToast) {

                            showToast("Tidak ada acara mendatang yang ditemukan")
                            networkViewModel.setHasShownNoInternetToast(true)
                        }
                        View.GONE
                    } else {
                        // Update adapter untuk carousel
                        carouselAdapter =
                            CarouselAdapter(events.take(5)) { event -> onEventClick(event) }
                        binding.recyclerViewCarousel.adapter = carouselAdapter
                        View.VISIBLE
                    }
                }

                is Result.Error -> {
                    binding.progressBar.visibility = View.GONE
                    binding.recyclerViewCarousel.visibility = View.GONE
                    if (!networkViewModel.hasShownNoInternetToast) {

                        showToast("Error: ${result.error}")
                        networkViewModel.setHasShownNoInternetToast(true)
                    }
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
                    binding.recyclerViewVertical.visibility = if (events.isEmpty()) {
                        if (!networkViewModel.hasShownNoInternetToast) {

                            showToast("Tidak ada acara yang sudah selesai ditemukan")
                            networkViewModel.setHasShownNoInternetToast(true)
                        }
                        View.GONE
                    } else {
                        // Update adapter untuk vertical RecyclerView
                        adapter =
                            Adapter(events.take(5)) { event -> onEventClick(event) }
                        binding.recyclerViewVertical.adapter = adapter
                        View.VISIBLE
                    }
                }

                is Result.Error -> {
                    binding.progressBar.visibility = View.GONE
                    binding.recyclerViewVertical.visibility = View.GONE
                    if (!networkViewModel.hasShownNoInternetToast) {

                        showToast("Error: ${result.error}")
                        networkViewModel.setHasShownNoInternetToast(true)
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}