package com.dicoding.aplikasi_dicoding_eventsubmission1.ui.setting

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.dicoding.aplikasi_dicoding_eventsubmission1.EventViewModel
import com.dicoding.aplikasi_dicoding_eventsubmission1.ViewModelFactory
import com.dicoding.aplikasi_dicoding_eventsubmission1.databinding.FragmentSettingBinding

class SettingFragment : Fragment() {


    private var _binding: FragmentSettingBinding? = null
    private val binding: FragmentSettingBinding
        get() = _binding ?: throw IllegalStateException("Binding is not initialized")

    private val viewModel: EventViewModel by viewModels {
        ViewModelFactory.getInstance(requireActivity())
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentSettingBinding.inflate(inflater, container, false)
        val root: View = binding.root
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupObservers()
        setupListeners()
    }


    private fun setupObservers() {
        viewModel.getThemeSettings().observe(viewLifecycleOwner) { isDarkModeActive ->
            binding.darkModeSwitch.isChecked = isDarkModeActive
        }

        // Observe reminder settings
        viewModel.getReminderState().observe(viewLifecycleOwner) { isReminderActive ->
            binding.ReminderSwitc.setOnCheckedChangeListener(null)
            binding.ReminderSwitc.isChecked = isReminderActive
            setupListeners()
        }

        // Mengamati pesan toast
        viewModel.toastMessage.observe(viewLifecycleOwner) { message ->
            message?.let {
                Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
                viewModel.clearToastMessage() // Menghapus pesan setelah ditampilkan
            }
        }

    }

    private fun setupListeners() {
        binding.darkModeSwitch.setOnCheckedChangeListener { _, isChecked ->
            viewModel.saveThemeSetting(isChecked)
            Log.d("SettingsFragment", "Dark mode setting changed: $isChecked")
        }

        binding.ReminderSwitc.setOnCheckedChangeListener { _, isChecked ->
            viewModel.setReminder(isChecked)
            Log.d("SettingsFragment", "Reminder setting changed: $isChecked")
        }
    }


}
