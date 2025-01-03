package com.dicoding.aplikasi_dicoding_eventsubmission1

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import com.dicoding.aplikasi_dicoding_eventsubmission1.data.EventRepository
import com.dicoding.aplikasi_dicoding_eventsubmission1.data.Result
import com.dicoding.aplikasi_dicoding_eventsubmission1.data.entitiy.EventEntitiy
import com.dicoding.aplikasi_dicoding_eventsubmission1.ui.reminder.MyReminderWorker
import com.dicoding.aplikasi_dicoding_eventsubmission1.ui.setting.SettingPreferences
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

class EventViewModel(
    private val repository: EventRepository,
    private val pref: SettingPreferences,
    private val workManager: androidx.work.WorkManager
) : ViewModel() {

    private val _favoriteEvents = MutableLiveData<Result<List<EventEntitiy>>>()
    val favoriteEvents: LiveData<Result<List<EventEntitiy>>> = _favoriteEvents

    // Flag to track if the "no events" & eror toast has been shown
    var hasShownNoEventsToast = false

    var hasShownErrorToast = false

    // MutableLiveData untuk menampilkan pesan toast
    private val _toastMessage = MutableLiveData<String?>()
    val toastMessage: MutableLiveData<String?> get() = _toastMessage

    // Flag untuk melacak apakah toast pengingat sudah ditampilkan
    private var hasShownReminderToast = false



    fun getUpcomingEvents() = repository.getUpcomingEvent()
    fun getFinishedEvents() = repository.getFinishedEvent()

    fun searchEvent(query: String, isActive: Int) = repository.searchEvents(isActive, query)


    // Method to add a favorite event
    fun addFavoriteEvent(eventId: String) {
        viewModelScope.launch {
            repository.addFavoriteEvent(eventId)
        }
    }

    // Method to delete a favorite event
    fun deleteFavoriteEvent(eventId: String) {
        viewModelScope.launch {
            repository.deleteFavoriteEvent(eventId)
        }
    }

    // Method to check if an event is a favorite
    fun isEventFavorite(eventId: String): LiveData<Boolean> {
        return liveData {
            val result = repository.isEventFavorite(eventId)
            emit(result)
        }
    }

    // Method to fetch favorite events
    fun fetchFavoriteEvents() {
        viewModelScope.launch {
            _favoriteEvents.value = repository.getFavoriteEvents()
        }
    }

    // Method for theme settings
    fun getThemeSettings(): LiveData<Boolean> {
        return pref.getThemeSetting().asLiveData()
    }

    fun saveThemeSetting(isDarkModeActive: Boolean) {
        viewModelScope.launch {
            pref.saveThemeSetting(isDarkModeActive)
        }
    }

    // Method for reminder settings
    fun getReminderState(): LiveData<Boolean> {
        return pref.getReminderSetting().asLiveData()
    }

    fun setReminder(isReminderActive: Boolean) {
        viewModelScope.launch {
            pref.setReminderSetting(isReminderActive)
            updateReminderSchedule(isReminderActive)

            // Logika untuk menampilkan toast
            if (isReminderActive && !hasShownReminderToast) {
                hasShownReminderToast = true
                _toastMessage.value = "Pengingat diaktifkan"
            } else if (!isReminderActive && hasShownReminderToast) {
                hasShownReminderToast = false
                _toastMessage.value = "Pengingat dinonaktifkan"
            }
        }
    }

    private fun updateReminderSchedule(isActive: Boolean) {
        viewModelScope.launch {
            val reminderRequest = PeriodicWorkRequestBuilder<MyReminderWorker>(1, TimeUnit.DAYS)
                .addTag(MyReminderWorker.WORK_NAME)
                .build()

            if (isActive) {
                workManager.enqueueUniquePeriodicWork(
                    MyReminderWorker.WORK_NAME,
                    ExistingPeriodicWorkPolicy.KEEP,
                    reminderRequest
                )
            } else {
                workManager.cancelAllWorkByTag(MyReminderWorker.WORK_NAME)
            }
        }
    }

    fun clearToastMessage() {
        _toastMessage.value = null
    }


}