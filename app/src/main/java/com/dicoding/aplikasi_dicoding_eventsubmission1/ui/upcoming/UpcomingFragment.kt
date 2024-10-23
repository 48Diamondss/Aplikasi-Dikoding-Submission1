package com.dicoding.aplikasi_dicoding_eventsubmission1.ui.upcoming

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.aplikasi_dicoding_eventsubmission1.EventViewModel
import com.dicoding.aplikasi_dicoding_eventsubmission1.NetworkViewModel
import com.dicoding.aplikasi_dicoding_eventsubmission1.ViewModelFactory
import com.dicoding.aplikasi_dicoding_eventsubmission1.data.Result
import com.dicoding.aplikasi_dicoding_eventsubmission1.data.adapter.Adapter
import com.dicoding.aplikasi_dicoding_eventsubmission1.data.entitiy.EventEntitiy
import com.dicoding.aplikasi_dicoding_eventsubmission1.databinding.FragmentUpcomingBinding
import com.dicoding.aplikasi_dicoding_eventsubmission1.ui.detail.DetailActivity

class UpcomingFragment : Fragment() {

    private var _binding: FragmentUpcomingBinding? = null
    private val binding: FragmentUpcomingBinding
        get() = _binding ?: throw IllegalStateException("Binding is not initialized")

    private val viewModel: EventViewModel by viewModels {
        ViewModelFactory.getInstance(requireActivity())
    }
    private lateinit var adapter: Adapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentUpcomingBinding.inflate(inflater, container, false)
        val root: View = binding.root
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Setup RecyclerView
        setupRecyclerView()

        // Observe network connection
        observeNetworkConnection()

        // Observe data dari ViewModel
        observeViewModel()

        // Setup SearchView
        setupSearchView()
    }

    private fun setupRecyclerView() {
        // Setup vertical RecyclerView
        adapter = Adapter(emptyList()) { event -> onEventClick(event) }
        binding.recyclerViewVertical.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerViewVertical.adapter = adapter
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
                    Toast.makeText(
                        requireContext(),
                        "Internet kembali tersedia",
                        Toast.LENGTH_SHORT
                    ).show()
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

    private fun setupSearchView() {
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (query.isNullOrEmpty()) {
                    // Kembalikan hasil ke semua acara dan muat ulang data jika query kosong
                    observeUpcomingEvents()
                } else {
                    // Panggil fungsi pencarian di ViewModel
                    searchEvents(query)
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText.isNullOrEmpty()) {
                    // Muat ulang data jika input kosong
                    observeUpcomingEvents()
                } else {
                    // Panggil fungsi pencarian di ViewModel
                    searchEvents(newText)
                }
                return true
            }
        })
    }

    private fun searchEvents(query: String) {
        viewModel.hasShownNoEventsToast = false // Reset status saat memulai pencarian
        val active = 1
        viewModel.searchEvent(query, active).observe(viewLifecycleOwner) { result ->
            handleSearchResult(result)
        }
    }

    private fun observeUpcomingEvents() {
        viewModel.getUpcomingEvents().observe(viewLifecycleOwner) { result ->
            viewModel.hasShownNoEventsToast = false // Reset status saat memuat acara
            handleSearchResult(result)
        }
    }

    private fun handleSearchResult(result: Result<List<EventEntitiy>>) {
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

                    if (!viewModel.hasShownNoEventsToast) {
                        Toast.makeText(
                            requireContext(),
                            "Tidak ada acara yang ditemukan",
                            Toast.LENGTH_SHORT
                        ).show()
                        viewModel.hasShownNoEventsToast = true
                    }
                } else {
                    viewModel.hasShownNoEventsToast = false // Reset jika data ada

                    // Update adapter dengan data baru
                    adapter = Adapter(events) { event -> onEventClick(event) }
                    binding.recyclerViewVertical.adapter = adapter
                    binding.recyclerViewVertical.visibility = View.VISIBLE
                }

                // Reset error toast jika sukses
                viewModel.hasShownErrorToast = false
            }

            is Result.Error -> {
                binding.progressBar.visibility = View.GONE
                binding.recyclerViewVertical.visibility = View.GONE

                // Cek apakah toast error sudah ditampilkan
                if (!viewModel.hasShownErrorToast) {
                    Toast.makeText(requireContext(), "Error: ${result.error}", Toast.LENGTH_SHORT)
                        .show()
                    viewModel.hasShownErrorToast = true
                }
            }
        }
    }

    private fun observeViewModel() {
        // Observe upcomong events for the vertical RecyclerView
        viewModel.getUpcomingEvents().observe(viewLifecycleOwner) { result ->
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
                        Toast.makeText(
                            requireContext(),
                            "Tidak ada acara yang sudah selesai",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        // Update adapter untuk vertical RecyclerView
                        adapter = Adapter(events) { event -> onEventClick(event) }
                        binding.recyclerViewVertical.adapter = adapter
                        binding.recyclerViewVertical.visibility = View.VISIBLE
                    }
                }

                is Result.Error -> {
                    binding.progressBar.visibility = View.GONE
                    binding.recyclerViewVertical.visibility = View.GONE
                    Toast.makeText(requireContext(), "Error: ${result.error}", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}