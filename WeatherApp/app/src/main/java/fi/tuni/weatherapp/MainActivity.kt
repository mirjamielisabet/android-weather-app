package fi.tuni.weatherapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView

class MainActivity : AppCompatActivity() {
    lateinit var textView : TextView
    lateinit var submitButton : Button
    lateinit var locationInput : EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        textView = findViewById(R.id.textView)
        submitButton = findViewById(R.id.submitButton)
        locationInput = findViewById(R.id.locationInput)
    }

    fun onClick(button: View) {
        val inputText = locationInput.text.toString()
    }
}