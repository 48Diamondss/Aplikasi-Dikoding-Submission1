package com.dicoding.aplikasi_dicoding_eventsubmission1

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class NetworkViewModel : ViewModel() {
    private val _isConnected = MutableLiveData<Boolean>()
    val isConnected: LiveData<Boolean> get() = _isConnected

    // Menyimpan status apakah toast sudah ditampilkan
    private var _hasShownNoInternetToast = false
    val hasShownNoInternetToast: Boolean
        get() = _hasShownNoInternetToast

    fun setConnectionStatus(isConnected: Boolean) {
        _isConnected.postValue(isConnected)
    }

    fun setHasShownNoInternetToast(shown: Boolean) {
        _hasShownNoInternetToast = shown
    }
}