package com.to_do_listapp


import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.util.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.DividerItemDecoration



class MainActivity : AppCompatActivity() {

    private lateinit var taskAdapter: TaskAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        val daysContainer = findViewById<LinearLayout>(R.id.daysContainer)
        val monthSpinner = findViewById<Spinner>(R.id.monthSpinner)
        val yearSpinner = findViewById<Spinner>(R.id.yearSpinner)
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        val fabAddTask = findViewById<FloatingActionButton>(R.id.fabAddTask)

        val calendar = Calendar.getInstance()

        val fab = findViewById<FloatingActionButton>(R.id.fabAddTask)
        fab.visibility = View.VISIBLE // ✅ Ensure FAB is always visible


        // Populate the month spinner with custom adapter to increase text size
        val months = listOf(
            "January", "February", "March", "April", "May", "June",
            "July", "August", "September", "October", "November", "December"
        )
        val monthAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, months).apply {
            setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }
        monthSpinner.adapter = monthAdapter


        // Populate the year spinner with custom adapter to increase text size
        val years = (2020..2030).toList()
        val yearAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, years).apply {
            setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }
        yearSpinner.adapter = yearAdapter


        // Set custom text size for spinner items
        fun setSpinnerTextSize(spinner: Spinner) {
            spinner.viewTreeObserver.addOnGlobalLayoutListener {
                for (i in 0 until spinner.childCount) {
                    (spinner.getChildAt(i) as? TextView)?.textSize = 18f
                }
            }
        }
        setSpinnerTextSize(monthSpinner)
        setSpinnerTextSize(yearSpinner)


        // Set default to the current month and year
        monthSpinner.setSelection(calendar.get(Calendar.MONTH))
        yearSpinner.setSelection(calendar.get(Calendar.YEAR) - 2020)


        // Update the calendar based on the selected month and year
        monthSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parentView: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selectedMonth = position
                val selectedYear = yearSpinner.selectedItem.toString().toInt()
                updateCalendar(selectedMonth, selectedYear, daysContainer)
            }


            override fun onNothingSelected(parentView: AdapterView<*>?) {
                // Do nothing
            }
        }


        yearSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parentView: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selectedYear = yearSpinner.selectedItem.toString().toInt()
                val selectedMonth = monthSpinner.selectedItemPosition
                updateCalendar(selectedMonth, selectedYear, daysContainer)
            }


            override fun onNothingSelected(parentView: AdapterView<*>?) {
                // Do nothing
            }
        }


        // Initial calendar display
        updateCalendar(calendar.get(Calendar.MONTH), calendar.get(Calendar.YEAR), daysContainer)


        taskAdapter = TaskAdapter(mutableListOf())
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = taskAdapter

        recyclerView.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))

        fabAddTask.setOnClickListener {
            showAddTaskDialog()
        }
    }


    private fun updateCalendar(month: Int, year: Int, daysContainer: LinearLayout) {
        daysContainer.removeAllViews()


        val calendar = Calendar.getInstance()
        calendar.set(year, month, 1)


        val daysOfWeek = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
        val daysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)


        var dayCount = calendar.get(Calendar.DAY_OF_WEEK) - 2 // Adjust starting day for Monday-first
        if (dayCount < 0) dayCount = 6 // Convert Sunday (index 0) to last position


        var currentDay = 1


        for (i in 1..daysInMonth) {
            val dayName = daysOfWeek[dayCount % 7]
            val dayButton = Button(this).apply {
                text = "$dayName\n$currentDay"
                textSize = 12f  // Adjusted for better fit
                gravity = Gravity.CENTER
                layoutParams = LinearLayout.LayoutParams(
                    240,  // Adjusted width for better text fitting
                    LinearLayout.LayoutParams.MATCH_PARENT  // Full height
                ).apply {
                    setMargins(5, 5, 5, 5)
                }
                setPadding(10, 5, 10, 5) // Optimized padding
                background = ContextCompat.getDrawable(context, android.R.drawable.btn_default)
                contentDescription = "Day $currentDay"
                setOnClickListener { view ->
                    onDayClicked(view)
                }
            }


            daysContainer.addView(dayButton)
            currentDay++
            dayCount++
        }


        Log.d("Calendar", "Total Views Added: ${daysContainer.childCount}")
    }


    private fun showAddTaskDialog() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_task, null)
        val taskInput = dialogView.findViewById<EditText>(R.id.editTask)

        AlertDialog.Builder(this)
            .setTitle("Add Task")
            .setView(dialogView)
            .setPositiveButton("Add") { dialog, _ ->
                val taskText = taskInput.text.toString().trim()
                if (taskText.isNotEmpty()) {
                    val newTask = Task(taskText, false)
                    Log.d("DEBUG", "Adding task: $taskText")  // ✅ Check if task is being captured
                    taskAdapter.addTask(newTask)
                } else {
                    Toast.makeText(this, "Task cannot be empty!", Toast.LENGTH_SHORT).show()
                }
                dialog.dismiss()
            }
            .setNegativeButton("Cancel") { dialog, _ -> dialog.dismiss() }
            .show()
    }




    private fun onDayClicked(view: View) {
        val button = view as Button
        val day = button.text
        Toast.makeText(this, "Clicked on $day", Toast.LENGTH_SHORT).show()
    }
}

