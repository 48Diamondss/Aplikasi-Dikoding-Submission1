package com.dicoding.aplikasi_dicoding_eventsubmission1

import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.dicoding.aplikasi_dicoding_eventsubmission1.databinding.ActivityMainBinding
import com.google.android.material.bottomnavigation.BottomNavigationView

@Suppress("DEPRECATION")
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var connectivityManager: ConnectivityManager
    private val viewModel: MainViewModel by viewModels() // Mendapatkan instance ViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home, R.id.navigation_upcoming, R.id.navigation_finished, R.id.navigation_favourite
            )
        )

        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        // Inisialisasi ConnectivityManager dan pantau jaringan di seluruh aplikasi
        connectivityManager = getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
        observeNetwork()

        // Periksa status koneksi saat aplikasi dibuka
        checkInitialConnection()

    }

    private fun checkInitialConnection() {
        val activeNetwork = connectivityManager.activeNetworkInfo
        viewModel.setConnectionStatus(activeNetwork != null && activeNetwork.isConnected)
    }

    private fun observeNetwork() {
        val networkRequest =
            NetworkRequest.Builder().addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
                .addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR).build()

        val networkCallback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                viewModel.setConnectionStatus(true)
            }

            override fun onLost(network: Network) {
                viewModel.setConnectionStatus(false)
            }
        }

        connectivityManager.registerNetworkCallback(networkRequest, networkCallback)
    }
}