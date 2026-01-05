package com.example.voyageproject.ui.booking

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.voyageproject.databinding.ActivityBookingBinding
import com.example.voyageproject.model.Circuit
import com.example.voyageproject.model.Flight
import com.example.voyageproject.model.Hotel
import com.example.voyageproject.ui.payment.PaymentActivity
import com.example.voyageproject.utils.SessionManager
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.gson.Gson

class BookingActivity : AppCompatActivity() {

    private lateinit var binding: ActivityBookingBinding
    private lateinit var session: SessionManager
    private var offerType: String = ""
    private var offerData: String = ""
    private var totalPrice: Double = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBookingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        session = SessionManager(this)

        offerType = intent.getStringExtra("offerType") ?: ""
        offerData = intent.getStringExtra("offerData") ?: ""

        binding.btnBack.setOnClickListener { finish() }
        binding.btnContinueToPayment.setOnClickListener { validateAndContinue() }

        setupForm()
        loadUserInfo()
    }

    private fun setupForm() {
        val gson = Gson()
        
        when (offerType) {
            "hotel" -> {
                val hotel = gson.fromJson(offerData, Hotel::class.java)
                binding.tvOfferSummary.text = hotel.name
                totalPrice = hotel.pricePerNight ?: 0.0
                addHotelSpecificFields(hotel)
            }
            "flight" -> {
                val flight = gson.fromJson(offerData, Flight::class.java)
                binding.tvOfferSummary.text = "${flight.origin} → ${flight.destination}"
                totalPrice = flight.price
                addFlightSpecificFields()
            }
            "circuit" -> {
                val circuit = gson.fromJson(offerData, Circuit::class.java)
                binding.tvOfferSummary.text = circuit.title
                totalPrice = circuit.prix
                addCircuitSpecificFields()
            }
        }

        binding.tvBasePrice.text = "${totalPrice.toInt()} €"
        binding.tvTotalPrice.text = "${totalPrice.toInt()} €"
    }

    private fun addHotelSpecificFields(hotel: Hotel) {
        // Date d'arrivée
        addTextField("Date d'arrivée", "JJ/MM/AAAA", "date_checkin")
        
        // Date de départ
        addTextField("Date de départ", "JJ/MM/AAAA", "date_checkout")
        
        // Nombre de personnes
        addTextField("Nombre de personnes", "1", "num_guests")
    }

    private fun addFlightSpecificFields() {
        // Nombre de passagers
        addTextField("Nombre de passagers", "1", "num_passengers")
        
        // Numéro de passeport
        addTextField("Numéro de passeport", "", "passport_number")
    }

    private fun addCircuitSpecificFields() {
        // Date de départ souhaitée
        addTextField("Date de départ souhaitée", "JJ/MM/AAAA", "departure_date")
        
        // Nombre de participants
        addTextField("Nombre de participants", "1", "num_participants")
    }

    private fun addTextField(hint: String, placeholder: String, tag: String) {
        val textInputLayout = TextInputLayout(this).apply {
            layoutParams = android.widget.LinearLayout.LayoutParams(
                android.widget.LinearLayout.LayoutParams.MATCH_PARENT,
                android.widget.LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                bottomMargin = 32
            }
            this.hint = hint
        }

        val editText = TextInputEditText(this).apply {
            this.tag = tag
            this.hint = placeholder
        }

        textInputLayout.addView(editText)
        binding.layoutSpecificFields.addView(textInputLayout)
    }

    private fun loadUserInfo() {
        val email = session.getEmail()
        if (!email.isNullOrEmpty()) {
            binding.etEmail.setText(email)
        }
    }

    private fun validateAndContinue() {
        val fullName = binding.etFullName.text.toString().trim()
        val email = binding.etEmail.text.toString().trim()
        val phone = binding.etPhone.text.toString().trim()

        if (fullName.isEmpty()) {
            Toast.makeText(this, "Veuillez entrer votre nom complet", Toast.LENGTH_SHORT).show()
            return
        }

        if (email.isEmpty()) {
            Toast.makeText(this, "Veuillez entrer votre email", Toast.LENGTH_SHORT).show()
            return
        }

        if (phone.isEmpty()) {
            Toast.makeText(this, "Veuillez entrer votre téléphone", Toast.LENGTH_SHORT).show()
            return
        }

        // Valider les champs spécifiques
        for (i in 0 until binding.layoutSpecificFields.childCount) {
            val layout = binding.layoutSpecificFields.getChildAt(i) as? TextInputLayout
            val editText = layout?.editText
            if (editText?.text.isNullOrEmpty()) {
                Toast.makeText(this, "Veuillez remplir tous les champs", Toast.LENGTH_SHORT).show()
                return
            }
        }

        // Naviguer vers le paiement
        val intent = Intent(this, PaymentActivity::class.java)
        intent.putExtra("offerType", offerType)
        intent.putExtra("offerData", offerData)
        intent.putExtra("fullName", fullName)
        intent.putExtra("email", email)
        intent.putExtra("phone", phone)
        intent.putExtra("totalPrice", totalPrice)
        startActivity(intent)
    }
}
