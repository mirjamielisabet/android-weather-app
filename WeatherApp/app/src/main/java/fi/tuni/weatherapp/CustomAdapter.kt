package fi.tuni.weatherapp

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

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

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        viewHolder.date.text = data[position].dt_txt
        viewHolder.temp.text = data[position].main.temp.toString()
        viewHolder.desc.text = data[position].weather[0].description
        viewHolder.wind.text = data[position].wind.speed.toString()
        Glide.with(context)
            .load("https://openweathermap.org/img/w/${data[position].weather[0].icon}.png")
            .into(viewHolder.icon)
    }

    override fun getItemCount() = data.size
}