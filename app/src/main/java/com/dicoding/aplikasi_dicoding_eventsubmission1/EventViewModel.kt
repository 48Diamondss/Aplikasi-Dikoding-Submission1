package com.dicoding.aplikasi_dicoding_eventsubmission1

import androidx.lifecycle.ViewModel
import com.dicoding.aplikasi_dicoding_eventsubmission1.data.EventRepository

class EventViewModel(private val repository: EventRepository) : ViewModel() {

    fun getUpcomingEvents() = repository.getUpcomingEvent()
    fun getFinishedEvents() = repository.getFinishedEvent()

    fun searchEvent(query: String, isActive: Int) = repository.searchEvents(isActive, query)

}