package com.dicoding.aplikasi_dicoding_eventsubmission1.data.retrofit

import com.dicoding.aplikasi_dicoding_eventsubmission1.data.response.ResponseApi
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {
    @GET("/events")
    suspend fun getListEvents(
        @Query("active") active: Int? = 1,
        @Query("q") query: String? = null,
        @Query("limit") limit: Int? = 40
    ): Response<ResponseApi>
}
