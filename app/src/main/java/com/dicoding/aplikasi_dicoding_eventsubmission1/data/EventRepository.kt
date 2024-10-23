package com.dicoding.aplikasi_dicoding_eventsubmission1.data

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import com.dicoding.aplikasi_dicoding_eventsubmission1.data.entitiy.EventEntitiy
import com.dicoding.aplikasi_dicoding_eventsubmission1.data.retrofit.ApiService
import com.dicoding.aplikasi_dicoding_eventsubmission1.data.room.EventDao
import retrofit2.HttpException
import java.io.IOException

class EventRepository private constructor(
    private val apiService: ApiService,
    private val eventDao: EventDao,
    ) {

    fun getUpcomingEvent(): LiveData<Result<List<EventEntitiy>>> = liveData {
        emit(Result.Loading)
        try {
            val databaseLocal = eventDao.getUpcomingEvent()
            Log.d("EventRepository", "getUpcomingEvent - Local Data: $databaseLocal")

            if (databaseLocal.isNotEmpty()) {
                emit(Result.Success(databaseLocal))
            } else {
                try {
                    val response = apiService.getListEvents(active = 1)
                    val events = response.listEvents.sortedBy { it.beginTime }
                    Log.d("EventRepository", "API Raw Response Upcoming: ${events.size}")

                    val eventList = events.map { event ->
                        EventEntitiy(
                            id = event.id,
                            name = event.name,
                            summary = event.summary,
                            mediaCover = event.mediaCover,
                            registrants = event.registrants,
                            imageLogo = event.imageLogo,
                            link = event.link,
                            description = event.description,
                            ownerName = event.ownerName,
                            cityName = event.cityName,
                            quota = event.quota,
                            beginTime = event.beginTime,
                            endTime = event.endTime,
                            category = event.category,
                            isActive = true
                        )
                    }

                    eventDao.deleteUpcomingEvents() // Clear old events
                    eventDao.insertEvents(eventList) // Insert new events
                    emit(Result.Success(eventList))
                } catch (e: IOException) {
                    Log.e("EventRepository", "Network error: ${e.message}") // Log specific network error
                    emit(Result.Error("Gagal memuat acara mendatang, periksa koneksi jaringan Anda."))
                } catch (e: HttpException) {
                    Log.e("EventRepository", "API error: ${e.message()}") // Log specific API error
                    emit(Result.Error("Terjadi kesalahan saat menghubungi server, coba lagi nanti."))
                } catch (e: Exception) {
                    Log.e("EventRepository", "Unknown error: ${e.message}") // Log any other error
                    emit(Result.Error("Gagal memuat acara mendatang, silakan coba lagi."))
                }
            }
        } catch (e: Exception) {
            Log.e("EventRepository", "Database error: ${e.message}") // Log database error
            emit(Result.Error("Koneksi gagal dan tidak ada data lokal yang ditemukan."))
        }
    }

    fun getFinishedEvent(): LiveData<Result<List<EventEntitiy>>> = liveData {
        emit(Result.Loading)
        try {
            val databaseLocal = eventDao.getFinishedEvent()
            Log.d("EventRepository", "getFinishedEvent - Local Data: $databaseLocal") // Log local data

            if (databaseLocal.isNotEmpty()) {
                emit(Result.Success(databaseLocal))
            } else {
                try {
                    val response = apiService.getListEvents(active = 0)
                    val events = response.listEvents
                    Log.d("EventRepository", "API Raw Response Finished: ${events.size}")

                    val eventList = events.map { event ->
                        EventEntitiy(
                            id = event.id,
                            name = event.name,
                            summary = event.summary,
                            mediaCover = event.mediaCover,
                            registrants = event.registrants,
                            imageLogo = event.imageLogo,
                            link = event.link,
                            description = event.description,
                            ownerName = event.ownerName,
                            cityName = event.cityName,
                            quota = event.quota,
                            beginTime = event.beginTime,
                            endTime = event.endTime,
                            category = event.category,
                            isActive = false
                        )
                    }
                    Log.d("EventRepository", "getFinishedEvent - API Data: $eventList") // Log API data

                    eventDao.deleteFinishedEvents() // Clear old data
                    eventDao.insertEvents(eventList) // Insert new data

                    emit(Result.Success(eventList))
                } catch (e: IOException) {
                    Log.e("EventRepository", "Network error: ${e.message}") // Log specific error
                    emit(Result.Error("Gagal memproses data, periksa koneksi jaringan Anda."))
                } catch (e: HttpException) {
                    Log.e("EventRepository", "API error: ${e.message()}") // Log specific error
                    emit(Result.Error("Terjadi kesalahan saat menghubungi server, coba lagi nanti."))
                } catch (e: Exception) {
                    Log.e("EventRepository", "Unknown error: ${e.message}") // Log any other error
                    emit(Result.Error("Gagal memproses data, silakan coba lagi."))
                }
            }
        } catch (e: Exception) {
            Log.e("EventRepository", "Error fetching local data: ${e.message}") // Log error
            emit(Result.Error("Koneksi gagal dan tidak ada data lokal yang ditemukan."))
        }
    }

    fun searchEvents(active: Int, query: String): LiveData<Result<List<EventEntitiy>>> = liveData {
        emit(Result.Loading)
        try {
            val searchResult = eventDao.searchEvents(query, active)
            if (searchResult.isNotEmpty()) {
                emit(Result.Success(searchResult))
            } else {
                try {
                    val response = apiService.getListEvents(active, query)
                    val events = response.listEvents
                    val eventList = events.map { event ->
                        EventEntitiy(
                            id = event.id,
                            name = event.name,
                            summary = event.summary,
                            mediaCover = event.mediaCover,
                            registrants = event.registrants,
                            imageLogo = event.imageLogo,
                            link = event.link,
                            description = event.description,
                            ownerName = event.ownerName,
                            cityName = event.cityName,
                            quota = event.quota,
                            beginTime = event.beginTime,
                            endTime = event.endTime,
                            category = event.category,
                            isActive = false
                        )
                    }
                    eventDao.deleteFinishedEvents()
                    eventDao.insertEvents(eventList)
                    emit(Result.Success(eventList))
                } catch (e: Exception) {
                    emit(Result.Error("Event tidak ditemukan"))
                }
            }
        } catch (e: Exception) {
            emit(Result.Error("Event tidak ditemukan"))
        }
    }


    // Singleton instance
    companion object {
        @Volatile
        private var instance: EventRepository? = null
        fun getInstance(apiService: ApiService, eventDao: EventDao): EventRepository =
            instance ?: synchronized(this) {
                instance ?: EventRepository(apiService, eventDao)
            }.also { instance = it }
    }


}

