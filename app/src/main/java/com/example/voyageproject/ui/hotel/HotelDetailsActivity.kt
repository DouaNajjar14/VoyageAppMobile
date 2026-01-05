package com.example.voyageproject.ui.hotel

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.voyageproject.R
import com.example.voyageproject.databinding.ActivityHotelDetailsBinding
import com.example.voyageproject.model.Hotel
import com.example.voyageproject.ui.hotel.adapters.AmenityAdapter
import com.example.voyageproject.ui.hotel.adapters.RoomAdapter
import com.google.gson.Gson

class HotelDetailsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHotelDetailsBinding
    private lateinit var hotel: Hotel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHotelDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        try {
            val hotelData = intent.getStringExtra("hotelData")
            if (hotelData == null) {
                Toast.makeText(this, "Erreur: données manquantes", Toast.LENGTH_SHORT).show()
                finish()
                return
            }

            hotel = Gson().fromJson(hotelData, Hotel::class.java)
            
            setupToolbar()
            displayHotelInfo()
            setupRooms()
            
        } catch (e: Exception) {
            Log.e("HOTEL", "Erreur: ${e.message}", e)
            Toast.makeText(this, "Erreur: ${e.message}", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.setNavigationOnClickListener { finish() }
        // Pas de titre dans la toolbar pour éviter la duplication
    }

    private fun displayHotelInfo() {
        // Image
        val imageUrl = hotel.imageUrl ?: "https://images.unsplash.com/photo-1566073771259-6a8506099945?w=1200"
        Glide.with(this)
            .load(imageUrl)
            .placeholder(R.drawable.gradient_red_orange)
            .into(binding.ivHotelImage)

        // Info
        binding.tvHotelName.text = hotel.name
        binding.tvStars.text = "⭐".repeat(hotel.etoile)
        binding.tvLocation.text = "${hotel.address}, ${hotel.city}"
        
        // Prix en TND
        binding.tvPrice.text = "${hotel.pricePerNight} TND/nuit"

        // Description
        binding.tvDescription.text = hotel.description ?: 
            "Hôtel ${hotel.etoile} étoiles situé à ${hotel.city}."

        // Points forts de l'hôtel
        val highlights = listOf(
            "✓ Emplacement idéal",
            "✓ Personnel accueillant",
            "✓ Chambres confortables",
            "✓ Excellent rapport qualité-prix",
            "✓ Petit-déjeuner inclus"
        )
        binding.tvHighlights.text = highlights.joinToString("\n")

        // Équipements
        val amenities = hotel.amenities?.takeIf { it.isNotEmpty() } ?: listOf(
            "WiFi gratuit", "Parking", "Climatisation", "Restaurant", "Piscine", "Spa"
        )
        binding.rvAmenities.layoutManager = LinearLayoutManager(this)
        binding.rvAmenities.adapter = AmenityAdapter(amenities)

        // FAB - Navigate to booking flow
        binding.fabReserve.setOnClickListener {
            val intent = Intent(this, HotelDetailsBookingActivity::class.java)
            intent.putExtra("hotel", hotel)
            startActivity(intent)
        }
    }

    private fun setupRooms() {
        val rooms = hotel.rooms
        
        // Ne pas afficher de message si pas de chambres
        if (rooms == null || rooms.isEmpty()) {
            binding.rvRooms.visibility = View.GONE
            return
        }
        
        binding.rvRooms.layoutManager = LinearLayoutManager(this)
        
        // Images pour les chambres
        val roomImages = listOf(
            "https://images.unsplash.com/photo-1631049307264-da0ec9d70304?w=800",
            "https://images.unsplash.com/photo-1618773928121-c32242e63f39?w=800",
            "https://images.unsplash.com/photo-1582719478250-c89cae4dc85b?w=800",
            "https://images.unsplash.com/photo-1590490360182-c33d57733427?w=800",
            "https://images.unsplash.com/photo-1611892440504-42a792e24d32?w=800"
        )

        binding.rvRooms.adapter = RoomAdapter(
            rooms, 
            roomImages,
            hotel.id,
            hotel.name
        ) { }
    }
}
