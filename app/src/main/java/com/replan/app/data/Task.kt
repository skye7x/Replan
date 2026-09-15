package com.replan.app.data

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * A single to-do item.
 *
 * @param startAt when the user plans to START working on the task (epoch millis), nullable.
 * @param dueAt when the task is DUE / must be finished (epoch millis), nullable.
 */
@Entity(tableName = "tasks")
data class Task(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val title: String,
    val description: String = "",
    val startAt: Long? = null,
    val dueAt: Long? = null,
    val isDone: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)
