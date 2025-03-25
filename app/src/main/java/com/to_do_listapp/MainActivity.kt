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

        // Populate the month spinner
        val months = listOf(
            "January", "February", "March", "April", "May", "June",
            "July", "August", "September", "October", "November", "December"
        )
        val monthAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, months).apply {
            setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }
        monthSpinner.adapter = monthAdapter

        // Populate the year spinner
        val years = (2020..2030).toList()
        val yearAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, years).apply {
            setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }
        yearSpinner.adapter = yearAdapter

        // Set default month and year
        monthSpinner.setSelection(calendar.get(Calendar.MONTH))
        yearSpinner.setSelection(calendar.get(Calendar.YEAR) - 2020)

        // Update calendar when month or year is selected
        monthSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parentView: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selectedYear = yearSpinner.selectedItem.toString().toInt()
                updateCalendar(position, selectedYear, daysContainer)
            }

            override fun onNothingSelected(parentView: AdapterView<*>?) {}
        }

        yearSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parentView: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selectedMonth = monthSpinner.selectedItemPosition
                updateCalendar(selectedMonth, yearSpinner.selectedItem.toString().toInt(), daysContainer)
            }

            override fun onNothingSelected(parentView: AdapterView<*>?) {}
        }

        // Initial calendar display
        updateCalendar(calendar.get(Calendar.MONTH), calendar.get(Calendar.YEAR), daysContainer)
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
                textSize = 14f  // Reduced text size to fit text properly
                gravity = Gravity.CENTER
                layoutParams = LinearLayout.LayoutParams(
                    120,  // Reduced button width slightly
                    LinearLayout.LayoutParams.MATCH_PARENT
                ).apply {
                    setMargins(5, 5, 5, 5)
                }
                setPadding(10, 10, 10, 10)
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

    private fun onDayClicked(view: View) {
        val button = view as Button
        val day = button.text
        Toast.makeText(this, "Clicked on $day", Toast.LENGTH_SHORT).show()
    }
}

