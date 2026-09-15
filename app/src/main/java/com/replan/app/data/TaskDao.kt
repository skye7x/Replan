package com.replan.app.data

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface TaskDao {

    @Query(
        "SELECT * FROM tasks " +
            "ORDER BY isDone ASC, " +
            "(CASE WHEN dueAt IS NULL THEN 1 ELSE 0 END) ASC, " +
            "dueAt ASC, createdAt DESC"
    )
    fun getAllTasks(): LiveData<List<Task>>

    @Query("SELECT * FROM tasks WHERE id = :id")
    suspend fun getTaskById(id: Long): Task?

    @Query("SELECT * FROM tasks WHERE isDone = 0 AND (startAt IS NOT NULL OR dueAt IS NOT NULL)")
    suspend fun getActiveTasksWithReminders(): List<Task>

    @Insert
    suspend fun insert(task: Task): Long

    @Update
    suspend fun update(task: Task)

    @Delete
    suspend fun delete(task: Task)
}
