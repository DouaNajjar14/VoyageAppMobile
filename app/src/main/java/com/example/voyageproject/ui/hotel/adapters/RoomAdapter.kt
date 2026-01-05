package com.example.voyageproject.ui.hotel.adapters

import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.voyageproject.R
import com.example.voyageproject.databinding.ItemRoomBookingStyleBinding
import com.example.voyageproject.model.Room
import com.example.voyageproject.ui.hotel.RoomDetailsActivity
import com.google.gson.Gson

class RoomAdapter(
    private val rooms: List<Room>,
    private val roomImages: List<String>,
    private val hotelId: String,
    private val hotelName: String,
    private val onRoomClick: (Room) -> Unit
) : RecyclerView.Adapter<RoomAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: ItemRoomBookingStyleBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemRoomBookingStyleBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val room = rooms[position]
        
        try {
            holder.binding.apply {
                // Disponibilité
                badgeAvailability.visibility = if (room.available) View.VISIBLE else View.GONE

                // Nom
                tvRoomName.text = room.name

                // Capacité
                val capacity = room.maxGuests ?: room.capacity
                tvCapacity.text = "$capacity ${if (capacity > 1) "personnes" else "personne"}"

                // Taille
                room.size?.let {
                    layoutSize.visibility = View.VISIBLE
                    tvSize.text = "$it m²"
                } ?: run {
                    layoutSize.visibility = View.GONE
                }

                // Type de lit
                room.bedType?.let {
                    tvBedType.visibility = View.VISIBLE
                    tvBedType.text = it
                } ?: run {
                    tvBedType.visibility = View.GONE
                }

                // Description
                room.description?.let {
                    tvRoomDescription.visibility = View.VISIBLE
                    tvRoomDescription.text = it
                } ?: run {
                    tvRoomDescription.visibility = View.GONE
                }

                // Équipements
                room.amenities?.takeIf { it.isNotEmpty() }?.let { amenities ->
                    rvRoomAmenities.visibility = View.VISIBLE
                    rvRoomAmenities.layoutManager = LinearLayoutManager(holder.itemView.context)
                    rvRoomAmenities.adapter = AmenityAdapter(amenities.take(3))
                } ?: run {
                    rvRoomAmenities.visibility = View.GONE
                }

                // Petit-déjeuner
                layoutBreakfast.visibility = if (room.breakfastIncluded) View.VISIBLE else View.GONE

                // Annulation
                room.cancellationPolicy?.let {
                    layoutCancellation.visibility = View.VISIBLE
                    tvCancellation.text = it
                } ?: run {
                    layoutCancellation.visibility = View.GONE
                }

                // Prix en TND
                tvPrice.text = "${room.price.toInt()} TND"

                // Bouton réserver
                btnReserveRoom.setOnClickListener {
                    try {
                        Log.d("ROOM", "Voir détails: $hotelName - ${room.name}")
                        
                        val context = holder.itemView.context
                        val intent = Intent(context, RoomDetailsActivity::class.java).apply {
                            putExtra("roomData", com.google.gson.Gson().toJson(room))
                            putExtra("hotelId", hotelId)
                            putExtra("hotelName", hotelName)
                        }
                        context.startActivity(intent)
                    } catch (e: Exception) {
                        Log.e("ROOM", "Erreur: ${e.message}", e)
                        Toast.makeText(
                            holder.itemView.context,
                            "Erreur: ${e.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        } catch (e: Exception) {
            Log.e("ROOM", "Erreur bind: ${e.message}", e)
        }
    }

    override fun getItemCount() = rooms.size
}
