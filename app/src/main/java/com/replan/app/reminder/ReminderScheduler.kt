package com.replan.app.reminder

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import com.replan.app.data.Task

object ReminderScheduler {

    fun scheduleReminders(context: Context, task: Task) {
        cancelReminders(context, task.id)

        if (task.isDone) return

        task.startAt?.let { startTime ->
            if (startTime > System.currentTimeMillis()) {
                schedule(context, task.id, task.title, startTime, ReminderReceiver.TYPE_START)
            }
        }

        task.dueAt?.let { dueTime ->
            if (dueTime > System.currentTimeMillis()) {
                schedule(context, task.id, task.title, dueTime, ReminderReceiver.TYPE_DUE)
            }
        }
    }

    fun cancelReminders(context: Context, taskId: Long) {
        cancelOne(context, taskId, ReminderReceiver.TYPE_START)
        cancelOne(context, taskId, ReminderReceiver.TYPE_DUE)
    }

    private fun schedule(context: Context, taskId: Long, title: String, triggerAt: Long, type: String) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val pendingIntent = buildPendingIntent(context, taskId, title, type)

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && !alarmManager.canScheduleExactAlarms()) {
                alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAt, pendingIntent)
            } else {
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAt, pendingIntent)
            }
        } catch (e: SecurityException) {
            alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAt, pendingIntent)
        }
    }

    private fun cancelOne(context: Context, taskId: Long, type: String) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val pendingIntent = buildPendingIntent(context, taskId, title = "", type = type)
        alarmManager.cancel(pendingIntent)
    }

    private fun buildPendingIntent(context: Context, taskId: Long, title: String, type: String): PendingIntent {
        val intent = Intent(context, ReminderReceiver::class.java).apply {
            putExtra(ReminderReceiver.EXTRA_TASK_ID, taskId)
            putExtra(ReminderReceiver.EXTRA_TITLE, title)
            putExtra(ReminderReceiver.EXTRA_TYPE, type)
        }
        val requestCode = requestCodeFor(taskId, type)
        return PendingIntent.getBroadcast(
            context,
            requestCode,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

    private fun requestCodeFor(taskId: Long, type: String): Int {
        val base = (taskId * 10).toInt()
        return if (type == ReminderReceiver.TYPE_START) base + 1 else base + 2
    }
}
