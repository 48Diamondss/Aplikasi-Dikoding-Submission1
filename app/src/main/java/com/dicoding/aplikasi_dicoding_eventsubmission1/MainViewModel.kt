package com.dicoding.aplikasi_dicoding_eventsubmission1

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MainViewModel : ViewModel() {
    private val _isConnected = MutableLiveData<Boolean>()
    val isConnected: LiveData<Boolean> get() = _isConnected

    fun setConnectionStatus(status: Boolean) {
        _isConnected.postValue(status)
    }
}