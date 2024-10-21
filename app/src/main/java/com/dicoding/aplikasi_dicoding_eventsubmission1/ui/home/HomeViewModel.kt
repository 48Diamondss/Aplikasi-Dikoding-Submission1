package com.dicoding.aplikasi_dicoding_eventsubmission1.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dicoding.aplikasi_dicoding_eventsubmission1.data.response.ListEventsItem
import com.dicoding.aplikasi_dicoding_eventsubmission1.data.retrofit.ApiConfig
import kotlinx.coroutines.launch

class HomeViewModel : ViewModel() {

    private val _upcomingEvents = MutableLiveData<List<ListEventsItem>>()
    val upcomingEvents: LiveData<List<ListEventsItem>> = _upcomingEvents

    private val _finishedEvents = MutableLiveData<List<ListEventsItem>>()
    val finishedEvents: LiveData<List<ListEventsItem>> = _finishedEvents

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    fun fetchEvents() {
        // Hanya tampilkan loading jika data belum ada
        if (_upcomingEvents.value == null && _finishedEvents.value == null) {
            _isLoading.postValue(true)  // Mulai loading

            viewModelScope.launch {
                val apiService = ApiConfig.getApiService()

                try {
                    // Ambil event yang akan datang
                    val upcomingResponse = apiService.getListEvents(active = 1)
                    if (upcomingResponse.isSuccessful) {
                        upcomingResponse.body()?.listEvents?.take(5)?.let {
                            _upcomingEvents.postValue(it)
                            _errorMessage.postValue(null)
                        } ?: run {
                            _upcomingEvents.postValue(emptyList())
                            _errorMessage.postValue("No upcoming events available.")
                        }
                    } else {
                        _upcomingEvents.postValue(emptyList())
                        _errorMessage.postValue("Failed to load upcoming events.")
                    }

                    // Ambil event yang sudah selesai
                    val finishedResponse = apiService.getListEvents(active = 0)
                    if (finishedResponse.isSuccessful) {
                        finishedResponse.body()?.listEvents?.take(5)?.let {
                            _finishedEvents.postValue(it)
                            _errorMessage.postValue(null)
                        } ?: run {
                            _finishedEvents.postValue(emptyList())
                            _errorMessage.postValue("No finished events available.")
                        }
                    } else {
                        _finishedEvents.postValue(emptyList())
                        _errorMessage.postValue("Failed to load finished events.")
                    }
                } catch (e: Exception) {
                    _upcomingEvents.postValue(emptyList())
                    _finishedEvents.postValue(emptyList())
                    _errorMessage.postValue("Network error: ${e.message}")
                } finally {
                    _isLoading.postValue(false)  // Selesai loading
                }
            }
        }
    }
}