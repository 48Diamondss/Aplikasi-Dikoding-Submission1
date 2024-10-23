package com.dicoding.aplikasi_dicoding_eventsubmission1.ui.favourite

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
import com.dicoding.aplikasi_dicoding_eventsubmission1.ViewModelFactory
import com.dicoding.aplikasi_dicoding_eventsubmission1.data.Result
import com.dicoding.aplikasi_dicoding_eventsubmission1.data.adapter.Adapter
import com.dicoding.aplikasi_dicoding_eventsubmission1.data.entitiy.EventEntitiy
import com.dicoding.aplikasi_dicoding_eventsubmission1.databinding.FragmentFavouriteBinding
import com.dicoding.aplikasi_dicoding_eventsubmission1.ui.detail.DetailActivity


class FavoriteFragment : Fragment() {

    private var _binding: FragmentFavouriteBinding? = null
    private val binding
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
        _binding = FragmentFavouriteBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        observeFavoriteEvents()
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

    private fun observeFavoriteEvents() {
        viewModel.favoriteEvents.observe(viewLifecycleOwner) { result ->
            when (result) {
                is Result.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                    binding.recyclerViewVertical.visibility = View.GONE
                    binding.noInternetLayout.visibility = View.GONE
                }
                is Result.Success -> {
                    binding.progressBar.visibility = View.GONE
                    val events = result.data
                    if (events.isEmpty()) {
                        binding.recyclerViewVertical.visibility = View.GONE
                        binding.noInternetLayout.visibility = View.VISIBLE
                    } else {
                        binding.noInternetLayout.visibility = View.GONE
                        adapter = Adapter(events) { event -> onEventClick(event) }
                        binding.recyclerViewVertical.adapter = adapter
                        binding.recyclerViewVertical.visibility = View.VISIBLE
                    }
                }
                is Result.Error -> {
                    binding.progressBar.visibility = View.GONE
                    binding.recyclerViewVertical.visibility = View.GONE
                    binding.noInternetLayout.visibility = View.VISIBLE
                    Toast.makeText(requireContext(), "Error: ${result.error}", Toast.LENGTH_SHORT).show()
                }
            }
        }
        // Trigger fetching of favorite events
        viewModel.fetchFavoriteEvents()
    }

    override fun onResume() {
        super.onResume()
        viewModel.fetchFavoriteEvents() // Refresh the data when fragment resumes
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

