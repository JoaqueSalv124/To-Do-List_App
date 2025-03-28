package com.to_do_listapp

import androidx.recyclerview.widget.ItemTouchHelper
import android.graphics.*
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
import java.text.SimpleDateFormat
import android.graphics.drawable.Drawable
import java.util.*


private var selectedDate: String = ""  // Stores the currently selected date

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


        val itemTouchHelper = ItemTouchHelper(object :
            ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false // No move action
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                if (direction == ItemTouchHelper.LEFT) {
                    taskAdapter.removeTask(position) // Delete task
                } else if (direction == ItemTouchHelper.RIGHT) {
                    val task = taskAdapter.getTaskAt(position) // Retrieve task
                    showEditTaskDialog(task, position) // Open edit dialog
                }
                taskAdapter.notifyItemChanged(position) // Reset background after swipe action
            }

            override fun onChildDraw(
                c: Canvas, recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder,
                dX: Float, dY: Float, actionState: Int, isCurrentlyActive: Boolean
            ) {
                val itemView = viewHolder.itemView
                val paint = Paint()
                val icon: Drawable?

                if (dX > 100) { // Only show blue when swiping right far enough
                    paint.color = Color.BLUE
                    icon = ContextCompat.getDrawable(this@MainActivity, R.drawable.baseline_edit_24)
                } else if (dX < -100) { // Only show red when swiping left far enough
                    paint.color = Color.RED
                    icon =
                        ContextCompat.getDrawable(this@MainActivity, R.drawable.baseline_delete_24)
                } else {
                    paint.color = Color.TRANSPARENT // Reset color when swipe is incomplete
                    icon = null
                }

                c.drawRect(
                    itemView.left.toFloat(), itemView.top.toFloat(),
                    itemView.right.toFloat(), itemView.bottom.toFloat(), paint
                )

                icon?.let {
                    val iconMargin = (itemView.height - it.intrinsicHeight) / 2
                    val iconTop = itemView.top + (itemView.height - it.intrinsicHeight) / 2
                    val iconLeft =
                        if (dX > 0) itemView.left + iconMargin else itemView.right - iconMargin - it.intrinsicWidth
                    val iconRight = iconLeft + it.intrinsicWidth
                    val iconBottom = iconTop + it.intrinsicHeight

                    it.setBounds(iconLeft, iconTop, iconRight, iconBottom)
                    it.draw(c)
                }

                super.onChildDraw(
                    c,
                    recyclerView,
                    viewHolder,
                    dX,
                    dY,
                    actionState,
                    isCurrentlyActive
                )
            }
        })
        itemTouchHelper.attachToRecyclerView(recyclerView)

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
            override fun onItemSelected(
                parentView: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                val selectedMonth = position
                val selectedYear = yearSpinner.selectedItem.toString().toInt()
                updateCalendar(selectedMonth, selectedYear, daysContainer)
            }


            override fun onNothingSelected(parentView: AdapterView<*>?) {
                // Do nothing
            }
        }


        yearSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parentView: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
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


        taskAdapter = TaskAdapter(this)  // ✅ Correct, since TaskAdapter handles its own list
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
        val daysOfWeek = listOf("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat")
        val daysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
        val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        val todayTextView = findViewById<TextView>(R.id.Today)

        if (selectedDate.isEmpty()) {
            selectedDate = today // Automatically select today's date on app launch
        }

        val scrollView = findViewById<HorizontalScrollView>(R.id.daysScrollView)
        var todayButton: Button? = null
        var selectedButton: Button? = null

        var dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK) - 1

        for (day in 1..daysInMonth) {
            val formattedDate = String.format("%04d-%02d-%02d", year, month + 1, day)
            val dayName = daysOfWeek[dayOfWeek % 7]  // Get day of the week

            val dayButton = Button(this).apply {
                text = "$dayName\n$day"
                textSize = 12f
                gravity = Gravity.CENTER
                layoutParams = LinearLayout.LayoutParams(240, 240).apply { // ✅ Fix button height
                    setMargins(5, 5, 5, 5)
                }
                background = ContextCompat.getDrawable(context,
                    if (formattedDate == selectedDate) R.drawable.selected_date_border
                    else R.drawable.calendar_button_bg
                )
                setOnClickListener {
                    selectedDate = formattedDate
                    updateCalendar(month, year, daysContainer) // Refresh calendar
                    taskAdapter.filterTasks(selectedDate) // Show tasks
                    updateTodayTextView(todayTextView, formattedDate, today)
                }
            }

            if (formattedDate == today) todayButton = dayButton
            if (formattedDate == selectedDate) selectedButton = dayButton // ✅ Store selected date button

            daysContainer.addView(dayButton)
            dayOfWeek++
        }

        // ✅ Now scroll to either today or the selected date correctly
        selectedButton?.let {
            scrollView.post { scrollView.smoothScrollTo(it.left, 0) }
        }

        updateTodayTextView(todayTextView, selectedDate, today)

        if (::taskAdapter.isInitialized) {
            taskAdapter.filterTasks(selectedDate) // Ensure taskAdapter is initialized before use
        }
    }



    private fun updateTodayTextView(todayTextView: TextView, selectedDate: String, today: String) {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val newFormat = SimpleDateFormat("MMMM d, yyyy", Locale.getDefault())
        val dateObject = dateFormat.parse(selectedDate)

        todayTextView.text = if (selectedDate == today) "Today" else newFormat.format(dateObject!!)
    }

    private fun showAddTaskDialog() {
        if (selectedDate.isEmpty()) {
            Toast.makeText(this, "Please select a date first!", Toast.LENGTH_SHORT).show()
            return
        }

        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_task, null)
        val taskInput = dialogView.findViewById<EditText>(R.id.editTask)

        AlertDialog.Builder(this)
            .setTitle("Add Task")
            .setView(dialogView)
            .setPositiveButton("Add") { dialog, _ ->
                val taskText = taskInput.text.toString().trim()
                if (taskText.isNotEmpty()) {
                    val newTask =
                        Task(taskText, false, selectedDate)  // ✅ Assign task to selected date
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

    private fun showEditTaskDialog(task: Task, position: Int) {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_task, null)
        val taskInput = dialogView.findViewById<EditText>(R.id.editTask)
        taskInput.setText(task.description) // Set current task description

        AlertDialog.Builder(this)
            .setTitle("Edit Task")
            .setView(dialogView)
            .setPositiveButton("Save") { dialog, _ ->
                val updatedText = taskInput.text.toString().trim()
                if (updatedText.isNotEmpty()) {
                    taskAdapter.updateTask(position, updatedText) // Update task in adapter
                } else {
                    Toast.makeText(this, "Task cannot be empty!", Toast.LENGTH_SHORT).show()
                }
                dialog.dismiss()
            }
            .setNegativeButton("Cancel") { dialog, _ -> dialog.dismiss() }
            .show()
    }
}


