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

class TaskAdapter(private val context: Context) :
    RecyclerView.Adapter<TaskAdapter.TaskViewHolder>() {

    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("task_prefs", Context.MODE_PRIVATE)

    private val taskList = mutableListOf<Task>()  // ✅ Stores ALL tasks
    private var filteredTasks = mutableListOf<Task>()  // ✅ Stores tasks for selected date

    init {
        loadTasks()  // ✅ Load saved tasks when adapter is initialized
    }

    class TaskViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val taskCheckBox: CheckBox = itemView.findViewById(R.id.taskCheckBox)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.task_item, parent, false)
        return TaskViewHolder(view)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val task = filteredTasks[position]
        holder.taskCheckBox.text = task.description
        holder.taskCheckBox.isChecked = task.isCompleted

        holder.itemView.setOnClickListener {
            task.isCompleted = !task.isCompleted
            holder.taskCheckBox.isChecked = task.isCompleted
            saveTasks()
        }

        holder.taskCheckBox.setOnCheckedChangeListener(null)
        holder.taskCheckBox.setOnCheckedChangeListener { _, isChecked ->
            filteredTasks[position].isCompleted = isChecked
            saveTasks()
        }
    }

    override fun getItemCount(): Int = filteredTasks.size  // ✅ Show only filtered tasks

    fun addTask(task: Task) {
        taskList.add(task)  // ✅ Add to the full list
        saveTasks()
        filterTasks(task.date)  // ✅ Update filtered list for selected date
    }

    fun removeTask(position: Int) {
        val removedTask = filteredTasks[position]  // Get task from filtered list

        // Find and remove only the FIRST matching task in the full task list
        taskList.indexOfFirst { it.description == removedTask.description && it.date == removedTask.date }
            .takeIf { it != -1 }?.let { index ->
                taskList.removeAt(index)
            }

        saveTasks()
        filterTasks(removedTask.date)  // Refresh the filtered list
    }


    fun filterTasks(selectedDate: String) {
        filteredTasks = taskList.filter { it.date == selectedDate }.toMutableList()
        notifyDataSetChanged()
    }


    private fun saveTasks() {
        val editor = sharedPreferences.edit()
        val gson = Gson()
        val json = gson.toJson(taskList)
        editor.putString("tasks", json)
        editor.apply()
    }

    fun loadTasks() {
        val gson = Gson()
        val json = sharedPreferences.getString("tasks", null)
        val type = object : TypeToken<MutableList<Task>>() {}.type
        val savedTasks: MutableList<Task> = gson.fromJson(json, type) ?: mutableListOf()
        taskList.clear()
        taskList.addAll(savedTasks)
        notifyDataSetChanged()
    }

    fun getTaskAt(position: Int): Task {
        return filteredTasks[position] // Return the task at the given position
    }

    fun updateTask(position: Int, newDescription: String) {
        val oldTask = filteredTasks[position] // Get the task from filtered list
        val updatedTask = Task(newDescription, oldTask.isCompleted, oldTask.date) // Create new task instance

        // Replace the task in the main list
        val index = taskList.indexOfFirst { it.date == oldTask.date && it.description == oldTask.description }
        if (index != -1) {
            taskList[index] = updatedTask
        }

        saveTasks() // Save changes
        filterTasks(oldTask.date) // Refresh UI with updated tasks
    }

}
