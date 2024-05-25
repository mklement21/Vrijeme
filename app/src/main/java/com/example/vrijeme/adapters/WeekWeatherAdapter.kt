package com.example.vrijeme.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.vrijeme.R
import com.example.vrijeme.classes.WeekWeatherItem

class WeekWeatherAdapter(private val items: List<WeekWeatherItem>) : RecyclerView.Adapter<WeekWeatherAdapter.WeekWeatherViewHolder>() {
    class WeekWeatherViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val date: TextView = view.findViewById(R.id.textOverlay)
        val description: TextView = view.findViewById(R.id.descriptionOverlay)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WeekWeatherViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.card_item, parent, false)
        return WeekWeatherViewHolder(view)
    }

    override fun onBindViewHolder(holder: WeekWeatherViewHolder, position: Int) {
        val item = items[position]
        holder.date.text = item.date
        holder.description.text = "Max: ${item.tempMax}°C, Min: ${item.tempMin}°C, ${item.description}"
    }

    override fun getItemCount(): Int {
        return 1
    }
}