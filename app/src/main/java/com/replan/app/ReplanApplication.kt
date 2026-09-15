package com.replan.app

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import com.replan.app.data.AppDatabase
import com.replan.app.data.TaskRepository

class ReplanApplication : Application() {

    val database: AppDatabase by lazy { AppDatabase.getInstance(this) }
    val repository: TaskRepository by lazy { TaskRepository(database.taskDao()) }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannels()
    }

    private fun createNotificationChannels() {
        val manager = getSystemService(NotificationManager::class.java)

        val startChannel = NotificationChannel(
            CHANNEL_START,
            "Start zadania",
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = "Przypomnienia o rozpoczęciu zadania"
        }

        val dueChannel = NotificationChannel(
            CHANNEL_DUE,
            "Termin zadania",
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = "Przypomnienia o terminie wykonania zadania"
        }

        manager.createNotificationChannel(startChannel)
        manager.createNotificationChannel(dueChannel)
    }

    companion object {
        const val CHANNEL_START = "channel_start"
        const val CHANNEL_DUE = "channel_due"
    }
}
