package com.example.voyageproject.ui.hotel

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.voyageproject.R
import com.example.voyageproject.databinding.ActivityRoomDetailsBinding
import com.example.voyageproject.model.Room
import com.example.voyageproject.ui.hotel.adapters.AmenityAdapter
import com.google.gson.Gson

class RoomDetailsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRoomDetailsBinding
    private lateinit var room: Room
    private var hotelId: String = ""
    private var hotelName: String = ""
    
    private var basePrice: Double = 0.0
    private var viewPrice: Double = 0.0
    private var balconyPrice: Double = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRoomDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        try {
            val roomData = intent.getStringExtra("roomData")
            hotelId = intent.getStringExtra("hotelId") ?: ""
            hotelName = intent.getStringExtra("hotelName") ?: ""
            
            if (roomData == null) {
                finish()
                return
            }

            room = Gson().fromJson(roomData, Room::class.java)
            basePrice = room.price
            
            setupToolbar()
            displayRoomInfo()
            setupOptions()
            setupReserveButton()
            updatePrice()
            
        } catch (e: Exception) {
            Log.e("ROOM_DETAILS", "Erreur: ${e.message}", e)
            finish()
        }
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.setNavigationOnClickListener { finish() }
        binding.collapsingToolbar.title = room.name
    }

    private fun displayRoomInfo() {
        // Image
        val imageUrl = room.imageUrl ?: "https://images.unsplash.com/photo-1631049307264-da0ec9d70304?w=800"
        Glide.with(this)
            .load(imageUrl)
            .placeholder(R.drawable.gradient_red_orange)
            .into(binding.ivRoomImage)

        // Info de base
        binding.tvRoomName.text = room.name
        
        val roomType = room.roomType ?: when (room.capacity) {
            1 -> "Single"
            2 -> "Double"
            3 -> "Triple"
            4 -> "Quadruple"
            else -> "${room.capacity} personnes"
        }
        binding.tvRoomType.text = "$roomType • ${room.capacity} ${if (room.capacity > 1) "adultes" else "adulte"}"
        
        room.size?.let {
            binding.tvRoomSize.text = "$it m²"
        }
        
        binding.tvDescription.text = room.description ?: 
            "Chambre confortable et spacieuse avec tous les équipements modernes."

        // Équipements
        val amenities = mutableListOf<String>()
        if (room.hasWifi) amenities.add("WiFi gratuit")
        if (room.hasTV) amenities.add("TV écran plat")
        if (room.hasBathroom) amenities.add("Salle de bain privée")
        if (room.hasMinibar) amenities.add("Minibar")
        if (room.hasSafe) amenities.add("Coffre-fort")
        room.amenities?.let { amenities.addAll(it) }
        
        if (amenities.isNotEmpty()) {
            binding.rvAmenities.layoutManager = LinearLayoutManager(this)
            binding.rvAmenities.adapter = AmenityAdapter(amenities)
        }
    }

    private fun setupOptions() {
        // Vue standard sélectionnée par défaut
        binding.rbStandardView.isChecked = true
        
        // Activer/désactiver les options selon la chambre
        binding.rbPoolView.isEnabled = room.hasPoolView
        binding.rbSeaView.isEnabled = room.hasSeaView
        
        if (!room.hasPoolView) {
            binding.rbPoolView.text = "Vue sur piscine (non disponible)"
        } else {
            binding.rbPoolView.text = "Vue sur piscine (+${room.poolViewPrice.toInt()}€/nuit)"
        }
        
        if (!room.hasSeaView) {
            binding.rbSeaView.text = "Vue sur mer (non disponible)"
        } else {
            binding.rbSeaView.text = "Vue sur mer (+${room.seaViewPrice.toInt()}€/nuit)"
        }
        
        binding.cbBalcony.isEnabled = room.hasBalcony
        if (!room.hasBalcony) {
            binding.cbBalcony.text = "Balcon (non disponible)"
        } else {
            binding.cbBalcony.text = "Balcon (+${room.balconyPrice.toInt()}€/nuit)"
        }
        
        // Listeners pour mise à jour du prix
        binding.rgView.setOnCheckedChangeListener { _, _ -> updatePrice() }
        binding.cbBalcony.setOnCheckedChangeListener { _, _ -> updatePrice() }
    }

    private fun updatePrice() {
        // Prix de base
        var total = basePrice
        binding.tvBasePrice.text = "${basePrice.toInt()} €"
        
        // Supplément vue
        viewPrice = when {
            binding.rbPoolView.isChecked && room.hasPoolView -> room.poolViewPrice
            binding.rbSeaView.isChecked && room.hasSeaView -> room.seaViewPrice
            else -> 0.0
        }
        
        if (viewPrice > 0) {
            binding.layoutViewPrice.visibility = View.VISIBLE
            binding.tvViewPrice.text = "+${viewPrice.toInt()} €"
            total += viewPrice
        } else {
            binding.layoutViewPrice.visibility = View.GONE
        }
        
        // Balcon
        balconyPrice = if (binding.cbBalcony.isChecked && room.hasBalcony) {
            room.balconyPrice
        } else {
            0.0
        }
        
        if (balconyPrice > 0) {
            binding.layoutBalconyPrice.visibility = View.VISIBLE
            binding.tvBalconyPrice.text = "+${balconyPrice.toInt()} €"
            total += balconyPrice
        } else {
            binding.layoutBalconyPrice.visibility = View.GONE
        }
        
        // Total
        binding.tvTotalPrice.text = "${total.toInt()} €"
    }

    private fun setupReserveButton() {
        binding.btnReserve.setOnClickListener {
            val totalPrice = basePrice + viewPrice + balconyPrice
            
            val viewType = when {
                binding.rbPoolView.isChecked -> "Vue piscine"
                binding.rbSeaView.isChecked -> "Vue mer"
                else -> "Vue standard"
            }
            
            val hasBalcony = binding.cbBalcony.isChecked
            
            val intent = Intent(this, HotelBookingActivity::class.java).apply {
                putExtra("hotelId", hotelId)
                putExtra("hotelName", hotelName)
                putExtra("roomId", room.id)
                putExtra("roomName", room.name)
                putExtra("pricePerNight", totalPrice)
                putExtra("maxGuests", room.maxGuests ?: room.capacity)
                putExtra("viewType", viewType)
                putExtra("hasBalcony", hasBalcony)
            }
            startActivity(intent)
        }
    }
}
