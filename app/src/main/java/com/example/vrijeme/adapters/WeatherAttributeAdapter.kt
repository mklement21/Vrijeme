package com.example.vrijeme.adapters

import com.example.vrijeme.classes.WeatherAttributesData
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.vrijeme.R

class WeatherAttributeAdapter(
    private val attributesList: List<WeatherAttributesData>
) : RecyclerView.Adapter<WeatherAttributeAdapter.AttributeViewHolder>() {

    class AttributeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titleLabel: TextView = itemView.findViewById(R.id.titleLabel)
        val descriptionLabel: TextView = itemView.findViewById(R.id.descriptionLabel)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AttributeViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.attributes_layout, parent, false)
        return AttributeViewHolder(view)
    }

    override fun onBindViewHolder(holder: AttributeViewHolder, position: Int) {
        val attribute = attributesList[position]
        holder.titleLabel.text = when (position) {
            0 -> "Pressure"
            1 -> "Humidity"
            2 -> "Wind Speed"
            3 -> "Wind Degree"
            4 -> "Clouds"
            5 -> "Visibility"
            else -> "Unknown"
        }
        holder.descriptionLabel.text = when (position) {
            0 -> "${attribute.pressure} hPa"
            1 -> "${attribute.humidity} %"
            2 -> "${attribute.windSpeed} m/s"
            3 -> "${attribute.windDegree}°"
            4 -> "${attribute.clouds} %"
            5 -> "${attribute.visibility} m"
            else -> "Unknown"
        }
    }

    override fun getItemCount(): Int {
        return attributesList.size
    }
}