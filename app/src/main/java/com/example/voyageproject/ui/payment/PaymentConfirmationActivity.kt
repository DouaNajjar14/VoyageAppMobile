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

        // Cacher tous les champs par défaut
        findViewById<TextView>(R.id.tvDestination)?.visibility = View.GONE
        findViewById<TextView>(R.id.tvDuration)?.visibility = View.GONE
        findViewById<TextView>(R.id.tvPeriod)?.visibility = View.GONE
        findViewById<TextView>(R.id.tvTravelers)?.visibility = View.GONE
        findViewById<TextView>(R.id.tvHotelLevelLabel)?.visibility = View.GONE
        findViewById<TextView>(R.id.tvHotelLevel)?.visibility = View.GONE
        findViewById<TextView>(R.id.tvFlightClassLabel)?.visibility = View.GONE
        findViewById<TextView>(R.id.tvFlightClass)?.visibility = View.GONE

        // Afficher uniquement les champs pertinents selon le type
        when (offerType.lowercase()) {
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

        // Destination - VISIBLE
        findViewById<TextView>(R.id.tvDestination)?.apply {
            visibility = View.VISIBLE
            text = destination
        }

        // Durée - VISIBLE
        findViewById<TextView>(R.id.tvDuration)?.apply {
            visibility = View.VISIBLE
            text = "$duration jours / ${duration - 1} nuits"
        }

        // Période - VISIBLE
        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val calendar = Calendar.getInstance()
        calendar.time = departureDate
        calendar.add(Calendar.DAY_OF_YEAR, duration - 1)
        val endDate = calendar.time
        findViewById<TextView>(R.id.tvPeriod)?.apply {
            visibility = View.VISIBLE
            text = "Du ${sdf.format(departureDate)} au ${sdf.format(endDate)}"
        }

        // Voyageurs - VISIBLE
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
        findViewById<TextView>(R.id.tvTravelers)?.apply {
            visibility = View.VISIBLE
            text = travelersText
        }

        // Hébergement - VISIBLE avec label
        val hotelText = when (hotelLevel) {
            "STANDARD" -> "⭐ Standard (inclus)"
            "SUPERIOR" -> "⭐⭐ Supérieur (+200 TND/adulte)"
            "LUXURY" -> "⭐⭐⭐ Luxe (+450 TND/adulte)"
            else -> "Standard"
        }
        findViewById<TextView>(R.id.tvHotelLevelLabel)?.visibility = View.VISIBLE
        findViewById<TextView>(R.id.tvHotelLevel)?.apply {
            visibility = View.VISIBLE
            text = hotelText
        }

        // Vol - VISIBLE avec label
        val flightText = when (flightClass) {
            "ECONOMY" -> "Economy (inclus)"
            "BUSINESS" -> "Business (+400 TND/adulte)"
            "FIRST" -> "First (+900 TND/adulte)"
            else -> "Economy"
        }
        findViewById<TextView>(R.id.tvFlightClassLabel)?.visibility = View.VISIBLE
        findViewById<TextView>(R.id.tvFlightClass)?.apply {
            visibility = View.VISIBLE
            text = flightText
        }

        // Détail des prix
        displayPriceBreakdown()
    }

    private fun displayPriceBreakdown() {
        val basePrice = intent.getDoubleExtra("basePrice", 0.0)
        val hotelSupplement = intent.getDoubleExtra("hotelSupplement", 0.0)
        val flightSupplement = intent.getDoubleExtra("flightSupplement", 0.0)
        val activitiesTotal = intent.getDoubleExtra("activitiesTotal", 0.0)

        val layoutPriceDetails = findViewById<LinearLayout>(R.id.layoutPriceDetails)
        layoutPriceDetails?.removeAllViews()

        // Prix de base
        if (basePrice > 0) {
            addPriceLine(layoutPriceDetails, "Prix de base", basePrice, false)
        }

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
        layoutPriceDetails?.addView(separator)

        // Total
        val totalPrice = intent.getDoubleExtra("totalPrice", 0.0)
        addPriceLine(layoutPriceDetails, "PRIX TOTAL", totalPrice, true)
    }

    private fun displayHotelPriceBreakdown(numberOfNights: Int, mealPlan: String) {
        val totalPrice = intent.getDoubleExtra("totalPrice", 0.0)
        
        val layoutPriceDetails = findViewById<LinearLayout>(R.id.layoutPriceDetails)
        layoutPriceDetails?.removeAllViews()

        // Calculer le prix par nuit
        val pricePerNight = if (numberOfNights > 0) totalPrice / numberOfNights else totalPrice

        // Prix par nuit
        addPriceLine(layoutPriceDetails, "Prix par nuit", pricePerNight, false)

        // Nombre de nuits
        addPriceLine(layoutPriceDetails, "× $numberOfNights nuit${if (numberOfNights > 1) "s" else ""}", 0.0, false, showPrice = false)

        // Séparateur
        val separator = View(this)
        separator.layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            2
        ).apply {
            setMargins(0, 16, 0, 16)
        }
        separator.setBackgroundColor(android.graphics.Color.parseColor("#1976D2"))
        layoutPriceDetails?.addView(separator)

        // Total
        addPriceLine(layoutPriceDetails, "PRIX TOTAL", totalPrice, true)
    }

    private fun displayFlightPriceBreakdown(adultsCount: Int, childrenCount: Int, flightClass: String) {
        val totalPrice = intent.getDoubleExtra("totalPrice", 0.0)
        
        val layoutPriceDetails = findViewById<LinearLayout>(R.id.layoutPriceDetails)
        layoutPriceDetails?.removeAllViews()

        // Calculer le prix par personne (estimation)
        val totalPassengers = adultsCount + (childrenCount * 0.5)
        val pricePerAdult = if (totalPassengers > 0) totalPrice / totalPassengers else totalPrice
        val pricePerChild = pricePerAdult * 0.5

        // Prix adultes
        if (adultsCount > 0) {
            addPriceLine(layoutPriceDetails, "$adultsCount adulte${if (adultsCount > 1) "s" else ""}", pricePerAdult * adultsCount, false)
        }

        // Prix enfants
        if (childrenCount > 0) {
            addPriceLine(layoutPriceDetails, "$childrenCount enfant${if (childrenCount > 1) "s" else ""} (50%)", pricePerChild * childrenCount, false)
        }

        // Séparateur
        val separator = View(this)
        separator.layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            2
        ).apply {
            setMargins(0, 16, 0, 16)
        }
        separator.setBackgroundColor(android.graphics.Color.parseColor("#F57C00"))
        layoutPriceDetails?.addView(separator)

        // Total
        addPriceLine(layoutPriceDetails, "PRIX TOTAL", totalPrice, true)
    }

    private fun addPriceLine(container: LinearLayout?, label: String, price: Double, isTotal: Boolean, showPrice: Boolean = true) {
        if (container == null) return
        
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

        lineLayout.addView(labelView)

        if (showPrice) {
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
            lineLayout.addView(priceView)
        }

        container.addView(lineLayout)
    }

    private fun displayHotelDetails() {
        val checkInDate = intent.getStringExtra("checkInDate") ?: ""
        val checkOutDate = intent.getStringExtra("checkOutDate") ?: ""
        val numberOfNights = intent.getIntExtra("numberOfNights", 0)
        val numberOfAdults = intent.getIntExtra("numberOfAdults", 0)
        val numberOfChildren = intent.getIntExtra("numberOfChildren", 0)
        val roomType = intent.getStringExtra("roomType") ?: ""
        val viewType = intent.getStringExtra("viewType") ?: ""
        val mealPlan = intent.getStringExtra("mealPlan") ?: ""

        // Destination - CACHÉ pour hôtel
        findViewById<TextView>(R.id.tvDestination)?.visibility = View.GONE

        // Durée - VISIBLE
        findViewById<TextView>(R.id.tvDuration)?.apply {
            visibility = View.VISIBLE
            text = "$numberOfNights nuit${if (numberOfNights > 1) "s" else ""}"
        }

        // Période - VISIBLE
        findViewById<TextView>(R.id.tvPeriod)?.apply {
            visibility = View.VISIBLE
            text = "Du $checkInDate au $checkOutDate"
        }

        // Voyageurs - VISIBLE
        val travelersText = buildString {
            append("$numberOfAdults adulte${if (numberOfAdults > 1) "s" else ""}")
            if (numberOfChildren > 0) {
                append(", $numberOfChildren enfant${if (numberOfChildren > 1) "s" else ""}")
            }
        }
        findViewById<TextView>(R.id.tvTravelers)?.apply {
            visibility = View.VISIBLE
            text = travelersText
        }

        // Type de chambre - VISIBLE (utilise tvHotelLevel)
        findViewById<TextView>(R.id.tvHotelLevelLabel)?.apply {
            visibility = View.VISIBLE
            text = "🛏️ Chambre"
        }
        findViewById<TextView>(R.id.tvHotelLevel)?.apply {
            visibility = View.VISIBLE
            text = if (roomType.isNotEmpty()) roomType else "Standard"
        }

        // Options - VISIBLE (utilise tvFlightClass)
        val optionsText = buildString {
            if (viewType.isNotEmpty()) {
                append("👁️ Vue: $viewType")
            }
            if (mealPlan.isNotEmpty()) {
                if (viewType.isNotEmpty()) append("\n")
                val mealText = when (mealPlan) {
                    "petit_dejeuner" -> "Petit déjeuner"
                    "demi_pension" -> "Demi-pension"
                    "pension_complete" -> "Pension complète"
                    "all_inclusive" -> "All inclusive"
                    else -> mealPlan
                }
                append("🍽️ Formule: $mealText")
            }
        }
        if (optionsText.isNotEmpty()) {
            findViewById<TextView>(R.id.tvFlightClassLabel)?.apply {
                visibility = View.VISIBLE
                text = "📋 Options"
            }
            findViewById<TextView>(R.id.tvFlightClass)?.apply {
                visibility = View.VISIBLE
                text = optionsText.trim()
            }
        }

        // Détail des prix pour hôtel
        displayHotelPriceBreakdown(numberOfNights, mealPlan)
    }

    private fun displayFlightDetails() {
        val departureDate = intent.getStringExtra("departureDate") ?: ""
        val returnDate = intent.getStringExtra("returnDate") ?: ""
        val adultsCount = intent.getIntExtra("adultsCount", 0)
        val childrenCount = intent.getIntExtra("childrenCount", 0)
        val flightClass = intent.getStringExtra("flightClass") ?: ""

        // Destination - CACHÉ pour vol
        findViewById<TextView>(R.id.tvDestination)?.visibility = View.GONE

        // Durée - CACHÉ pour vol
        findViewById<TextView>(R.id.tvDuration)?.visibility = View.GONE

        // Dates - VISIBLE
        val datesText = buildString {
            if (departureDate.isNotEmpty()) {
                append("✈️ Départ: $departureDate")
            }
            if (returnDate.isNotEmpty()) {
                if (departureDate.isNotEmpty()) append("\n")
                append("🔙 Retour: $returnDate")
            }
        }
        findViewById<TextView>(R.id.tvPeriod)?.apply {
            visibility = View.VISIBLE
            text = datesText
        }

        // Voyageurs - VISIBLE
        val travelersText = buildString {
            append("$adultsCount adulte${if (adultsCount > 1) "s" else ""}")
            if (childrenCount > 0) {
                append(", $childrenCount enfant${if (childrenCount > 1) "s" else ""}")
            }
        }
        findViewById<TextView>(R.id.tvTravelers)?.apply {
            visibility = View.VISIBLE
            text = travelersText
        }

        // Hôtel - CACHÉ pour vol
        findViewById<TextView>(R.id.tvHotelLevelLabel)?.visibility = View.GONE
        findViewById<TextView>(R.id.tvHotelLevel)?.visibility = View.GONE

        // Classe de vol - VISIBLE
        if (flightClass.isNotEmpty()) {
            findViewById<TextView>(R.id.tvFlightClassLabel)?.visibility = View.VISIBLE
            findViewById<TextView>(R.id.tvFlightClass)?.apply {
                visibility = View.VISIBLE
                text = flightClass
            }
        }

        // Détail des prix pour vol
        displayFlightPriceBreakdown(adultsCount, childrenCount, flightClass)
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
