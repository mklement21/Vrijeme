package com.example.vrijeme.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.vrijeme.R
import kotlin.math.ceil

data class WeatherAttribute(
    val title: String,
    val value: String
)

class WeatherAttributeAdapter(private val attributes: List<WeatherAttribute>) :
    RecyclerView.Adapter<WeatherAttributeAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.attributes_layout, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val attribute = attributes[position]
        holder.titleTextView.text = attribute.title
        holder.descriptionTextView.text = attribute.value
    }


    override fun getItemCount() : Int = attributes.size

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val titleTextView: TextView = view.findViewById(R.id.titleLabel)
        val descriptionTextView: TextView = view.findViewById(R.id.descriptionLabel)
    }
}
