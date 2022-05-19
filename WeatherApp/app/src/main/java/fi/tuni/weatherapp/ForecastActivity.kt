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

/**
 * Activity that presents the forecast results to the user.
 */
class ForecastActivity : AppCompatActivity() {
    lateinit var heading : TextView
    lateinit var key : String
    lateinit var location : String
    lateinit var recyclerView: RecyclerView
    lateinit var adapter : CustomAdapter
    private var data = ArrayList<ForecastList>()
    private var errmsg = ""

    /**
     * When the activity is starting, initializes the variables, sets the heading text and gets
     * the location from the Intent. Adds a divider between the recycler view's list items.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forecast)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        recyclerView = findViewById(R.id.recyclerView)
        heading = findViewById(R.id.forecastHeading)
        key = getString(R.string.key)
        location = intent.getStringExtra("location").toString()
        heading.text = getString(R.string.forecastHeading, location)

        adapter = CustomAdapter(this, data)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.addItemDecoration(DividerItemDecoration(this, LinearLayoutManager.VERTICAL))
        recyclerView.adapter = adapter
    }

    /**
     * When the activity becomes active, calls the getForecastData function that fetches the
     * forecast results.
     */
    override fun onResume() {
        super.onResume()
        getForecastData()
    }

    /**
     * Uses the downloadUrlAsync function from the UrlConnection class for fetching the forecast
     * results from the OpenWeather API. If the results are fetched successfully, the forecast
     * list item is added to the recycler view by using the CustomAdapter. If the results are not
     * fetched successfully, the error message is shown to the user.
     */
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

    /**
     * By using Jackson's ObjectMapper, parses the forecast json into the ForecastResult
     * object. Returns the formed object or null if exception is caught.
     *
     * @param json the forecast result json to be parsed.
     * @return the ForecastResult object that contains the forecast or null if error occurs.
     */
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
