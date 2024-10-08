package com.example.vrijeme.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.vrijeme.R
import com.example.vrijeme.classes.WeekWeatherItem

class WeekWeatherAdapter(private val items: List<WeekWeatherItem>) : RecyclerView.Adapter<WeekWeatherAdapter.WeekWeatherViewHolder>() {

    private var onItemClickListener: ((Int) -> Unit)? = null

    fun setOnItemClickListener(listener: (Int) -> Unit) {
        onItemClickListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WeekWeatherViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.week_card_item, parent, false)
        return WeekWeatherViewHolder(view)
    }

    override fun onBindViewHolder(holder: WeekWeatherViewHolder, position: Int) {
        val item = items[position]
        holder.date.text = item.date
        holder.description.text = "Max: ${item.tempMax}°C\nMin: ${item.tempMin}°C\n${item.description}"
        holder.itemView.setOnClickListener {
            onItemClickListener?.invoke(position)
        }
    }

    override fun getItemCount(): Int = items.size

    class WeekWeatherViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val date: TextView = view.findViewById(R.id.textOverlay)
        val description: TextView = view.findViewById(R.id.descriptionOverlay)
    }
}