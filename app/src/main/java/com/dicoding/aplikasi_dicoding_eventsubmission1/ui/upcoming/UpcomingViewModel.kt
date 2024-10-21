package com.dicoding.aplikasi_dicoding_eventsubmission1.ui.upcoming

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dicoding.aplikasi_dicoding_eventsubmission1.data.response.ListEventsItem
import com.dicoding.aplikasi_dicoding_eventsubmission1.data.retrofit.ApiConfig
import kotlinx.coroutines.launch

class UpcomingViewModel : ViewModel() {

    private val _upcomingEvents = MutableLiveData<List<ListEventsItem>>()
    val upcomingEvents: LiveData<List<ListEventsItem>> = _upcomingEvents

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    fun fetchEvents(forceReload: Boolean = false) {
        if (_upcomingEvents.value == null || forceReload) {
            _isLoading.postValue(true)  // Mulai loading

            viewModelScope.launch {
                val apiService = ApiConfig.getApiService()

                try {
                    // Ambil event yang akan datang
                    val response = apiService.getListEvents(active = 1)
                    if (response.isSuccessful) {
                        response.body()?.listEvents?.take(40)?.let {
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
                } catch (e: Exception) {
                    _upcomingEvents.postValue(emptyList())
                    _errorMessage.postValue("Network error: ${e.message}")
                } finally {
                    _isLoading.postValue(false)  // Selesai loading
                }
            }
        }
    }

    fun searchEvents(query: String) {
        _isLoading.postValue(true)  // Mulai loading

        viewModelScope.launch {
            val apiService = ApiConfig.getApiService()
            try {
                val response = apiService.getListEvents(active = 1, query = query)
                if (response.isSuccessful) {
                    response.body()?.listEvents?.let {
                        _upcomingEvents.postValue(it)
                        _errorMessage.postValue(null)
                    } ?: run {
                        _upcomingEvents.postValue(emptyList())
                        _errorMessage.postValue("No events found.")
                    }
                } else {
                    _upcomingEvents.postValue(emptyList())
                    _errorMessage.postValue("Failed to load events.")
                }
            } catch (e: Exception) {
                _upcomingEvents.postValue(emptyList())
                _errorMessage.postValue("Network error: ${e.message}")
            } finally {
                _isLoading.postValue(false)  // Selesai loading
            }
        }
    }
}