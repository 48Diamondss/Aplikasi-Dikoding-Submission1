package com.dicoding.aplikasi_dicoding_eventsubmission1.ui.reminder

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import java.util.concurrent.TimeUnit

class CihuyReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == "android.intent.action.BOOT_COMPLETED") {
            setRepeatingReminder(context)
        }
    }

    private fun setRepeatingReminder(context: Context) {
        val workManagerRequest = PeriodicWorkRequestBuilder<MyReminderWorker>(1, TimeUnit.DAYS)
            .build()
        WorkManager.getInstance(context).enqueue(workManagerRequest)
    }
}