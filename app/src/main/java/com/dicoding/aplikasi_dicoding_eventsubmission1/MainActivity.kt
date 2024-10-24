package com.dicoding.aplikasi_dicoding_eventsubmission1

import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.dicoding.aplikasi_dicoding_eventsubmission1.databinding.ActivityMainBinding
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var connectivityManager: ConnectivityManager
    private val viewModel: NetworkViewModel by viewModels() // Network viewmodel

    private val mainViewModel: EventViewModel by viewModels { // Theme viewmodel
        ViewModelFactory.getInstance(this)
    }

    private lateinit var networkCallback: ConnectivityManager.NetworkCallback

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home,
                R.id.navigation_upcoming,
                R.id.navigation_finished,
                R.id.navigation_favourite,
                R.id.navigation_setting
            )
        )

        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        // Inisialisasi ConnectivityManager dan pantau jaringan di seluruh aplikasi
        connectivityManager = getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager

        observeNetwork()

        // Periksa status koneksi saat aplikasi dibuka
        checkInitialConnection()

        // Observe Setting
        observeSetting()

    }

    private fun checkInitialConnection() {
        val network = connectivityManager.activeNetwork
        val networkCapabilities = connectivityManager.getNetworkCapabilities(network)
        val isConnected =
            networkCapabilities?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) == true
        viewModel.setConnectionStatus(isConnected)
    }

    private fun observeNetwork() {
        val networkRequest =
            NetworkRequest.Builder()
                .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
                .addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
                .build()

        // Inisialisasi networkCallback
        networkCallback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                Log.d("NetworkStatus", "Network available")
                viewModel.setConnectionStatus(true)
            }

            override fun onLost(network: Network) {
                Log.d("NetworkStatus", "Network lost")
                viewModel.setConnectionStatus(false)
            }
        }

        connectivityManager.registerNetworkCallback(networkRequest, networkCallback)
    }

    // Observe Setting
    private fun observeSetting() {
        mainViewModel.getThemeSettings().observe(this) { isDarkModeActive: Boolean ->
            if (isDarkModeActive) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
        }

        mainViewModel.getReminderState().observe(this) { isActive ->
            mainViewModel.setReminder(isActive)
        }
    }

    // Unregister NetworkCallback saat activity dihancurkan
    override fun onDestroy() {
        super.onDestroy()
        connectivityManager.unregisterNetworkCallback(networkCallback)
    }

}