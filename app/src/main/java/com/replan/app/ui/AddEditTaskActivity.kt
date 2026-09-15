package com.replan.app.ui

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.text.format.DateFormat
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.google.android.material.snackbar.Snackbar
import com.replan.app.databinding.ActivityAddEditTaskBinding
import kotlinx.coroutines.launch
import java.util.Calendar

class AddEditTaskActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddEditTaskBinding
    private lateinit var viewModel: TaskViewModel

    private var taskId: Long? = null
    private var startAtMillis: Long? = null
    private var dueAtMillis: Long? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddEditTaskBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        viewModel = ViewModelProvider(this)[TaskViewModel::class.java]

        val incomingId = intent.getLongExtra(EXTRA_TASK_ID, -1L)
        if (incomingId != -1L) {
            taskId = incomingId
            supportActionBar?.title = "Edytuj zadanie"
            loadTask(incomingId)
        } else {
            supportActionBar?.title = "Nowe zadanie"
        }

        binding.buttonPickStart.setOnClickListener { pickDateTime(isStart = true) }
        binding.buttonPickDue.setOnClickListener { pickDateTime(isStart = false) }

        binding.buttonClearStart.setOnClickListener {
            startAtMillis = null
            updateDateButtonLabels()
        }
        binding.buttonClearDue.setOnClickListener {
            dueAtMillis = null
            updateDateButtonLabels()
        }

        binding.buttonSave.setOnClickListener { saveTask() }

        updateDateButtonLabels()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }

    private fun loadTask(id: Long) {
        lifecycleScope.launch {
            val task = viewModel.getTaskById(id)
            if (task != null) {
                binding.editTitle.setText(task.title)
                binding.editDescription.setText(task.description)
                startAtMillis = task.startAt
                dueAtMillis = task.dueAt
                updateDateButtonLabels()
            }
        }
    }

    private fun pickDateTime(isStart: Boolean) {
        val calendar = Calendar.getInstance()
        val existing = if (isStart) startAtMillis else dueAtMillis
        if (existing != null) calendar.timeInMillis = existing

        DatePickerDialog(
            this,
            { _, year, month, dayOfMonth ->
                calendar.set(Calendar.YEAR, year)
                calendar.set(Calendar.MONTH, month)
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)

                TimePickerDialog(
                    this,
                    { _, hourOfDay, minute ->
                        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
                        calendar.set(Calendar.MINUTE, minute)
                        calendar.set(Calendar.SECOND, 0)
                        calendar.set(Calendar.MILLISECOND, 0)

                        if (isStart) {
                            startAtMillis = calendar.timeInMillis
                        } else {
                            dueAtMillis = calendar.timeInMillis
                        }
                        updateDateButtonLabels()
                    },
                    calendar.get(Calendar.HOUR_OF_DAY),
                    calendar.get(Calendar.MINUTE),
                    true
                ).show()
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    private fun updateDateButtonLabels() {
        binding.buttonPickStart.text = startAtMillis?.let { formatDateTime(it) } ?: "Wybierz start"
        binding.buttonPickDue.text = dueAtMillis?.let { formatDateTime(it) } ?: "Wybierz termin"

        binding.buttonClearStart.visibility = if (startAtMillis != null) View.VISIBLE else View.GONE
        binding.buttonClearDue.visibility = if (dueAtMillis != null) View.VISIBLE else View.GONE
    }

    private fun formatDateTime(millis: Long): String {
        return DateFormat.format("dd.MM.yyyy HH:mm", millis).toString()
    }

    private fun saveTask() {
        val title = binding.editTitle.text?.toString()?.trim().orEmpty()
        if (title.isEmpty()) {
            binding.inputLayoutTitle.error = "Podaj tytuł zadania"
            return
        }
        binding.inputLayoutTitle.error = null

        val start = startAtMillis
        val due = dueAtMillis
        if (start != null && due != null && start > due) {
            Snackbar.make(binding.root, "Start nie może być późniejszy niż termin", Snackbar.LENGTH_LONG).show()
            return
        }

        val description = binding.editDescription.text?.toString()?.trim().orEmpty()

        viewModel.saveTask(taskId, title, description, start, due)
        finish()
    }

    companion object {
        const val EXTRA_TASK_ID = "extra_task_id"
    }
}
