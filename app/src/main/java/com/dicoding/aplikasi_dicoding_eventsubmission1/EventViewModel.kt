package com.dicoding.aplikasi_dicoding_eventsubmission1

import androidx.lifecycle.ViewModel
import com.dicoding.aplikasi_dicoding_eventsubmission1.data.EventRepository

class EventViewModel(private val repository: EventRepository) : ViewModel() {

    // Flag to track if the "no events" & eror toast has been shown
    var hasShownNoEventsToast = false

    var hasShownErrorToast = false


    fun getUpcomingEvents() = repository.getUpcomingEvent()
    fun getFinishedEvents() = repository.getFinishedEvent()

    fun searchEvent(query: String, isActive: Int) = repository.searchEvents(isActive, query)

}