package com.example.voyageproject.ui.offers.adapters

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.voyageproject.R
import com.example.voyageproject.databinding.ItemOfferNewBinding
import com.example.voyageproject.model.Circuit
import com.example.voyageproject.model.Flight
import com.example.voyageproject.model.Hotel
import com.example.voyageproject.ui.details.OfferDetailsActivity
import com.google.gson.Gson

class OffersAdapter(
    private val offers: List<Any>,
    private val type: String,
    private val onClick: (Any) -> Unit
) : RecyclerView.Adapter<OffersAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: ItemOfferNewBinding): RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemOfferNewBinding.inflate(
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
            is Hotel -> bindHotel(holder, offer)
            is Circuit -> bindCircuit(holder, offer)
            is Flight -> bindFlight(holder, offer)
        }

        // Clic sur la card pour voir les détails
        holder.binding.root.setOnClickListener {
            navigateToDetails(holder, offer)
        }

        // Clic sur le bouton réserver
        holder.binding.btnBook.setOnClickListener {
            navigateToDetails(holder, offer)
        }
    }

    private fun bindHotel(holder: ViewHolder, hotel: Hotel) {
        holder.binding.apply {
            tvOfferTitle.text = hotel.name
            tvOfferDescription.text = "${hotel.city}, ${hotel.country}"
            
            val price = hotel.pricePerNight ?: 0.0
            tvOfferPrice.text = "${price.toInt()} €"
            
            // Afficher les étoiles
            layoutStars.visibility = View.VISIBLE
            tvStars.text = "⭐".repeat(hotel.etoile)

            // Charger l'image
            loadImage(ivOfferImage, hotel.imageUrl)
        }
    }

    private fun bindCircuit(holder: ViewHolder, circuit: Circuit) {
        holder.binding.apply {
            tvOfferTitle.text = circuit.title
            tvOfferDescription.text = circuit.description
            tvOfferPrice.text = "${circuit.prix.toInt()} €"
            
            // Cacher les étoiles pour les circuits
            layoutStars.visibility = View.GONE

            // Charger l'image
            loadImage(ivOfferImage, circuit.imageUrl)
        }
    }

    private fun bindFlight(holder: ViewHolder, flight: Flight) {
        holder.binding.apply {
            tvOfferTitle.text = "${flight.origin} → ${flight.destination}"
            tvOfferDescription.text = "${flight.airline} - Vol ${flight.flightNumber}"
            tvOfferPrice.text = "${flight.price.toInt()} €"
            
            // Cacher les étoiles pour les vols
            layoutStars.visibility = View.GONE

            // Charger l'image
            loadImage(ivOfferImage, flight.imageUrl)
        }
    }

    private fun loadImage(imageView: android.widget.ImageView, imageUrl: String?) {
        if (!imageUrl.isNullOrEmpty()) {
            Glide.with(imageView.context)
                .load(imageUrl)
                .placeholder(R.drawable.gradient_red_orange)
                .error(R.drawable.gradient_red_orange)
                .into(imageView)
        } else {
            imageView.setImageResource(R.drawable.gradient_red_orange)
        }
    }

    private fun navigateToDetails(holder: ViewHolder, offer: Any) {
        val context = holder.itemView.context
        val intent = Intent(context, OfferDetailsActivity::class.java)
        
        val gson = Gson()
        intent.putExtra("offerType", type)
        intent.putExtra("offerData", gson.toJson(offer))
        
        context.startActivity(intent)
    }
}

