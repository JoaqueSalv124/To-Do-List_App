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
        val totalDays = 30 // Adjust for actual month days

        for (day in 1..totalDays) {
            val dayButton = Button(this).apply {
                text = day.toString()
                textSize = 18f
                layoutParams = LinearLayout.LayoutParams(150, LinearLayout.LayoutParams.WRAP_CONTENT).apply {
                    setMargins(10, 0, 10, 0)
                }
                gravity = Gravity.CENTER
                setOnClickListener { view ->
                    onDayClicked(view)
                }
            }
            daysContainer.addView(dayButton)
        }
    }

    private fun onDayClicked(view: View) {
        val button = view as Button
        val day = button.text
        Toast.makeText(this, "Clicked on Day $day", Toast.LENGTH_SHORT).show()
        // Show task panel logic here
    }
}
