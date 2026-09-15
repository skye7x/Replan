package com.replan.app.ui

import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.replan.app.data.Task
import com.replan.app.databinding.ItemTaskBinding
import java.util.Date

class TaskAdapter(
    private val onItemClick: (Task) -> Unit,
    private val onCheckChanged: (Task) -> Unit
) : ListAdapter<Task, TaskAdapter.TaskViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val binding = ItemTaskBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TaskViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class TaskViewHolder(private val binding: ItemTaskBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(task: Task) {
            // Avoid firing the listener while we programmatically set the checked state.
            binding.checkboxDone.setOnCheckedChangeListener(null)

            binding.textTitle.text = task.title
            binding.textTitle.paint.isStrikeThruText = task.isDone

            if (task.description.isNotBlank()) {
                binding.textDescription.visibility = View.VISIBLE
                binding.textDescription.text = task.description
            } else {
                binding.textDescription.visibility = View.GONE
            }

            val dateInfo = StringBuilder()
            task.startAt?.let {
                dateInfo.append("Start: ").append(formatDateTime(it))
            }
            task.dueAt?.let {
                if (dateInfo.isNotEmpty()) dateInfo.append("   •   ")
                dateInfo.append("Termin: ").append(formatDateTime(it))
            }
            if (dateInfo.isNotEmpty()) {
                binding.textDates.visibility = View.VISIBLE
                binding.textDates.text = dateInfo.toString()
            } else {
                binding.textDates.visibility = View.GONE
            }

            binding.checkboxDone.isChecked = task.isDone

            binding.root.setOnClickListener { onItemClick(task) }
            binding.checkboxDone.setOnCheckedChangeListener { _, _ -> onCheckChanged(task) }
        }

        private fun formatDateTime(millis: Long): String {
            return DateFormat.format("dd.MM.yyyy HH:mm", Date(millis)).toString()
        }
    }

    object DiffCallback : DiffUtil.ItemCallback<Task>() {
        override fun areItemsTheSame(oldItem: Task, newItem: Task) = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: Task, newItem: Task) = oldItem == newItem
    }
}
