package com.dicoding.aplikasi_dicoding_eventsubmission1.ui.setting

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class SettingPreferences private constructor(private val dataStore: DataStore<Preferences>) {

    private val themekey = booleanPreferencesKey("theme_setting")
    private val reminderKey = booleanPreferencesKey("reminder_setting")

    // Theme
    fun getThemeSetting(): Flow<Boolean> {
        return dataStore.data
            .catch { emit(emptyPreferences()) }
            .map { preferences ->
            preferences[themekey] ?: false
        }
    }

    // Theme
    suspend fun saveThemeSetting(isDarkModeActive: Boolean) {
        dataStore.edit { preferences ->
            preferences[themekey] = isDarkModeActive
        }
    }

    // Reminder
    fun getReminderSetting(): Flow<Boolean> = dataStore.data.map { preferences ->
        preferences[reminderKey] ?: false
    }

    // Reminder
    suspend fun setReminderSetting(isReminderActive: Boolean) {
        dataStore.edit { preferences ->
            preferences[reminderKey] = isReminderActive
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: SettingPreferences? = null

        fun getInstance(dataStore: DataStore<Preferences>): SettingPreferences {
            return INSTANCE ?: synchronized(this) {
                val instance = SettingPreferences(dataStore)
                INSTANCE = instance
                instance
            }
        }
    }

}