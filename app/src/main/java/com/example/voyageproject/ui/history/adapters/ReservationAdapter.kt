package com.example.voyageproject.ui.history.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.voyageproject.R
import com.example.voyageproject.model.Reservation

class ReservationAdapter(
    private val reservations: List<Reservation>,
    private val onItemClick: (Reservation) -> Unit
) : RecyclerView.Adapter<ReservationAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val cardView: CardView = view.findViewById(R.id.cardReservation)
        val tvOfferIcon: TextView = view.findViewById(R.id.tvOfferIcon)
        val tvOfferName: TextView = view.findViewById(R.id.tvOfferName)
        val tvOfferType: TextView = view.findViewById(R.id.tvOfferType)
        val tvPrice: TextView = view.findViewById(R.id.tvPrice)
        val tvStatus: TextView = view.findViewById(R.id.tvStatus)
        val tvBookingDate: TextView = view.findViewById(R.id.tvBookingDate)
        val tvDates: TextView = view.findViewById(R.id.tvDates)
        val tvGuests: TextView = view.findViewById(R.id.tvGuests)
        val viewColorBar: View = view.findViewById(R.id.viewColorBar)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_reservation, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val reservation = reservations[position]

        // Nom de l'offre
        holder.tvOfferName.text = reservation.offerName
        
        // Type et icône selon l'offre
        val offerType = reservation.offerType.lowercase()
        when (offerType) {
            "hotel" -> {
                holder.tvOfferIcon.text = "🏨"
                holder.tvOfferType.text = "HÔTEL"
                holder.viewColorBar.setBackgroundColor(android.graphics.Color.parseColor("#1976D2"))
            }
            "flight" -> {
                holder.tvOfferIcon.text = "✈️"
                holder.tvOfferType.text = "VOL"
                holder.viewColorBar.setBackgroundColor(android.graphics.Color.parseColor("#F57C00"))
            }
            "circuit" -> {
                holder.tvOfferIcon.text = "🗺️"
                holder.tvOfferType.text = "CIRCUIT"
                holder.viewColorBar.setBackgroundColor(android.graphics.Color.parseColor("#673AB7"))
            }
            else -> {
                holder.tvOfferIcon.text = "📋"
                holder.tvOfferType.text = reservation.offerType.uppercase()
                holder.viewColorBar.setBackgroundColor(android.graphics.Color.parseColor("#757575"))
            }
        }
        
        // Prix (déjà en TND dans la base de données)
        holder.tvPrice.text = String.format("%.2f TND", reservation.price)
        
        // Statut
        holder.tvStatus.text = when (reservation.status.lowercase()) {
            "confirmed" -> "✅ Confirmée"
            "pending" -> "⏳ En attente"
            "cancelled" -> "❌ Annulée"
            else -> reservation.status
        }

        // Couleur du statut
        val statusColor = when (reservation.status.lowercase()) {
            "confirmed" -> android.graphics.Color.parseColor("#4CAF50")
            "pending" -> android.graphics.Color.parseColor("#FF9800")
            "cancelled" -> android.graphics.Color.parseColor("#F44336")
            else -> android.graphics.Color.parseColor("#666666")
        }
        holder.tvStatus.setTextColor(statusColor)

        // Date de réservation (format court)
        val bookingDate = try {
            reservation.bookingDate.substring(0, 10)
        } catch (e: Exception) {
            reservation.bookingDate
        }
        holder.tvBookingDate.text = bookingDate

        // Masquer les champs optionnels pour l'instant
        holder.tvDates.visibility = View.GONE
        holder.tvGuests.visibility = View.GONE
        
        // Clic sur la carte pour voir les détails
        holder.cardView.setOnClickListener {
            onItemClick(reservation)
        }
    }

    override fun getItemCount() = reservations.size
}
