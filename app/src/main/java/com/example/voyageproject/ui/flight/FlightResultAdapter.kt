package com.example.voyageproject.ui.flight

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.voyageproject.R
import com.example.voyageproject.model.Flight
import com.example.voyageproject.model.FlightPriceCalculator
import com.example.voyageproject.model.FlightSearchCriteria
import java.text.SimpleDateFormat
import java.util.*

class FlightResultAdapter(
    private val flights: List<Flight>,
    private val searchCriteria: FlightSearchCriteria,
    private val onFlightClick: (Flight) -> Unit
) : RecyclerView.Adapter<FlightResultAdapter.FlightViewHolder>() {

    inner class FlightViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvAirline: TextView = view.findViewById(R.id.tvAirline)
        val tvFlightNumber: TextView = view.findViewById(R.id.tvFlightNumber)
        val tvRoute: TextView = view.findViewById(R.id.tvRoute)
        val tvDuration: TextView = view.findViewById(R.id.tvDuration)
        val tvSeatsAvailable: TextView = view.findViewById(R.id.tvSeatsAvailable)
        val tvPrice: TextView = view.findViewById(R.id.tvPrice)
        val btnViewDetails: Button = view.findViewById(R.id.btnViewDetails)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FlightViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_flight_result, parent, false)
        return FlightViewHolder(view)
    }

    override fun onBindViewHolder(holder: FlightViewHolder, position: Int) {
        val flight = flights[position]
        
        holder.tvAirline.text = flight.airline
        holder.tvFlightNumber.text = flight.flightNumber
        
        val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
        val departureTime = try {
            val date = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
                .parse(flight.departureTime)
            timeFormat.format(date!!)
        } catch (e: Exception) {
            "08:00"
        }
        
        val arrivalTime = try {
            val date = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
                .parse(flight.arrivalTime)
            timeFormat.format(date!!)
        } catch (e: Exception) {
            "12:00"
        }
        
        holder.tvRoute.text = "$departureTime ${flight.origin} → $arrivalTime ${flight.destination}"
        holder.tvDuration.text = flight.duration ?: "⏱ 4h"
        holder.tvSeatsAvailable.text = "💺 ${flight.seatsAvailable} sièges disponibles"
        
        val priceBreakdown = FlightPriceCalculator.calculatePrice(
            basePrice = flight.price,
            flightClass = searchCriteria.selectedClass,
            adults = searchCriteria.adults,
            children = searchCriteria.children
        )
        
        holder.tvPrice.text = "À partir de: ${String.format("%.2f", priceBreakdown.totalPrice)} TND"
        
        holder.btnViewDetails.setOnClickListener {
            onFlightClick(flight)
        }
        
        holder.itemView.setOnClickListener {
            onFlightClick(flight)
        }
    }

    override fun getItemCount() = flights.size
}
