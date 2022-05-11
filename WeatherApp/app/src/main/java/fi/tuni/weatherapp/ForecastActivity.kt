package fi.tuni.weatherapp

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import java.lang.Exception

class ForecastActivity : AppCompatActivity() {
    lateinit var listView : ListView
    lateinit var heading : TextView
    lateinit var key : String
    lateinit var location : String
    lateinit var adapter : ArrayAdapter<ForecastList>

    var errmsg = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forecast)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        listView = findViewById(R.id.listView)
        heading = findViewById(R.id.forecastHeading)
        key = getString(R.string.key)
        location = intent.getStringExtra("location").toString()
        heading.text = location

        adapter = ArrayAdapter<ForecastList>(this, R.layout.list_item, R.id.listItem, mutableListOf<ForecastList>())
        listView.adapter = adapter
    }

    override fun onResume() {
        super.onResume()
        getForecastData()
    }

    fun getForecastData() {
        val url = "https://api.openweathermap.org/data/2.5/forecast?q=${location}&units=metric&appid=${key}"
        UrlConnection().downloadUrlAsync(this, url) {
            if (it != null) {
                val result = parseForecastJSON(it)
                if (result != null) {
                    val list = result.list
                    for(item : ForecastList in list) {
                        adapter.add(item)
                    }
                }
            }
        }
    }

    fun parseForecastJSON(json: String): ForecastResult? {
        return try {
            val mapper = ObjectMapper().registerKotlinModule()
            mapper.readValue(json, ForecastResult::class.java)
        } catch (e: Exception) {
            errmsg = "Error When Processing the Forecast Results"
            null
        }
    }
}