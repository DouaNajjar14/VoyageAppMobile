package com.example.voyageproject.ui.circuit

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.voyageproject.R
import com.example.voyageproject.model.Circuit
import java.text.SimpleDateFormat
import java.util.*

class CircuitCardAdapter(
    private val circuits: List<Circuit>,
    private val departureDate: Date?,
    private val onCircuitClick: (Circuit) -> Unit
) : RecyclerView.Adapter<CircuitCardAdapter.CircuitViewHolder>() {

    inner class CircuitViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val ivCircuitImage: ImageView = view.findViewById(R.id.ivCircuitImage)
        val tvCircuitTitle: TextView = view.findViewById(R.id.tvCircuitTitle)
        val tvDestination: TextView = view.findViewById(R.id.tvDestination)
        val tvDuration: TextView = view.findViewById(R.id.tvDuration)
        val tvDurationDays: TextView? = view.findViewById(R.id.tvDurationDays)
        val tvCircuitDates: TextView? = view.findViewById(R.id.tvCircuitDates)
        val tvActivities: TextView = view.findViewById(R.id.tvActivities)
        val tvPrice: TextView = view.findViewById(R.id.tvPrice)
        val btnViewDetails: Button = view.findViewById(R.id.btnViewDetails)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CircuitViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_circuit_card, parent, false)
        return CircuitViewHolder(view)
    }

    override fun onBindViewHolder(holder: CircuitViewHolder, position: Int) {
        val circuit = circuits[position]

        // Image
        Glide.with(holder.itemView.context)
            .load(circuit.imageUrl)
            .placeholder(R.drawable.gradient_red_orange)
            .into(holder.ivCircuitImage)

        // Titre
        holder.tvCircuitTitle.text = circuit.title

        // Durée avec icône calendrier
        holder.tvDuration.text = "${circuit.duree}j"
        holder.tvDurationDays?.text = "📅 ${circuit.duree} jours"

        // Calculer et afficher les dates
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val startDate = departureDate ?: Date()
        val calendar = Calendar.getInstance()
        calendar.time = startDate
        calendar.add(Calendar.DAY_OF_YEAR, circuit.duree - 1)
        val endDate = calendar.time
        
        holder.tvCircuitDates?.text = "📅 ${dateFormat.format(startDate)} - ${dateFormat.format(endDate)}"

        // Destinations (pays/ville/lieu) - afficher toutes les destinations
        val destinationsText = if (!circuit.destinations.isNullOrEmpty()) {
            circuit.destinations.joinToString(", ")
        } else {
            "Destination"
        }
        holder.tvDestination.text = destinationsText

        // Activités (placeholder)
        holder.tvActivities.text = "🎯 Activités incluses"

        // Prix
        holder.tvPrice.text = "${String.format("%.0f", circuit.prix)} TND"

        // Click
        holder.btnViewDetails.setOnClickListener {
            onCircuitClick(circuit)
        }

        holder.itemView.setOnClickListener {
            onCircuitClick(circuit)
        }
    }

    override fun getItemCount() = circuits.size
}
