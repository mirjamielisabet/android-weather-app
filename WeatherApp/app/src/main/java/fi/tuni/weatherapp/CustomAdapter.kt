package fi.tuni.weatherapp

import android.content.Context
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlin.collections.ArrayList


class CustomAdapter(private var context: Context, private var data:  ArrayList<ForecastList>)
    : RecyclerView.Adapter<CustomAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val date: TextView = view.findViewById(R.id.date)
        val temp: TextView = view.findViewById(R.id.forecastTemp)
        val desc: TextView = view.findViewById(R.id.forecastDescription)
        val icon: ImageView = view.findViewById(R.id.forecastIcon)
        val wind: TextView = view.findViewById(R.id.forecastWind)
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.list_item, viewGroup, false)
        return ViewHolder(view)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        val formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")
        val currentFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
        val date = LocalDateTime.parse(data[position].dt_txt, currentFormat)
        val formattedDate = formatter.format(date)

        viewHolder.date.text = formattedDate
        viewHolder.temp.text = context.getString(R.string.temp, data[position].main.temp)
        viewHolder.desc.text = data[position].weather[0].description
        viewHolder.wind.text = context.getString(R.string.wind, data[position].wind.speed)
        Glide.with(context)
            .load("https://openweathermap.org/img/w/${data[position].weather[0].icon}.png")
            .into(viewHolder.icon)
    }

    override fun getItemCount() = data.size
}