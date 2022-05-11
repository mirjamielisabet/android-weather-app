package fi.tuni.weatherapp

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Geocoder
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.core.app.ActivityCompat
import com.bumptech.glide.Glide
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import java.lang.Exception

class MainActivity : AppCompatActivity() {
    lateinit var heading : TextView
    lateinit var heading2 : TextView
    lateinit var description : TextView
    lateinit var temp : TextView
    lateinit var feels_like_temp : TextView
    lateinit var wind : TextView
    lateinit var humidity : TextView
    lateinit var submitButton : Button
    lateinit var locationInput : EditText
    lateinit var weatherIcon : ImageView
    lateinit var fusedLocationClient: FusedLocationProviderClient
    lateinit var key : String

    var location = "Helsinki"
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
        feels_like_temp = findViewById(R.id.feels_like_temp)
        wind = findViewById(R.id.wind)
        humidity = findViewById(R.id.humidity)
        weatherIcon = findViewById(R.id.weatherIcon)

        heading.text = "Current Weather | $location"
        heading2.text = "Search Weather by Location"

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
    }

    fun getWeatherData() {
        val url = "https://api.openweathermap.org/data/2.5/weather?q=${location}&units=metric&appid=${key}"
        UrlConnection().downloadUrlAsync(this, url) {
            if (it != null) {
                val result = parseWeatherJSON(it)
                if (result != null) {
                    locationInput.text = null
                    locationInput.clearFocus()

                    temp.text = "${result.main.temp.toString()} °C"
                    feels_like_temp.text = "Feels like: ${result.main.feels_like} °C"
                    wind.text = "Wind: ${result.wind.speed.toString()} m/s"
                    humidity.text = "Humidity: ${result.main.humidity} %"
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
                errmsg = "Entered Location Not Found"
                locationInput.error = errmsg
            }
        }
    }

    override fun onResume() {
        super.onResume()
        getWeatherData()
    }

    fun onClick(button: View) {
        val intent = Intent(this, ForecastActivity::class.java)
        intent.putExtra("location", location)

        when (button.id) {
            R.id.submitButton -> {
                val inputText = locationInput.text.toString()
                location = inputText
                getWeatherData()
            }
            R.id.forecastButton -> startActivity(intent)
            R.id.currentLocationButton -> getCurrentLocation()
        }
    }

    fun askPermissions() {
        ActivityCompat.requestPermissions(this,
            arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION), 42)
    }

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            42 -> {
                if ((grantResults.isNotEmpty() &&
                            grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    getCurrentLocation()
                } else {
                    Toast.makeText(this,
                        "Access to Location denied, cannot show Weather Info based on your location.",
                        Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    fun checkPermission() : Boolean {
        return ActivityCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    @SuppressLint("MissingPermission")
    fun getCurrentLocation() {
        if (checkPermission()) {
            fusedLocationClient.lastLocation
                .addOnSuccessListener { currentLocation : Location? ->
                    if (currentLocation != null) {
                        location = getCity(currentLocation.latitude, currentLocation.longitude)
                        getWeatherData()
                    }
                }
        } else {
            askPermissions()
        }
    }

    fun getCity(lat: Double, lon: Double) : String {
        val geocoder = Geocoder(this)
        val list = geocoder.getFromLocation(lat, lon, 1)
        return list[0].locality
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