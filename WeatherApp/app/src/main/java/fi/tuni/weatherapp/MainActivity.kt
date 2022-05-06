package fi.tuni.weatherapp

import android.app.Activity
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import java.io.BufferedReader
import java.io.InputStreamReader
import java.lang.Exception
import java.net.HttpURLConnection
import java.net.URL
import kotlin.concurrent.thread

class MainActivity : AppCompatActivity() {
    lateinit var heading : TextView
    lateinit var heading2 : TextView
    lateinit var description : TextView
    lateinit var temp : TextView
    lateinit var wind : TextView
    lateinit var submitButton : Button
    lateinit var locationInput : EditText
    lateinit var weatherIcon : ImageView

    lateinit var key : String
    lateinit var url : String

    var location = "Tampere"
    var errmsg = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        key = getString(R.string.key)
        heading = findViewById(R.id.heading)
        heading2 = findViewById(R.id.heading2)
        submitButton = findViewById(R.id.submitButton)
        locationInput = findViewById(R.id.locationInput)
        description = findViewById(R.id.description)
        temp = findViewById(R.id.temp)
        wind = findViewById(R.id.wind)
        weatherIcon = findViewById(R.id.weatherIcon)

        heading.text = "Current Weather | $location"
        heading2.text = "Search Weather by Location"
    }

    fun getWeatherData() {
        url = "https://api.openweathermap.org/data/2.5/weather?q=${location}&units=metric&appid=${key}"
        downloadUrlAsync(this, url) {
            if (it != null) {
                val result = parseWeatherJSON(it)
                if (result != null) {
                    locationInput.text = null
                    locationInput.clearFocus()

                    temp.text = "${result.main.temp.toString()} °C"
                    wind.text = "${result.wind.speed.toString()} m/s"
                    description.text = result.weather[0].description
                    heading.text = "Current Weather | ${result.name}"
                    Glide.with(this)
                        .load("https://openweathermap.org/img/w/${result.weather[0].icon}.png")
                        .into(weatherIcon)
                } else {
                    heading.text = errmsg
                    heading.setTextColor(Color.parseColor("red"))
                }
            } else {
                locationInput.error = errmsg
            }
        }
    }

    override fun onResume() {
        super.onResume()
        getWeatherData()
    }

    fun onClick(button: View) {
        val inputText = locationInput.text.toString()
        location = inputText
        getWeatherData()
    }

    fun downloadUrlAsync(context: Activity, url: String, callback: (result: String?) -> Unit) {
        thread() {
            val json = getUrl(url)
            context.runOnUiThread() {
                callback(json)
            }
        }
    }

    fun getUrl(url: String) : String? {
        var result : String? = ""

        try {
            val myUrl = URL(url)
            val conn = myUrl.openConnection() as HttpURLConnection
            val reader = BufferedReader(InputStreamReader(conn.inputStream))

            reader.use {
                var line : String? = ""

                while (line != null) {
                    line = it.readLine()
                    result += line
                }
            }
        } catch (e: Exception) {
            println(e)
            result = null
            errmsg = "Entered Location Not Found"
        }
        return result
    }

    fun parseWeatherJSON(json: String): WeatherResult? {
        return try {
            val mapper = ObjectMapper().registerKotlinModule()
            mapper.readValue(json, WeatherResult::class.java)
        } catch (e: Exception) {
            errmsg = "Error When Processing the Weather Results"
            null
        }
    }
}