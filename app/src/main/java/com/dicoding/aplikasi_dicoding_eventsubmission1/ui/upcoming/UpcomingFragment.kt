package com.dicoding.aplikasi_dicoding_eventsubmission1.ui.upcoming

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.dicoding.aplikasi_dicoding_eventsubmission1.NetworkViewModel
import com.dicoding.aplikasi_dicoding_eventsubmission1.databinding.FragmentUpcomingBinding

class UpcomingFragment : Fragment() {

    private var _binding: FragmentUpcomingBinding? = null
    private val binding: FragmentUpcomingBinding
        get() = _binding ?: throw IllegalStateException("Binding is not initialized")


    private lateinit var networkViewModel: NetworkViewModel


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {

        networkViewModel = ViewModelProvider(requireActivity())[NetworkViewModel::class.java]
        _binding = FragmentUpcomingBinding.inflate(inflater, container, false)
        val root: View = binding.root
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}