package com.example.voyageproject.ui.offers.adapters

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.example.voyageproject.R
import com.example.voyageproject.databinding.ItemOfferPremiumBinding
import com.example.voyageproject.model.Circuit
import com.example.voyageproject.model.Flight
import com.example.voyageproject.model.Hotel
import com.example.voyageproject.ui.details.OfferDetailsActivity
import com.google.gson.Gson

class PremiumOffersAdapter(
    private val offers: List<Any>,
    private val type: String
) : RecyclerView.Adapter<PremiumOffersAdapter.ViewHolder>() {

    // URLs d'images réelles depuis Unsplash (gratuites et libres de droits)
    private val hotelImages = listOf(
        "https://images.unsplash.com/photo-1566073771259-6a8506099945?w=800",
        "https://images.unsplash.com/photo-1542314831-068cd1dbfeeb?w=800",
        "https://images.unsplash.com/photo-1520250497591-112f2f40a3f4?w=800",
        "https://images.unsplash.com/photo-1571896349842-33c89424de2d?w=800",
        "https://images.unsplash.com/photo-1551882547-ff40c63fe5fa?w=800"
    )

    private val flightImages = listOf(
        "https://images.unsplash.com/photo-1436491865332-7a61a109cc05?w=800",
        "https://images.unsplash.com/photo-1464037866556-6812c9d1c72e?w=800",
        "https://images.unsplash.com/photo-1583531172005-814191b8b6c0?w=800",
        "https://images.unsplash.com/photo-1569629743817-70d8db6c323b?w=800",
        "https://images.unsplash.com/photo-1556388158-158f25a6c7a5?w=800"
    )

    private val circuitImages = listOf(
        "https://images.unsplash.com/photo-1488646953014-85cb44e25828?w=800",
        "https://images.unsplash.com/photo-1502602898657-3e91760cbb34?w=800",
        "https://images.unsplash.com/photo-1499856871958-5b9627545d1a?w=800",
        "https://images.unsplash.com/photo-1523906834658-6e24ef2386f9?w=800",
        "https://images.unsplash.com/photo-1476514525535-07fb3b4ae5f1?w=800"
    )

    inner class ViewHolder(val binding: ItemOfferPremiumBinding): RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemOfferPremiumBinding.inflate(
            LayoutInflater.from(parent.context), 
            parent, 
            false
        )
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int = offers.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val offer = offers[position]
        
        when(offer) {
            is Hotel -> bindHotel(holder, offer, position)
            is Circuit -> bindCircuit(holder, offer, position)
            is Flight -> bindFlight(holder, offer, position)
        }

        // Clic sur la card
        holder.binding.root.setOnClickListener {
            navigateToDetails(holder, offer)
        }

        // Clic sur le bouton détails
        holder.binding.btnDetails.setOnClickListener {
            navigateToDetails(holder, offer)
        }
    }

    private fun bindHotel(holder: ViewHolder, hotel: Hotel, position: Int) {
        holder.binding.apply {
            tvType.text = "HÔTEL"
            tvTitle.text = hotel.name
            
            // Afficher l'icône de localisation pour les hôtels
            ivLocationIcon.visibility = View.VISIBLE
            tvLocation.text = "${hotel.city}, ${hotel.country}"
            
            val price = hotel.pricePerNight ?: 0.0
            tvPrice.text = "${price.toInt()} TND"
            
            // Afficher les étoiles
            layoutStars.visibility = View.VISIBLE
            tvStars.text = "⭐".repeat(hotel.etoile)
            
            // Description
            tvDescription.text = hotel.description ?: 
                "Magnifique hôtel avec tous les conforts modernes"
            
            // Info supplémentaire
            val roomCount = hotel.rooms?.size ?: 0
            tvExtraInfo.visibility = View.VISIBLE
            tvExtraInfo.text = if (roomCount > 0) "$roomCount types de chambres" else "Chambres disponibles"
            ivExtraIcon.visibility = View.VISIBLE
            ivExtraIcon.setImageResource(R.drawable.ic_home)

            // Charger l'image
            val imageUrl = hotel.imageUrl ?: hotelImages[position % hotelImages.size]
            loadImage(ivOfferImage, imageUrl)
        }
    }

    private fun bindCircuit(holder: ViewHolder, circuit: Circuit, position: Int) {
        holder.binding.apply {
            tvType.text = "CIRCUIT"
            tvTitle.text = circuit.title
            
            // Cacher l'icône de localisation pour les circuits
            ivLocationIcon.visibility = View.GONE
            
            // Afficher la durée avec icône calendrier (emoji dans le texte)
            tvLocation.text = "📅 ${circuit.duree} jours"
            
            tvPrice.text = "${circuit.prix.toInt()} TND"
            
            // Cacher les étoiles
            layoutStars.visibility = View.GONE
            
            // Description
            tvDescription.text = circuit.description
            
            // Info supplémentaire - Afficher les destinations avec icône de localisation
            if (!circuit.destinations.isNullOrEmpty()) {
                tvExtraInfo.text = "📍 ${circuit.destinations.joinToString(", ")}"
                ivExtraIcon.visibility = View.GONE
            } else {
                // Si pas de destinations, cacher la section
                tvExtraInfo.visibility = View.GONE
                ivExtraIcon.visibility = View.GONE
            }

            // Charger l'image
            val imageUrl = circuit.imageUrl ?: circuitImages[position % circuitImages.size]
            loadImage(ivOfferImage, imageUrl)
        }
    }

    private fun bindFlight(holder: ViewHolder, flight: Flight, position: Int) {
        holder.binding.apply {
            tvType.text = "VOL"
            tvTitle.text = "${flight.origin} → ${flight.destination}"
            
            // Afficher l'icône de localisation pour les vols
            ivLocationIcon.visibility = View.VISIBLE
            tvLocation.text = "${flight.airline} - ${flight.flightNumber}"
            
            tvPrice.text = "${flight.price.toInt()} TND"
            
            // Cacher les étoiles
            layoutStars.visibility = View.GONE
            
            // Description
            tvDescription.text = "Vol direct avec ${flight.airline}. ${flight.class_type ?: "Economy"}"
            
            // Info supplémentaire
            tvExtraInfo.visibility = View.VISIBLE
            tvExtraInfo.text = "${flight.seatsAvailable} places disponibles"
            ivExtraIcon.visibility = View.VISIBLE
            ivExtraIcon.setImageResource(R.drawable.ic_info)

            // Charger l'image
            val imageUrl = flight.imageUrl ?: flightImages[position % flightImages.size]
            loadImage(ivOfferImage, imageUrl)
        }
    }

    private fun loadImage(imageView: android.widget.ImageView, imageUrl: String) {
        Glide.with(imageView.context)
            .load(imageUrl)
            .placeholder(R.drawable.gradient_red_orange)
            .error(R.drawable.gradient_red_orange)
            .transition(DrawableTransitionOptions.withCrossFade())
            .centerCrop()
            .into(imageView)
    }

    private fun navigateToDetails(holder: ViewHolder, offer: Any) {
        val context = holder.itemView.context
        val gson = Gson()
        
        when(offer) {
            is Hotel -> {
                val intent = Intent(context, com.example.voyageproject.ui.hotel.HotelDetailsActivity::class.java)
                intent.putExtra("hotelData", gson.toJson(offer))
                context.startActivity(intent)
            }
            is Circuit -> {
                val intent = Intent(context, com.example.voyageproject.ui.circuit.CircuitDetailsActivity::class.java)
                intent.putExtra("circuitData", gson.toJson(offer))
                context.startActivity(intent)
            }
            is Flight -> {
                val intent = Intent(context, com.example.voyageproject.ui.flight.FlightDetailsActivity::class.java)
                intent.putExtra("flightData", gson.toJson(offer))
                context.startActivity(intent)
            }
        }
    }
}
