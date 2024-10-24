package com.dicoding.aplikasi_dicoding_eventsubmission1.data.di

import android.content.Context
import com.dicoding.aplikasi_dicoding_eventsubmission1.data.EventRepository
import com.dicoding.aplikasi_dicoding_eventsubmission1.data.retrofit.ApiConfig
import com.dicoding.aplikasi_dicoding_eventsubmission1.data.room.EventDatabase
import com.dicoding.aplikasi_dicoding_eventsubmission1.ui.setting.SettingPreferences
import com.dicoding.aplikasi_dicoding_eventsubmission1.ui.setting.dataStore

object Injection {
    fun provideRepository(context: Context): EventRepository {
        val apiService = ApiConfig.getApiService()
        val database = EventDatabase.getDatabase(context)
        val dao = database.eventDao()
        return EventRepository.getInstance(apiService, dao)
    }

    fun provideSettingPreferences(context: Context): SettingPreferences {
        return SettingPreferences.getInstance(context.dataStore)
    }
}