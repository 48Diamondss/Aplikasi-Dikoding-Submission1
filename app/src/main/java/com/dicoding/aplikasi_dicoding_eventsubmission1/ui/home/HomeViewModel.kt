package com.dicoding.aplikasi_dicoding_eventsubmission1.ui.home

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dicoding.aplikasi_dicoding_eventsubmission1.data.response.ListEventsItem
import com.dicoding.aplikasi_dicoding_eventsubmission1.data.response.UpcomingResponse
import com.dicoding.aplikasi_dicoding_eventsubmission1.data.retrofit.ApiConfig
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomeViewModel : ViewModel() {

    private val _upcomingEvents = MutableLiveData<List<ListEventsItem>>()
    val upcomingEvents: LiveData<List<ListEventsItem>> = _upcomingEvents

    private val _finishedEvents = MutableLiveData<List<ListEventsItem>>()
    val finishedEvents: LiveData<List<ListEventsItem>> = _finishedEvents

    fun fetchEvents() {
        val apiService = ApiConfig.getApiService()

        // Ambil event yang akan datang
        apiService.getListEvents(active = 1).enqueue(object : Callback<UpcomingResponse> {
            override fun onResponse(call: Call<UpcomingResponse>, response: Response<UpcomingResponse>) {
                if (response.isSuccessful) {
                    response.body()?.listEvents?.take(5)?.let { // Ambil maksimal 5 event
                        _upcomingEvents.postValue(it)
                    } ?: run {
                        // Jika listEvents adalah null
                        Log.e("FetchEvents", "ListEvents is null")
                        _upcomingEvents.postValue(emptyList())
                    }
                } else {
                    // Tangani kesalahan respons yang tidak berhasil
                    Log.e("FetchEvents", "Error: ${response.errorBody()?.string()}")
                    _upcomingEvents.postValue(emptyList())
                }
            }

            override fun onFailure(call: Call<UpcomingResponse>, t: Throwable) {

                Log.e("FetchEvents", "Network error: ${t.message}")
                _upcomingEvents.postValue(emptyList())

            }
        })

        // Ambil event yang sudah selesai
        apiService.getListEvents(active = 0).enqueue(object : Callback<UpcomingResponse> {
            override fun onResponse(call: Call<UpcomingResponse>, response: Response<UpcomingResponse>) {
                if (response.isSuccessful) {
                    response.body()?.listEvents?.take(5)?.let { // Ambil maksimal 5 event
                        _finishedEvents.postValue(it)
                    } ?: run {
                        // Jika listEvents adalah null
                        Log.e("FetchEvents", "ListEvents is null")
                        _finishedEvents.postValue(emptyList())
                    }
                } else {
                    // Tangani kesalahan respons yang tidak berhasil
                    Log.e("FetchEvents", "Error: ${response.errorBody()?.string()}")
                    _finishedEvents.postValue(emptyList())
                }
            }

            override fun onFailure(call: Call<UpcomingResponse>, t: Throwable) {
                Log.e("FetchEvents", "Network error: ${t.message}")

                _finishedEvents.postValue(emptyList())
            }
        })
    }
}
