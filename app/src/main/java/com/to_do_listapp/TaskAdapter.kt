package com.to_do_listapp

import android.content.Context
import android.content.SharedPreferences
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class TaskAdapter(private val context: Context, private val taskList: MutableList<Task>) :
    RecyclerView.Adapter<TaskAdapter.TaskViewHolder>() {

    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("task_prefs", Context.MODE_PRIVATE)

    init {
        loadTasks() // Load tasks when adapter is created
    }

    class TaskViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val taskCheckBox: CheckBox = itemView.findViewById(R.id.taskCheckBox)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.task_item, parent, false)
        return TaskViewHolder(view)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val task = taskList[position]
        holder.taskCheckBox.text = task.description
        holder.taskCheckBox.isChecked = task.isCompleted

        holder.itemView.setOnClickListener {
            task.isCompleted = !task.isCompleted
            holder.taskCheckBox.isChecked = task.isCompleted
            saveTasks() // Save updated list
        }

        holder.taskCheckBox.setOnCheckedChangeListener(null)
        holder.taskCheckBox.setOnCheckedChangeListener { _, isChecked ->
            taskList[position].isCompleted = isChecked
            saveTasks() // Save updated list
        }
    }

    override fun getItemCount(): Int = taskList.size

    fun addTask(task: Task) {
        taskList.add(task)
        notifyItemInserted(taskList.size - 1)
        saveTasks() // Save when a new task is added
    }

    fun removeTask(position: Int) {
        taskList.removeAt(position)
        notifyItemRemoved(position)
        saveTasks() // Save when a task is removed
    }

    private fun saveTasks() {
        val editor = sharedPreferences.edit()
        val gson = Gson()
        val json = gson.toJson(taskList) // Convert task list to JSON
        editor.putString("tasks", json)
        editor.apply()
    }

    private fun loadTasks() {
        val gson = Gson()
        val json = sharedPreferences.getString("tasks", null)
        val type = object : TypeToken<MutableList<Task>>() {}.type
        val savedTasks: MutableList<Task> = gson.fromJson(json, type) ?: mutableListOf()
        taskList.clear()
        taskList.addAll(savedTasks)
        notifyDataSetChanged()
    }
}

