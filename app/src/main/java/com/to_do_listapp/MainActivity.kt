package com.to_do_listapp

import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import java.util.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val daysContainer = findViewById<LinearLayout>(R.id.daysContainer)
        val monthSpinner = findViewById<Spinner>(R.id.monthSpinner)
        val yearSpinner = findViewById<Spinner>(R.id.yearSpinner)

        val calendar = Calendar.getInstance()

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
    }

    private fun updateCalendar(month: Int, year: Int, daysContainer: LinearLayout) {
        daysContainer.removeAllViews() // Clear previous days

        val calendar = Calendar.getInstance()
        calendar.set(year, month, 1)

        val startDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)
        val daysOfWeek = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
        val adjustedStartDay = if (startDayOfWeek == Calendar.SUNDAY) 6 else startDayOfWeek - 2
        val daysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)

        val dayRow = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
        }

        var dayCount = 0
        var currentDay = 1

        for (i in 1..daysInMonth) {
            if (i == 1) {
                for (j in 0 until adjustedStartDay) {
                    val emptyButton = Button(this).apply {
                        text = " "
                        setBackgroundColor(ContextCompat.getColor(context, android.R.color.transparent))
                        layoutParams = LinearLayout.LayoutParams(0, 250, 1f)
                    }
                    dayRow.addView(emptyButton)
                    dayCount++
                }
            }

            val dayName = daysOfWeek[(dayCount) % 7]
            val dayButton = Button(this).apply {
                text = "$dayName\n$currentDay"
                textSize = 18f
                gravity = Gravity.CENTER
                layoutParams = LinearLayout.LayoutParams(250, 250).apply {
                    setMargins(10, 10, 10, 10)
                }
                setPadding(30, 30, 30, 30)
                background = ContextCompat.getDrawable(context, android.R.drawable.btn_default)
                contentDescription = "Day $currentDay"
                setOnClickListener { view ->
                    onDayClicked(view)
                }
            }

            dayRow.addView(dayButton)
            currentDay++
            dayCount++

            if (dayCount == 7) {
                dayCount = 0
            }
        }

        daysContainer.addView(dayRow)

        Log.d("Calendar", "Total Views Added: ${dayRow.childCount}")
    }

    private fun onDayClicked(view: View) {
        val button = view as Button
        val day = button.text
        Toast.makeText(this, "Clicked on $day", Toast.LENGTH_SHORT).show()
    }
}
