package com.example.voyageproject.ui.payment

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.example.voyageproject.R
import com.example.voyageproject.model.ChildInfo
import com.example.voyageproject.ui.main.MainActivity
import java.text.SimpleDateFormat
import java.util.*

class PaymentConfirmationActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payment_confirmation)

        setupViews()
        displayPaymentDetails()
    }

    private fun setupViews() {
        findViewById<ImageButton>(R.id.btnClose).setOnClickListener {
            navigateToHome()
        }

        findViewById<Button>(R.id.btnViewReservations).setOnClickListener {
            navigateToHistory()
        }

        findViewById<Button>(R.id.btnBackHome).setOnClickListener {
            navigateToHome()
        }
    }

    private fun displayPaymentDetails() {
        val offerType = intent.getStringExtra("offerType") ?: ""
        val offerName = intent.getStringExtra("offerName") ?: ""
        val totalPrice = intent.getDoubleExtra("totalPrice", 0.0)
        val transactionId = "TXN-${System.currentTimeMillis()}"
        val reservationId = "RES-${System.currentTimeMillis()}"

        // Informations générales
        findViewById<TextView>(R.id.tvOfferName).text = offerName
        findViewById<TextView>(R.id.tvTransactionId).text = transactionId
        findViewById<TextView>(R.id.tvReservationId).text = reservationId
        findViewById<TextView>(R.id.tvPaymentDate).text = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date())
        findViewById<TextView>(R.id.tvTotalPrice).text = String.format("%.2f TND", totalPrice)

        when (offerType) {
            "circuit" -> displayCircuitDetails()
            "hotel" -> displayHotelDetails()
            "flight" -> displayFlightDetails()
        }
    }

    private fun displayCircuitDetails() {
        val adults = intent.getIntExtra("adults", 0)
        val childrenAgesList = intent.getIntegerArrayListExtra("childrenAges") ?: arrayListOf()
        val hotelLevel = intent.getStringExtra("hotelLevel") ?: "STANDARD"
        val flightClass = intent.getStringExtra("flightClass") ?: "ECONOMY"
        val duration = intent.getIntExtra("circuitDuration", 0)
        val destination = intent.getStringExtra("circuitDestination") ?: ""
        val departureDate = Date(intent.getLongExtra("departureDate", 0L))

        // Période
        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val calendar = Calendar.getInstance()
        calendar.time = departureDate
        calendar.add(Calendar.DAY_OF_YEAR, duration - 1)
        val endDate = calendar.time

        findViewById<TextView>(R.id.tvPeriod).text = "Du ${sdf.format(departureDate)} au ${sdf.format(endDate)}"
        findViewById<TextView>(R.id.tvDestination).text = destination
        findViewById<TextView>(R.id.tvDuration).text = "$duration jours / ${duration - 1} nuits"

        // Voyageurs
        val travelersText = buildString {
            append("$adults adulte${if (adults > 1) "s" else ""}")
            if (childrenAgesList.isNotEmpty()) {
                append(", ${childrenAgesList.size} enfant${if (childrenAgesList.size > 1) "s" else ""}\n")
                childrenAgesList.forEachIndexed { index, age ->
                    append("Enfant ${index + 1}: $age ans")
                    when {
                        age in 0..4 -> append(" (gratuit)")
                        age in 5..18 -> append(" (50%)")
                    }
                    if (index < childrenAgesList.size - 1) append("\n")
                }
            }
        }
        findViewById<TextView>(R.id.tvTravelers).text = travelersText

        // Hébergement
        val hotelText = when (hotelLevel) {
            "STANDARD" -> "⭐ Standard (inclus)"
            "SUPERIOR" -> "⭐⭐ Supérieur (+200 TND/adulte)"
            "LUXURY" -> "⭐⭐⭐ Luxe (+450 TND/adulte)"
            else -> "Standard"
        }
        findViewById<TextView>(R.id.tvHotelLevel).text = hotelText

        // Vol
        val flightText = when (flightClass) {
            "ECONOMY" -> "Economy (inclus)"
            "BUSINESS" -> "Business (+400 TND/adulte)"
            "FIRST" -> "First (+900 TND/adulte)"
            else -> "Economy"
        }
        findViewById<TextView>(R.id.tvFlightClass).text = flightText

        // Détail des prix
        displayPriceBreakdown()
    }

    private fun displayPriceBreakdown() {
        val basePrice = intent.getDoubleExtra("basePrice", 0.0)
        val hotelSupplement = intent.getDoubleExtra("hotelSupplement", 0.0)
        val flightSupplement = intent.getDoubleExtra("flightSupplement", 0.0)
        val activitiesTotal = intent.getDoubleExtra("activitiesTotal", 0.0)

        val layoutPriceDetails = findViewById<LinearLayout>(R.id.layoutPriceDetails)
        layoutPriceDetails.removeAllViews()

        // Prix de base
        addPriceLine(layoutPriceDetails, "Prix de base", basePrice, false)

        // Suppléments
        if (hotelSupplement > 0) {
            addPriceLine(layoutPriceDetails, "Supplément hôtel", hotelSupplement, false)
        }

        if (flightSupplement > 0) {
            addPriceLine(layoutPriceDetails, "Supplément vol", flightSupplement, false)
        }

        if (activitiesTotal > 0) {
            addPriceLine(layoutPriceDetails, "Activités optionnelles", activitiesTotal, false)
        }

        // Séparateur
        val separator = View(this)
        separator.layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            2
        ).apply {
            setMargins(0, 16, 0, 16)
        }
        separator.setBackgroundColor(android.graphics.Color.parseColor("#673AB7"))
        layoutPriceDetails.addView(separator)

        // Total
        val totalPrice = intent.getDoubleExtra("totalPrice", 0.0)
        addPriceLine(layoutPriceDetails, "PRIX TOTAL", totalPrice, true)
    }

    private fun addPriceLine(container: LinearLayout, label: String, price: Double, isTotal: Boolean) {
        val lineLayout = LinearLayout(this).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                setMargins(0, 0, 0, 8)
            }
            orientation = LinearLayout.HORIZONTAL
        }

        val labelView = TextView(this).apply {
            layoutParams = LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                1f
            )
            text = label
            textSize = if (isTotal) 18f else 14f
            setTextColor(android.graphics.Color.parseColor(if (isTotal) "#212121" else "#757575"))
            if (isTotal) {
                setTypeface(null, android.graphics.Typeface.BOLD)
            }
        }

        val priceView = TextView(this).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            text = if (isTotal) {
                String.format("%.2f TND", price)
            } else {
                String.format("+ %.2f TND", price)
            }
            textSize = if (isTotal) 24f else 14f
            setTextColor(android.graphics.Color.parseColor(if (isTotal) "#4CAF50" else "#212121"))
            if (isTotal) {
                setTypeface(null, android.graphics.Typeface.BOLD)
            }
        }

        lineLayout.addView(labelView)
        lineLayout.addView(priceView)
        container.addView(lineLayout)
    }

    private fun displayHotelDetails() {
        // À implémenter si nécessaire
    }

    private fun displayFlightDetails() {
        // À implémenter si nécessaire
    }

    private fun navigateToHistory() {
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra("navigate_to", "history")
        }
        startActivity(intent)
        finish()
    }

    private fun navigateToHome() {
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        startActivity(intent)
        finish()
    }

    override fun onBackPressed() {
        navigateToHome()
    }
}
