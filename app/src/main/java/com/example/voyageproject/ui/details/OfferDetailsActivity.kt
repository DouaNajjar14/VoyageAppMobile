package com.example.voyageproject.ui.details

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.voyageproject.R
import com.example.voyageproject.databinding.ActivityOfferDetailsBinding
import com.example.voyageproject.model.Circuit
import com.example.voyageproject.model.Flight
import com.example.voyageproject.model.Hotel
import com.example.voyageproject.ui.booking.BookingActivity
import com.google.gson.Gson

class OfferDetailsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOfferDetailsBinding
    private var offerType: String = ""
    private var offerData: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOfferDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        offerType = intent.getStringExtra("offerType") ?: ""
        offerData = intent.getStringExtra("offerData") ?: ""

        binding.btnBack.setOnClickListener { finish() }
        binding.btnReserve.setOnClickListener { navigateToBooking() }

        displayOfferDetails()
    }

    private fun displayOfferDetails() {
        val gson = Gson()
        
        when (offerType) {
            "hotel" -> {
                val hotel = gson.fromJson(offerData, Hotel::class.java)
                displayHotelDetails(hotel)
            }
            "flight" -> {
                val flight = gson.fromJson(offerData, Flight::class.java)
                displayFlightDetails(flight)
            }
            "circuit" -> {
                val circuit = gson.fromJson(offerData, Circuit::class.java)
                displayCircuitDetails(circuit)
            }
        }
    }

    private fun displayHotelDetails(hotel: Hotel) {
        binding.tvTitle.text = hotel.name
        binding.tvLocation.text = "${hotel.city}, ${hotel.country}"
        binding.tvStars.visibility = View.VISIBLE
        binding.tvStars.text = "⭐".repeat(hotel.etoile)
        
        val price = hotel.pricePerNight ?: 0.0
        binding.tvPrice.text = "${price.toInt()} €"
        
        binding.tvDescription.text = hotel.description ?: 
            "Magnifique hôtel situé à ${hotel.city}. Profitez d'un séjour inoubliable avec tous les conforts modernes."

        // Charger l'image
        loadImage(hotel.imageUrl)

        // Afficher les chambres si disponibles
        hotel.rooms?.let { rooms ->
            if (rooms.isNotEmpty()) {
                addInfoSection("Chambres disponibles")
                rooms.forEach { room ->
                    addInfoItem("${room.roomType ?: room.name} - ${room.price.toInt()} € (${room.capacity} pers.)")
                }
            }
        }

        // Afficher les équipements
        hotel.amenities?.let { amenities ->
            if (amenities.isNotEmpty()) {
                addInfoSection("Équipements")
                amenities.forEach { addInfoItem(it) }
            }
        }
    }

    private fun displayFlightDetails(flight: Flight) {
        binding.tvTitle.text = "${flight.origin} → ${flight.destination}"
        binding.tvLocation.text = "${flight.airline} - Vol ${flight.flightNumber}"
        binding.tvPrice.text = "${flight.price.toInt()} €"
        
        binding.tvDescription.text = "Vol direct de ${flight.origin} à ${flight.destination}. " +
            "Profitez d'un voyage confortable avec ${flight.airline}."

        // Charger l'image
        loadImage(flight.imageUrl)

        // Informations du vol
        addInfoSection("Détails du vol")
        addInfoItem("🛫 Départ: ${formatDateTime(flight.departureTime)}")
        addInfoItem("🛬 Arrivée: ${formatDateTime(flight.arrivalTime)}")
        addInfoItem("⏱️ Durée: ${flight.duration ?: "Calculée automatiquement"}")
        addInfoItem("💺 Places disponibles: ${flight.seatsAvailable}")
        addInfoItem("🎫 Classe: ${flight.class_type ?: "Economy"}")
    }

    private fun displayCircuitDetails(circuit: Circuit) {
        binding.tvTitle.text = circuit.title
        binding.tvLocation.text = "${circuit.duree} jours"
        binding.tvPrice.text = "${circuit.prix.toInt()} €"
        
        binding.tvDescription.text = circuit.description

        // Charger l'image
        loadImage(circuit.imageUrl)

        // Destinations
        circuit.destinations?.let { destinations ->
            if (destinations.isNotEmpty()) {
                addInfoSection("Destinations")
                destinations.forEach { addInfoItem("📍 $it") }
            }
        }

        // Ce qui est inclus
        circuit.includes?.let { includes ->
            if (includes.isNotEmpty()) {
                addInfoSection("Inclus dans le prix")
                includes.forEach { addInfoItem("✓ $it") }
            }
        }

        addInfoSection("Informations")
        addInfoItem("⏱️ Durée: ${circuit.duree} jours")
        addInfoItem("💰 Prix par personne: ${circuit.prix.toInt()} €")
    }

    private fun loadImage(imageUrl: String?) {
        // URLs d'images par défaut selon le type
        val defaultImages = mapOf(
            "hotel" to "https://images.unsplash.com/photo-1566073771259-6a8506099945?w=1200",
            "flight" to "https://images.unsplash.com/photo-1436491865332-7a61a109cc05?w=1200",
            "circuit" to "https://images.unsplash.com/photo-1488646953014-85cb44e25828?w=1200"
        )
        
        val finalUrl = imageUrl ?: defaultImages[offerType] ?: defaultImages["hotel"]!!
        
        Glide.with(this)
            .load(finalUrl)
            .placeholder(R.drawable.gradient_red_orange)
            .error(R.drawable.gradient_red_orange)
            .centerCrop()
            .into(binding.ivOfferImage)
    }

    private fun addInfoSection(title: String) {
        val textView = TextView(this).apply {
            text = title
            textSize = 18f
            setTextColor(getColor(R.color.black))
            setTypeface(null, android.graphics.Typeface.BOLD)
            setPadding(0, 32, 0, 16)
        }
        binding.layoutExtraInfo.addView(textView)
    }

    private fun addInfoItem(text: String) {
        val textView = TextView(this).apply {
            this.text = text
            textSize = 16f
            setTextColor(getColor(R.color.black))
            setPadding(0, 8, 0, 8)
        }
        binding.layoutExtraInfo.addView(textView)
    }

    private fun formatDateTime(dateTime: String): String {
        // Format simple pour l'affichage
        return try {
            dateTime.replace("T", " à ").substring(0, 16)
        } catch (e: Exception) {
            dateTime
        }
    }

    private fun navigateToBooking() {
        val intent = Intent(this, BookingActivity::class.java)
        intent.putExtra("offerType", offerType)
        intent.putExtra("offerData", offerData)
        startActivity(intent)
    }
}
