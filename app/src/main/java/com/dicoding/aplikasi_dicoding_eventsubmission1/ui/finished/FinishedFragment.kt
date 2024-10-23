package com.dicoding.aplikasi_dicoding_eventsubmission1.ui.finished

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.dicoding.aplikasi_dicoding_eventsubmission1.NetworkViewModel
import com.dicoding.aplikasi_dicoding_eventsubmission1.databinding.FragmentFinishedBinding


class FinishedFragment : Fragment() {

    private var _binding: FragmentFinishedBinding? = null
    private val binding: FragmentFinishedBinding
        get() = _binding ?: throw IllegalStateException("Binding is not initialized")

    private lateinit var finishedViewModel: FinishedViewModel
    private lateinit var networkViewModel: NetworkViewModel
    private var toastDisplayed = false
    private var toastMessage: String? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentFinishedBinding.inflate(inflater, container, false)
        val root: View = binding.root
        return root
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}