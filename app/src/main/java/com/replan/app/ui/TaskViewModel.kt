package com.replan.app.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.replan.app.ReplanApplication
import com.replan.app.data.Task
import com.replan.app.data.TaskRepository
import com.replan.app.reminder.ReminderScheduler
import kotlinx.coroutines.launch

class TaskViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: TaskRepository = (application as ReplanApplication).repository

    val allTasks: LiveData<List<Task>> = repository.allTasks

    /**
     * Creates a new task (existingId == null) or updates one, then (re)schedules its reminders.
     */
    fun saveTask(
        existingId: Long?,
        title: String,
        description: String,
        startAt: Long?,
        dueAt: Long?
    ) {
        viewModelScope.launch {
            val task = Task(
                id = existingId ?: 0L,
                title = title,
                description = description,
                startAt = startAt,
                dueAt = dueAt
            )

            val finalId = if (existingId == null) {
                repository.insert(task)
            } else {
                repository.update(task)
                existingId
            }

            ReminderScheduler.scheduleReminders(getApplication(), task.copy(id = finalId))
        }
    }

    fun toggleDone(task: Task) {
        viewModelScope.launch {
            val updated = task.copy(isDone = !task.isDone)
            repository.update(updated)
            if (updated.isDone) {
                ReminderScheduler.cancelReminders(getApplication(), updated.id)
            } else {
                ReminderScheduler.scheduleReminders(getApplication(), updated)
            }
        }
    }

    fun deleteTask(task: Task) {
        viewModelScope.launch {
            repository.delete(task)
            ReminderScheduler.cancelReminders(getApplication(), task.id)
        }
    }

    suspend fun getTaskById(id: Long): Task? = repository.getTaskById(id)
}
