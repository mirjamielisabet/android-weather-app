package fi.tuni.weatherapp

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.SharedPreferences
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

/**
 * The Main Activity.
 * Consists of elements that present the current weather to the user. The weather results are
 * based on either the user's current location or the location user has inputted to the search field.
 *
 * @author Mirjami Laiho
 */
class MainActivity : AppCompatActivity() {
    lateinit var heading : TextView
    lateinit var heading2 : TextView
    lateinit var locationText : TextView
    lateinit var description : TextView
    lateinit var temp : TextView
    lateinit var feelsLikeTemp : TextView
    lateinit var wind : TextView
    lateinit var humidity : TextView
    lateinit var submitButton : Button
    lateinit var locationInput : EditText
    lateinit var weatherIcon : ImageView
    lateinit var fusedLocationClient: FusedLocationProviderClient
    lateinit var key : String
    lateinit var sharedPref : SharedPreferences
    lateinit var searchedLocation : String

    private var errmsg = ""

    /**
     * When the activity is starting, initializes the variables and sets the
     * texts for the headings.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        key = getString(R.string.key)
        heading = findViewById(R.id.heading)
        heading2 = findViewById(R.id.heading2)
        locationText = findViewById(R.id.locationText)
        submitButton = findViewById(R.id.submitButton)
        locationInput = findViewById(R.id.locationInput)
        description = findViewById(R.id.description)
        temp = findViewById(R.id.temp)
        feelsLikeTemp = findViewById(R.id.feels_like_temp)
        wind = findViewById(R.id.wind)
        humidity = findViewById(R.id.humidity)
        weatherIcon = findViewById(R.id.weatherIcon)

        heading.text = getString(R.string.currentWeather)
        heading2.text = getString(R.string.searchLocation)
        searchedLocation = getString(R.string.defaultCity)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        sharedPref = getSharedPreferences("savedLocation", MODE_PRIVATE)
    }

    /**
     * Uses the downloadUrlAsync function from the UrlConnection class for fetching the weather
     * results from the OpenWeather API. If the weather results are fetched successfully, the
     * UI is updated according to the fetched data and the location is saved to the sharedPreferences.
     * If the weather results are not fetched successfully, the error message is shown to the user.
     */
    fun getWeatherData() {
        val url = "https://api.openweathermap.org/data/2.5/weather?q=${searchedLocation}&units=metric&appid=${key}"
        UrlConnection().downloadUrlAsync(this, url) {
            if (it != null) {
                val result = parseWeatherJSON(it)
                if (result != null) {
                    locationInput.text = null
                    locationInput.clearFocus()

                    temp.text = getString(R.string.temp, result.main.temp)
                    feelsLikeTemp.text = getString(R.string.feels_like_temp, result.main.feels_like)
                    wind.text = getString(R.string.wind, result.wind.speed)
                    humidity.text = getString(R.string.humidity, result.main.humidity)
                    description.text = result.weather[0].description
                    locationText.text = result.name
                    val edit = sharedPref.edit()
                    edit.putString("location", result.name)
                    edit.apply()
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

    /**
     * When the activity becomes active, the sharedPreferences is checked for a
     * saved location name. If a location was retrieved, it will be saved to the savedLocation
     * variable and the weather results are fetched based on it. Otherwise the default location
     * will be used.
     */
    override fun onResume() {
        super.onResume()
        val savedLocation = sharedPref.getString("location", "")
        if (savedLocation != "" && savedLocation != null) {
            searchedLocation = savedLocation
        }
        getWeatherData()
    }

    /**
     * When a button is clicked, based on the buttons id:
     *   submitButton: saves the user's input to the searchedLocation variable
     *          and calls the getWeatherData function that finds the weather results for the
     *          inputted location.
     *   forecastButton: directs the user to the other activity that shows the forecast results.
     *          The location is passed to the activity in the Intent.
     *   currentLocationButton: calls the getCurrentLocation function that retrieves the user's
     *          current location.
     *
     * @param button the button that was clicked.
     */
    fun onClick(button: View) {
        val intent = Intent(this, ForecastActivity::class.java)
        intent.putExtra("location", locationText.text)

        when (button.id) {
            R.id.submitButton -> {
                val inputText = locationInput.text.toString()
                searchedLocation = inputText
                getWeatherData()
            }
            R.id.forecastButton -> startActivity(intent)
            R.id.currentLocationButton -> getCurrentLocation()
        }
    }

    /**
     * Asks the user for permission to access coarse location.
     */
    fun askPermissions() {
        ActivityCompat.requestPermissions(this,
            arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION), 42)
    }

    /**
     * When the user has answered to the permission request, based on the answer:
     * either calls the getCurrentLocation function for finding the user's current location
     * or informs the user of the effects of the denied permission on the use of the application.
     *
     * @param requestCode the code of the request, passed in requestPermissions.
     * @param permissions the array of the permissions requested.
     * @param grantResults the array of the grant results, either PERMISSION_DENIED or PERMISSION_GRANTED.
     */
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

    /**
     * Checks if the user has already given permission for accessing the coarse location.
     *
     * @return true or false, according to the result of the permission check.
     */
    fun checkPermission() : Boolean {
        return ActivityCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    /**
     * If the needed permissions are granted, finds the user's current location.
     * By using the latitude and the longitude of the found location, gets the
     * locality of the coordinates and saves it to the searchedLocation variable to be
     * used for searching the weather results.
     * If the needed permissions are not granted, calls the askPermissions function.
     */
    @SuppressLint("MissingPermission")
    fun getCurrentLocation() {
        if (checkPermission()) {
            fusedLocationClient.lastLocation
                .addOnSuccessListener { currentLocation : Location? ->
                    if (currentLocation != null) {
                        val city = getCity(currentLocation.latitude, currentLocation.longitude)
                        searchedLocation = city
                        getWeatherData()
                    }
                }
        } else {
            askPermissions()
        }
    }

    /**
     * By using Geocoder, finds and returns the locality of the address based on the given
     * latitude and longitude.
     *
     * @param lat the latitude of the searched location.
     * @param lon the longitude of the searched location.
     * @return the name of the city/locality.
     */
    fun getCity(lat: Double, lon: Double) : String {
        val geocoder = Geocoder(this)
        val list = geocoder.getFromLocation(lat, lon, 1)
        return list[0].locality
    }

    /**
     * By using Jackson's ObjectMapper, parses the weather result json into the WeatherResult
     * object. Returns the formed object or null if exception is caught.
     *
     * @param json the weather result json to be parsed.
     * @return the WeatherResult object that contains the info about the current weather
     * or null if error occurs.
     */
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
