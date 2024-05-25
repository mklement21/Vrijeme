package com.example.vrijeme.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.vrijeme.R
import com.example.vrijeme.classes.TodayWeatherItem

class TodayWeatherAdapter(private val items: List<TodayWeatherItem>) : RecyclerView.Adapter<TodayWeatherAdapter.TodayWeatherViewHolder>() {
    class TodayWeatherViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val time: TextView = view.findViewById(R.id.textOverlay)
        val description: TextView = view.findViewById(R.id.descriptionOverlay)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TodayWeatherViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.card_item, parent, false)
        return TodayWeatherViewHolder(view)
    }

    override fun onBindViewHolder(holder: TodayWeatherViewHolder, position: Int) {
        val item = items[position]
        holder.time.text = item.time
        holder.description.text = "${item.temperature}°C, ${item.description}"
    }

    override fun getItemCount(): Int = items.size
}