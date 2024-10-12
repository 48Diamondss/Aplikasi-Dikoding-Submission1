package com.dicoding.aplikasi_dicoding_eventsubmission1.data.retrofit

import com.dicoding.aplikasi_dicoding_eventsubmission1.data.response.UpcomingResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {

    @GET("/events")
    fun getListEvents(
        @Query("active") active: Int? = 1, // 1 untuk event aktif, 0 untuk yang sudah selesai, -1 untuk semua
        @Query("q") query: String? = null,
        @Query("limit") limit: Int? = 40 // default 40
    ): Call<UpcomingResponse>
}