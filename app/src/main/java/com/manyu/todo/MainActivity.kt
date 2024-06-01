package com.manyu.todo

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    private lateinit var taskListView: ListView
    private lateinit var taskList: MutableList<String>
    private lateinit var adapter: ArrayAdapter<String>
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        sharedPreferences = getSharedPreferences("com.manyu.todolist", Context.MODE_PRIVATE)
        taskListView = findViewById(R.id.task_list_view)
        taskList = retrieveTasks()
        adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, taskList)
        taskListView.adapter = adapter

        taskListView.onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
            val task = taskList[position]
            showEditDeleteDialog(task, position)
        }
    }

    fun addTask(view: View) {
        val taskEditText = findViewById<EditText>(R.id.task_edit_text)
        val task = taskEditText.text.toString()
        if (task.isNotEmpty()) {
            taskList.add(0,task)
            taskEditText.text.clear()
            adapter.notifyDataSetChanged()
            storeTasks()
        }
    }

    private fun showEditDeleteDialog(task: String, position: Int) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Edit/Delete Task")
        builder.setMessage("Would you like to edit or delete this task?")

        builder.setPositiveButton("Edit") { _, _ ->
            showEditDialog(task, position)
        }

        builder.setNegativeButton("Delete") { _, _ ->
            taskList.removeAt(position)
            adapter.notifyDataSetChanged()
            storeTasks()
        }

        builder.setNeutralButton("Cancel", null)
        builder.show()
    }

    private fun showEditDialog(task: String, position: Int) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Edit Task")

        val input = EditText(this)
        input.setText(task)
        builder.setView(input)

        builder.setPositiveButton("OK") { _, _ ->
            taskList[position] = input.text.toString()
            adapter.notifyDataSetChanged()
            storeTasks()
        }

        builder.setNegativeButton("Cancel", null)
        builder.show()
    }

    private fun retrieveTasks(): MutableList<String> {
        val tasks = sharedPreferences.getStringSet("tasks", emptySet())
        return tasks?.toMutableList() ?: mutableListOf()
    }

    private fun storeTasks() {
        with (sharedPreferences.edit()) {
            putStringSet("tasks", taskList.toSet())
            apply()
        }
    }
}



