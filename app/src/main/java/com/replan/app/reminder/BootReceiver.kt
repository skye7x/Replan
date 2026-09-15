package com.replan.app.reminder

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.replan.app.ReplanApplication
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class BootReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != Intent.ACTION_BOOT_COMPLETED) return

        val pendingResult = goAsync()
        val app = context.applicationContext as ReplanApplication

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val tasks = app.repository.getActiveTasksWithReminders()
                tasks.forEach { task ->
                    ReminderScheduler.scheduleReminders(context, task)
                }
            } finally {
                pendingResult.finish()
            }
        }
    }
}
