package com.replan.app.reminder

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.graphics.Color
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.replan.app.R
import com.replan.app.ReplanApplication
import com.replan.app.ui.MainActivity

class ReminderReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val taskId = intent.getLongExtra(EXTRA_TASK_ID, -1L)
        val title = intent.getStringExtra(EXTRA_TITLE) ?: return
        val type = intent.getStringExtra(EXTRA_TYPE) ?: TYPE_DUE

        if (taskId == -1L) return

        val channelId = if (type == TYPE_START) ReplanApplication.CHANNEL_START else ReplanApplication.CHANNEL_DUE
        val headline = if (type == TYPE_START) "Czas zacząć zadanie" else "Zadanie do zrobienia"
        val contentText = if (type == TYPE_START) "Zacznij: $title" else "Termin minął / mija: $title"

        val openIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            taskId.toInt(),
            openIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(headline)
            .setContentText(contentText)
            .setStyle(NotificationCompat.BigTextStyle().bigText(contentText))
            .setColor(Color.parseColor("#3F51B5"))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        val notificationId = (taskId * 10 + if (type == TYPE_START) 1 else 2).toInt()

        try {
            NotificationManagerCompat.from(context).notify(notificationId, notification)
        } catch (e: SecurityException) {
            // Notification permission not granted by the user; silently ignore.
        }
    }

    companion object {
        const val EXTRA_TASK_ID = "extra_task_id"
        const val EXTRA_TITLE = "extra_title"
        const val EXTRA_TYPE = "extra_type"
        const val TYPE_START = "type_start"
        const val TYPE_DUE = "type_due"
    }
}
