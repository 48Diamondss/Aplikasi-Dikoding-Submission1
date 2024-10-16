package com.dicoding.aplikasi_dicoding_eventsubmission1.ui.home

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dicoding.aplikasi_dicoding_eventsubmission1.data.response.ListEventsItem
import com.dicoding.aplikasi_dicoding_eventsubmission1.data.response.ResponseApi
import com.dicoding.aplikasi_dicoding_eventsubmission1.data.retrofit.ApiConfig
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

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

            val apiService = ApiConfig.getApiService()

            // Ambil event yang akan datang
            apiService.getListEvents(active = 1).enqueue(object : Callback<ResponseApi> {
                override fun onResponse(call: Call<ResponseApi>, response: Response<ResponseApi>) {
                    if (response.isSuccessful) {
                        response.body()?.listEvents?.take(5)?.let {
                            _upcomingEvents.postValue(it)
                            _errorMessage.postValue(null)
                        } ?: run {
                            Log.e("FetchEvents", "ListEvents is null")
                            _upcomingEvents.postValue(emptyList())
                            _errorMessage.postValue("No upcoming events available.")
                        }
                    } else {
                        Log.e("FetchEvents", "Error: ${response.errorBody()?.string()}")
                        _upcomingEvents.postValue(emptyList())
                        _errorMessage.postValue("Failed to load upcoming events.")
                    }
                    _isLoading.postValue(false)  // Selesai loading
                }

                override fun onFailure(call: Call<ResponseApi>, t: Throwable) {
                    Log.e("FetchEvents", "Network error: ${t.message}")
                    _upcomingEvents.postValue(emptyList())
                    _errorMessage.postValue("Network error: ${t.message}")
                    _isLoading.postValue(false)  // Selesai loading
                }
            })

            // Ambil event yang sudah selesai
            apiService.getListEvents(active = 0).enqueue(object : Callback<ResponseApi> {
                override fun onResponse(call: Call<ResponseApi>, response: Response<ResponseApi>) {
                    if (response.isSuccessful) {
                        response.body()?.listEvents?.take(5)?.let {
                            _finishedEvents.postValue(it)
                            _errorMessage.postValue(null)
                        } ?: run {
                            Log.e("FetchEvents", "ListEvents is null")
                            _finishedEvents.postValue(emptyList())
                            _errorMessage.postValue("No finished events available.")
                        }
                    } else {
                        Log.e("FetchEvents", "Error: ${response.errorBody()?.string()}")
                        _finishedEvents.postValue(emptyList())
                        _errorMessage.postValue("Failed to load finished events.")
                    }
                    _isLoading.postValue(false)  // Selesai loading
                }

                override fun onFailure(call: Call<ResponseApi>, t: Throwable) {
                    Log.e("FetchEvents", "Network error: ${t.message}")
                    _finishedEvents.postValue(emptyList())
                    _errorMessage.postValue("Network error: ${t.message}")
                    _isLoading.postValue(false)  // Selesai loading
                }
            })
        }
    }
}