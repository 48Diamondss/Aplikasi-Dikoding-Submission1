package com.dicoding.aplikasi_dicoding_eventsubmission1

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.dicoding.aplikasi_dicoding_eventsubmission1.data.EventRepository
import com.dicoding.aplikasi_dicoding_eventsubmission1.data.di.Injection
import com.dicoding.aplikasi_dicoding_eventsubmission1.ui.setting.SettingPreferences

class ViewModelFactory private constructor(
    private val eventRepository: EventRepository,
    private val settingPreferences: SettingPreferences,
    private val workManager: androidx.work.WorkManager
) :
    ViewModelProvider.NewInstanceFactory() {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(EventViewModel::class.java)) {
            return EventViewModel(eventRepository, settingPreferences, workManager) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
    }

    companion object {
        @Volatile
        private var instance: ViewModelFactory? = null
        fun getInstance(context: Context): ViewModelFactory =
            instance ?: synchronized(this) {
                val preferences = Injection.provideSettingPreferences(context)
                val workManagerr = androidx.work.WorkManager.getInstance(context)
                instance ?: ViewModelFactory(
                    Injection.provideRepository(context),
                    preferences,
                    workManagerr
                )
            }.also { instance = it }
    }
}