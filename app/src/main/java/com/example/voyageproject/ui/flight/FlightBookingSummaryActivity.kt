package com.example.voyageproject.ui.flight

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.voyageproject.R
import com.example.voyageproject.model.*
import com.example.voyageproject.ui.payment.PaymentActivity
import java.text.SimpleDateFormat
import java.util.*

class FlightBookingSummaryActivity : AppCompatActivity() {

    private lateinit var tvFlightInfo: TextView
    private lateinit var tvPassengersInfo: TextView
    private lateinit var tvClassInfo: TextView
    private lateinit var tvPriceBreakdown: TextView
    private lateinit var tvTotalPrice: TextView
    private lateinit var btnModify: Button
    private lateinit var btnPay: Button
    
    private lateinit var flight: Flight
    private lateinit var searchCriteria: FlightSearchCriteria
    private lateinit var priceBreakdown: FlightPriceBreakdown

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_flight_booking_summary)

        flight = intent.getSerializableExtra("flight") as Flight
        searchCriteria = intent.getParcelableExtra("search_criteria")!!

        initViews()
        calculatePrice()
        displaySummary()
        setupButtons()
    }

    private fun initViews() {
        tvFlightInfo = findViewById(R.id.tvFlightInfo)
        tvPassengersInfo = findViewById(R.id.tvPassengersInfo)
        tvClassInfo = findViewById(R.id.tvClassInfo)
        tvPriceBreakdown = findViewById(R.id.tvPriceBreakdown)
        tvTotalPrice = findViewById(R.id.tvTotalPrice)
        btnModify = findViewById(R.id.btnModify)
        btnPay = findViewById(R.id.btnPay)
        
        supportActionBar?.title = "Récapitulatif de réservation"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    private fun calculatePrice() {
        priceBreakdown = FlightPriceCalculator.calculatePrice(
            basePrice = flight.price,
            flightClass = searchCriteria.selectedClass,
            adults = searchCriteria.adults,
            children = searchCriteria.children
        )
    }

    private fun displaySummary() {
        // Informations du vol
        val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        
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
        
        // Type de billet
        val tripTypeText = when (searchCriteria.tripType) {
            TripType.ONE_WAY -> "Aller simple"
            TripType.ROUND_TRIP -> "Aller-retour"
        }
        
        val flightInfo = StringBuilder()
        flightInfo.append("${flight.airline} - ${flight.flightNumber}\n\n")
        flightInfo.append("🎫 Type: $tripTypeText\n\n")
        flightInfo.append("📅 Départ: ${searchCriteria.departureDate}\n")
        if (searchCriteria.tripType == TripType.ROUND_TRIP && searchCriteria.returnDate != null) {
            flightInfo.append("📅 Retour: ${searchCriteria.returnDate}\n")
        }
        flightInfo.append("\n")
        flightInfo.append("🛫 DÉPART: $departureTime - ${flight.origin}\n")
        flightInfo.append("🛬 ARRIVÉE: $arrivalTime - ${flight.destination}\n\n")
        flightInfo.append("⏱️ Durée: ${flight.duration ?: "4h"}")
        
        tvFlightInfo.text = flightInfo.toString()
        
        // Informations passagers
        val passengersList = mutableListOf<String>()
        if (searchCriteria.adults > 0) {
            passengersList.add("• ${searchCriteria.adults} adulte${if (searchCriteria.adults > 1) "s" else ""} (18+)")
        }
        
        val freeChildren = searchCriteria.children.filter { it.isFree() }
        val paidChildren = searchCriteria.children.filter { !it.isFree() }
        
        paidChildren.forEach { child ->
            passengersList.add("• 1 enfant (${child.age} ans) - 50% du tarif")
        }
        
        freeChildren.forEach { child ->
            passengersList.add("• 1 bébé (${child.age} ans) - Gratuit")
        }
        
        tvPassengersInfo.text = passengersList.joinToString("\n")
        
        // Classe de vol - affichage structuré
        val classFeatures = searchCriteria.selectedClass.getFeatures()
        val classInfo = StringBuilder()
        classInfo.append("${searchCriteria.selectedClass.displayName}\n")
        classInfo.append("Multiplicateur: ×${searchCriteria.selectedClass.priceMultiplier}\n\n")
        classInfo.append("Avantages inclus:\n")
        classFeatures.take(4).forEach { feature ->
            classInfo.append("  $feature\n")
        }
        
        tvClassInfo.text = classInfo.toString().trim()
        
        // Détail des prix
        val breakdown = StringBuilder()
        breakdown.append("Prix de base: ${String.format("%.2f", priceBreakdown.basePrice)} TND\n")
        breakdown.append("Classe ${searchCriteria.selectedClass.displayName} (×${searchCriteria.selectedClass.priceMultiplier}):\n")
        breakdown.append("  = ${String.format("%.2f", priceBreakdown.pricePerPerson)} TND/personne\n\n")
        
        breakdown.append("Passagers:\n")
        if (priceBreakdown.adults > 0) {
            breakdown.append("  • ${priceBreakdown.adults} adulte${if (priceBreakdown.adults > 1) "s" else ""}: ")
            breakdown.append("${String.format("%.2f", priceBreakdown.adultsPrice)} TND\n")
        }
        
        if (priceBreakdown.paidChildren > 0) {
            breakdown.append("  • ${priceBreakdown.paidChildren} enfant${if (priceBreakdown.paidChildren > 1) "s" else ""} (50%): ")
            breakdown.append("${String.format("%.2f", priceBreakdown.childrenPrice)} TND\n")
        }
        
        if (priceBreakdown.freeChildren > 0) {
            breakdown.append("  • ${priceBreakdown.freeChildren} bébé${if (priceBreakdown.freeChildren > 1) "s" else ""}: ")
            breakdown.append("0 TND (gratuit)\n")
        }
        
        tvPriceBreakdown.text = breakdown.toString()
        
        // Prix total
        tvTotalPrice.text = String.format("%.2f TND", priceBreakdown.totalPrice)
    }

    private fun setupButtons() {
        btnModify.setOnClickListener {
            finish()
        }
        
        btnPay.setOnClickListener {
            proceedToPayment()
        }
    }

    private fun proceedToPayment() {
        val intent = Intent(this, PaymentActivity::class.java)
        intent.putExtra("offerId", flight.id)
        intent.putExtra("offerType", "flight")
        intent.putExtra("offerPrice", priceBreakdown.totalPrice)
        intent.putExtra("offerName", "${flight.airline} ${flight.flightNumber}")
        
        // Créer les détails pour l'affichage
        val details = StringBuilder()
        details.append("${searchCriteria.departureDate}\n")
        details.append("${flight.origin} → ${flight.destination}\n")
        details.append("Classe: ${searchCriteria.selectedClass.displayName}\n")
        details.append("${searchCriteria.adults} adulte${if (searchCriteria.adults > 1) "s" else ""}")
        if (searchCriteria.children.isNotEmpty()) {
            details.append(", ${searchCriteria.children.size} enfant${if (searchCriteria.children.size > 1) "s" else ""}")
        }
        
        intent.putExtra("offerDetails", details.toString())
        
        // Ajouter les détails pour la réservation backend
        intent.putExtra("departure_date", searchCriteria.departureDate)
        intent.putExtra("return_date", searchCriteria.returnDate ?: "")
        intent.putExtra("adults_count", searchCriteria.adults)
        intent.putExtra("children_count", searchCriteria.children.size)
        intent.putExtra("flight_class", searchCriteria.selectedClass.name)
        
        startActivity(intent)
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}
