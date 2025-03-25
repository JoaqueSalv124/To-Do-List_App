package com.to_do_listapp

import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val daysContainer = findViewById<LinearLayout>(R.id.daysContainer)

        val daysOfWeek = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
        val totalDays = 30

        for (day in 1..totalDays) {
            val dayLayout = LinearLayout(this).apply {
                orientation = LinearLayout.VERTICAL
                gravity = Gravity.CENTER
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.MATCH_PARENT
                ).apply {
                    setMargins(15, 0, 15, 0)
                }
            }

            val dayButton = Button(this).apply {
                val dayName = daysOfWeek[(day - 1) % 7]
                text = "$dayName\n$day"  // Combining day name and day number
                textSize = 24f  // Adjust text size to fit
                gravity = Gravity.CENTER
                setPadding(30, 30, 30, 30)  // Padding to ensure text is not touching the edges
                background = resources.getDrawable(android.R.drawable.btn_default) // Adding background color
                layoutParams = LinearLayout.LayoutParams(250, 250).apply {
                    setMargins(10, 10, 10, 10) // Adjust margin to separate buttons
                }
                contentDescription = "Day $day"
                setOnClickListener { view ->
                    onDayClicked(view)
                }
            }

            dayLayout.addView(dayButton)
            daysContainer.addView(dayLayout)
        }
    }

    private fun onDayClicked(view: View) {
        val button = view as Button
        val day = button.text
        Toast.makeText(this, "Clicked on $day", Toast.LENGTH_SHORT).show()
    }
}
