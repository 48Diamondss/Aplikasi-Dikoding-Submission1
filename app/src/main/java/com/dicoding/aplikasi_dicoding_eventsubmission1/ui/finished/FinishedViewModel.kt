package com.dicoding.aplikasi_dicoding_eventsubmission1.ui.finished

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dicoding.aplikasi_dicoding_eventsubmission1.data.response.ListEventsItem
import com.dicoding.aplikasi_dicoding_eventsubmission1.data.retrofit.ApiConfig
import kotlinx.coroutines.launch

class FinishedViewModel : ViewModel() {

    private val _finishedEvents = MutableLiveData<List<ListEventsItem>>()
    val finishedEvents: LiveData<List<ListEventsItem>> = _finishedEvents

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    fun fetchEventsFinished(forceReload: Boolean = false) {
        if (_finishedEvents.value == null || forceReload) {
            _isLoading.postValue(true)  // Mulai loading

            viewModelScope.launch {
                try {
                    val apiService = ApiConfig.getApiService()
                    val response = apiService.getListEvents(active = 0)

                    if (response.isSuccessful) {
                        response.body()?.listEvents?.let {
                            _finishedEvents.postValue(it.take(40)) // Ambil 40 item
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
                    _finishedEvents.postValue(emptyList())
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
            try {
                val apiServiceSearch = ApiConfig.getApiService()
                val response = apiServiceSearch.getListEvents(active = 0, query = query)

                if (response.isSuccessful) {
                    response.body()?.listEvents?.let {
                        _finishedEvents.postValue(it)
                        _errorMessage.postValue(null)
                    } ?: run {
                        _finishedEvents.postValue(emptyList())
                        _errorMessage.postValue("No events found.")
                    }
                } else {
                    _finishedEvents.postValue(emptyList())
                    _errorMessage.postValue("Failed to load events.")
                }
            } catch (e: Exception) {
                _finishedEvents.postValue(emptyList())
                _errorMessage.postValue("Network error: ${e.message}")
            } finally {
                _isLoading.postValue(false)  // Selesai loading
            }
        }
    }
}
