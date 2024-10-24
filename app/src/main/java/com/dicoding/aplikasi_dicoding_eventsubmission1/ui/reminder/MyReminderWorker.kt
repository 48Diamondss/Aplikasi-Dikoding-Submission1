package com.dicoding.aplikasi_dicoding_eventsubmission1.ui.reminder

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.dicoding.aplikasi_dicoding_eventsubmission1.MainActivity
import com.dicoding.aplikasi_dicoding_eventsubmission1.R.drawable.dicodinglogo
import com.dicoding.aplikasi_dicoding_eventsubmission1.data.EventRepository
import com.dicoding.aplikasi_dicoding_eventsubmission1.data.entitiy.EventEntitiy
import com.dicoding.aplikasi_dicoding_eventsubmission1.data.retrofit.ApiConfig
import com.dicoding.aplikasi_dicoding_eventsubmission1.data.room.EventDatabase
import java.text.SimpleDateFormat
import java.util.Locale

class MyReminderWorker(
    context: Context,
    workerParameters: WorkerParameters
) : CoroutineWorker(context, workerParameters) {

    companion object {
        const val WORK_NAME = "MyReminderWorker"
        const val NOTIFICATION_ID = 1
        const val CHANNEL_ID = "channel_01"
        const val CHANNEL_NAME = "reminder_channel"
    }

    override suspend fun doWork(): Result {
        return try {
            val eventRepository = EventRepository.getInstance(
                ApiConfig.getApiService(),
                EventDatabase.getDatabase(applicationContext).eventDao()
            )

            // 1. Ambil data terbaru dari API
            val updatedEvents = eventRepository.fetchAndUpdateEvents()

            // 2. Log jumlah event yang diperbarui
            Log.d("MyReminderWorker", "Successfully updated ${updatedEvents.size} events.")

            // 3. Ambil event terdekat dari database lokal
            val closestEvents = eventRepository.getNotifEvent()

            // 4. Tampilkan notifikasi jika ada event terdekat
            if (closestEvents != null) {
                showNotification(closestEvents)
            } else if (updatedEvents.isEmpty()) {
                // Jika tidak ada event terdekat setelah update
                Log.d("MyReminderWorker", "No new events found.")
            }
            Result.success()
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure()
        }
    }

    private fun showNotification(event: EventEntitiy) {
        val notificationManager =
            applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val intent = Intent(applicationContext, MainActivity::class.java).apply {
            putExtra("eventId", event.id)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val pendingIntent: PendingIntent = PendingIntent.getActivity(
            applicationContext, 0, intent, PendingIntent.FLAG_IMMUTABLE
        )

        val subStringEventName = event.name?.let {
            if (it.length > 30) "${it.substring(0, 30)}..." else it
        } ?: "Unknown Event"

        val formattedDate = FormatDate().formatNotificationDateTime(event.beginTime ?: "Selesai")
        val notificationText = "$subStringEventName begins at $formattedDate"

        val notification = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
            .setSmallIcon(dicodinglogo)
            .setContentTitle("Upcoming Event")
            .setContentText(notificationText)
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)

        // Create the notification channel if it doesn't already exist
        createNotificationChannel(notificationManager)

        notificationManager.notify(NOTIFICATION_ID, notification.build())
    }

    private fun createNotificationChannel(notificationManager: NotificationManager) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager.createNotificationChannel(channel)
        }
    }

    private class FormatDate {
        fun formatNotificationDateTime(input: String): String {
            val inputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
            val outputFormat = SimpleDateFormat("dd MMMM yyyy HH:mm", Locale.getDefault())
            val date = inputFormat.parse(input)
            return date?.let { outputFormat.format(it) } ?: ""
        }
    }

}