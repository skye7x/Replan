package com.replan.app.data

import androidx.lifecycle.LiveData

class TaskRepository(private val dao: TaskDao) {

    val allTasks: LiveData<List<Task>> = dao.getAllTasks()

    suspend fun getTaskById(id: Long): Task? = dao.getTaskById(id)

    suspend fun getActiveTasksWithReminders(): List<Task> = dao.getActiveTasksWithReminders()

    suspend fun insert(task: Task): Long = dao.insert(task)

    suspend fun update(task: Task) = dao.update(task)

    suspend fun delete(task: Task) = dao.delete(task)
}
