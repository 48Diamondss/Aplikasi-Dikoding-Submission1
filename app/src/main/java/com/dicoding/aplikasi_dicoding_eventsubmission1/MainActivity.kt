package com.dicoding.aplikasi_dicoding_eventsubmission1

import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
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
    private val _isConnected = MutableLiveData<Boolean>()
    val isConnected: LiveData<Boolean> get() = _isConnected

    private var toastDisplayed = false // Variabel untuk melacak status toast

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
                R.id.navigation_finished
            )
        )

        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        // Inisialisasi ConnectivityManager dan pantau jaringan di seluruh aplikasi
        connectivityManager = getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
        observeNetwork()

        // Periksa status koneksi saat aplikasi dibuka
        if (savedInstanceState == null) {
            checkInitialConnection()
        } else {
            _isConnected.value = savedInstanceState.getBoolean("isConnected", true)
        }
    }

    private fun checkInitialConnection() {
        val activeNetwork = connectivityManager.activeNetworkInfo
        if (activeNetwork == null || !activeNetwork.isConnected) {
            _isConnected.postValue(false)
            showToast("No internet connection")
        } else {
            _isConnected.postValue(true)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean("isConnected", _isConnected.value != false)
    }

    private fun observeNetwork() {
        val networkRequest = NetworkRequest.Builder()
            .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
            .addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
            .build()

        val networkCallback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                _isConnected.postValue(true)
                toastDisplayed = false // Reset status toast saat terhubung
                showToast("Internet connected")
            }

            override fun onLost(network: Network) {
                _isConnected.postValue(false)
                // Tampilkan toast hanya jika belum ditampilkan
                if (!toastDisplayed) {
                    toastDisplayed = true
                    showToast("No internet connection")
                }
            }
        }

        connectivityManager.registerNetworkCallback(networkRequest, networkCallback)
    }

    private fun showToast(message: String) {
        runOnUiThread {
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        }
    }
}