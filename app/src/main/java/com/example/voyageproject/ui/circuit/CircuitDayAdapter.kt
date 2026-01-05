package com.example.voyageproject.ui.circuit

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.voyageproject.R
import com.example.voyageproject.model.CircuitDay

class CircuitDayAdapter(
    private val days: List<CircuitDay>
) : RecyclerView.Adapter<CircuitDayAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvDayNumber: TextView = view.findViewById(R.id.tvDayNumber)
        val tvDayTitle: TextView = view.findViewById(R.id.tvDayTitle)
        val tvDayDescription: TextView = view.findViewById(R.id.tvDayDescription)
        val tvDayMeals: TextView = view.findViewById(R.id.tvDayMeals)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_circuit_day, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val day = days[position]
        
        holder.tvDayNumber.text = "Jour ${day.dayNumber}"
        holder.tvDayTitle.text = day.title
        
        // Formater la description en points
        val formattedDescription = day.description
            .split(". ")
            .filter { it.isNotBlank() }
            .joinToString("\n") { "• ${it.trim().removeSuffix(".")}" }
        
        holder.tvDayDescription.text = formattedDescription
        
        // Afficher les repas
        if (day.meals.isNotEmpty()) {
            holder.tvDayMeals.text = "🍽️ ${day.meals.joinToString(", ")}"
            holder.tvDayMeals.visibility = View.VISIBLE
        } else {
            holder.tvDayMeals.visibility = View.GONE
        }
    }

    override fun getItemCount() = days.size
}
