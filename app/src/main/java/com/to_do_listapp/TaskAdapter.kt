package com.to_do_listapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import androidx.recyclerview.widget.RecyclerView

class TaskAdapter(private val taskList: MutableList<Task>) :
    RecyclerView.Adapter<TaskAdapter.TaskViewHolder>() {

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

        // When clicking anywhere in the item, toggle the checkbox
        holder.itemView.setOnClickListener {
            task.isCompleted = !task.isCompleted
            holder.taskCheckBox.isChecked = task.isCompleted
        }

        // Prevent multiple event triggers by setting listener null before resetting it
        holder.taskCheckBox.setOnCheckedChangeListener(null)
        holder.taskCheckBox.setOnCheckedChangeListener { _, isChecked ->
            taskList[position].isCompleted = isChecked
        }
    }



    override fun getItemCount(): Int = taskList.size

    fun addTask(task: Task) {
        taskList.add(task)
        notifyItemInserted(taskList.size - 1) // ✅ Forces RecyclerView to refresh
    }
}
