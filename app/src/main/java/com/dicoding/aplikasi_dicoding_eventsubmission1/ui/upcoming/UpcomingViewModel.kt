package com.dicoding.aplikasi_dicoding_eventsubmission1.ui.upcoming

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

            val apiService = ApiConfig.getApiService()

            // Ambil event yang akan datang
            apiService.getListEvents(active = 1).enqueue(object : Callback<ResponseApi> {
                override fun onResponse(call: Call<ResponseApi>, response: Response<ResponseApi>) {
                    if (response.isSuccessful) {
                        response.body()?.listEvents?.take(40)?.let {
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
        }
    }


    fun searchEvents(query: String) {
        _isLoading.postValue(true)  // Mulai loading

        val apiServicesearch = ApiConfig.getApiService()
        apiServicesearch.getListEvents(active = 1, query = query)
            .enqueue(object : Callback<ResponseApi> {
                override fun onResponse(call: Call<ResponseApi>, response: Response<ResponseApi>) {
                    if (response.isSuccessful) {
                        response.body()?.listEvents?.let {
                            _upcomingEvents.postValue(it)
                            _errorMessage.postValue(null)
                        } ?: run {
                            Log.e("SearchEvents", "ListEvents is null")
                            _upcomingEvents.postValue(emptyList())
                            _errorMessage.postValue("No events found.")
                        }
                    } else {
                        Log.e("SearchEvents", "Error: ${response.errorBody()?.string()}")
                        _upcomingEvents.postValue(emptyList())
                        _errorMessage.postValue("Failed to load events.")
                    }
                    _isLoading.postValue(false)  // Selesai loading
                }

                override fun onFailure(call: Call<ResponseApi>, t: Throwable) {
                    Log.e("SearchEvents", "Network error: ${t.message}")
                    _upcomingEvents.postValue(emptyList())
                    _errorMessage.postValue("Network error: ${t.message}")
                    _isLoading.postValue(false)  // Selesai loading
                }
            })
    }


}