package fi.tuni.weatherapp

import android.graphics.Color
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import java.lang.Exception

class ForecastActivity : AppCompatActivity() {
    lateinit var heading : TextView
    lateinit var key : String
    lateinit var location : String
    lateinit var recyclerView: RecyclerView
    lateinit var adapter : CustomAdapter
    var data = ArrayList<ForecastList>()

    var errmsg = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forecast)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        recyclerView = findViewById(R.id.recyclerView)
        heading = findViewById(R.id.forecastHeading)
        key = getString(R.string.key)
        location = intent.getStringExtra("location").toString()
        heading.text = "$location - 5 Day Forecast"

        adapter = CustomAdapter(this, data)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.addItemDecoration(DividerItemDecoration(this, LinearLayoutManager.VERTICAL))
        recyclerView.adapter = adapter
    }

    override fun onResume() {
        super.onResume()
        getForecastData()
    }

    fun getForecastData() {
        val url = "https://api.openweathermap.org/data/2.5/forecast?q=${location}&units=metric&appid=${key}"
        UrlConnection().downloadUrlAsync(this, url) { json ->
            if (json != null) {
                val result =  parseForecastJSON(json)
                if (result != null) {
                    result.list.forEachIndexed { index, element ->
                        data.add(element)
                        adapter.notifyItemInserted(index)
                    }
                } else {
                    heading.text = errmsg
                    heading.setTextColor(Color.parseColor("red"))
                }
            } else {
                errmsg = "Error when fetching the Forecast Data"
                heading.text = errmsg
                heading.setTextColor(Color.parseColor("red"))
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