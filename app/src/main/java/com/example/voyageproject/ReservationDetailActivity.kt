package com.example.voyageproject

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.voyageproject.model.Reservation
import kotlinx.coroutines.launch

class ReservationDetailActivity : AppCompatActivity() {

    private lateinit var reservation: Reservation

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reservation_detail)

        reservation = intent.getSerializableExtra("reservation") as? Reservation ?: run {
            finish()
            return
        }

        Log.d("RESERVATION_DETAIL", "=== AFFICHAGE RESERVATION ===")
        Log.d("RESERVATION_DETAIL", "ID: ${reservation.id}")
        Log.d("RESERVATION_DETAIL", "Type: ${reservation.offerType}")
        Log.d("RESERVATION_DETAIL", "Nom: ${reservation.offerName}")
        Log.d("RESERVATION_DETAIL", "Prix: ${reservation.price}")
        Log.d("RESERVATION_DETAIL", "Adultes: ${reservation.adultsCount}")
        Log.d("RESERVATION_DETAIL", "Enfants: ${reservation.childrenCount}")
        Log.d("RESERVATION_DETAIL", "Âges enfants: ${reservation.childrenAges}")
        Log.d("RESERVATION_DETAIL", "Niveau hôtel: ${reservation.hotelLevel}")
        Log.d("RESERVATION_DETAIL", "Classe vol: ${reservation.flightClass}")
        Log.d("RESERVATION_DETAIL", "Activités: ${reservation.selectedActivities}")
        Log.d("RESERVATION_DETAIL", "Prix détail: ${reservation.priceBreakdown}")

        displayAllDetails()

        findViewById<View>(R.id.btnBack)?.setOnClickListener {
            finish()
        }
        
        setupCancelButton()
    }
    
    private fun displayAllDetails() {
        val offerType = reservation.offerType.lowercase()
        val tvOfferIcon = findViewById<TextView>(R.id.tvDetailOfferIcon)
        val tvOfferType = findViewById<TextView>(R.id.tvDetailOfferType)
        val viewColorBar = findViewById<View>(R.id.viewDetailColorBar)
        
        when (offerType) {
            "hotel" -> {
                tvOfferIcon.text = "🏨"
                tvOfferType.text = "HÔTEL"
                viewColorBar.setBackgroundColor(android.graphics.Color.parseColor("#1976D2"))
            }
            "flight" -> {
                tvOfferIcon.text = "✈️"
                tvOfferType.text = "VOL"
                viewColorBar.setBackgroundColor(android.graphics.Color.parseColor("#F57C00"))
            }
            "circuit" -> {
                tvOfferIcon.text = "🗺️"
                tvOfferType.text = "CIRCUIT"
                viewColorBar.setBackgroundColor(android.graphics.Color.parseColor("#673AB7"))
            }
            else -> {
                tvOfferIcon.text = "📋"
                tvOfferType.text = reservation.offerType.uppercase()
                viewColorBar.setBackgroundColor(android.graphics.Color.parseColor("#757575"))
            }
        }
        
        findViewById<TextView>(R.id.tvDetailOfferName).text = reservation.offerName
        findViewById<TextView>(R.id.tvDetailPrice).text = String.format("%.2f TND", reservation.price)
        
        val statusText = when (reservation.status.lowercase()) {
            "confirmed" -> "✅ Confirmée"
            "pending" -> "⏳ En attente"
            "cancelled" -> "❌ Annulée"
            else -> reservation.status
        }
        val tvStatus = findViewById<TextView>(R.id.tvDetailStatus)
        tvStatus.text = statusText
        
        val statusColor = when (reservation.status.lowercase()) {
            "confirmed" -> android.graphics.Color.parseColor("#4CAF50")
            "pending" -> android.graphics.Color.parseColor("#FF9800")
            "cancelled" -> android.graphics.Color.parseColor("#F44336")
            else -> android.graphics.Color.parseColor("#666666")
        }
        tvStatus.setTextColor(statusColor)
        
        val bookingDate = try {
            reservation.bookingDate.substring(0, 10)
        } catch (e: Exception) {
            reservation.bookingDate
        }
        findViewById<TextView>(R.id.tvDetailBookingDate).text = bookingDate
        findViewById<TextView>(R.id.tvDetailReservationId).text = reservation.id
        findViewById<TextView>(R.id.tvDetailEmail).text = reservation.clientEmail
        
        val paymentMethod = reservation.paymentMethod ?: "Carte bancaire"
        findViewById<TextView>(R.id.tvDetailPaymentMethod).text = when (paymentMethod.lowercase()) {
            "card" -> "Carte bancaire"
            "cash" -> "Espèces"
            "paypal" -> "PayPal"
            else -> "Carte bancaire"
        }
        
        if (offerType == "hotel") {
            displayHotelDetails()
        } else if (offerType == "flight") {
            displayFlightDetails()
        } else if (offerType == "circuit") {
            displayCircuitDetailsComplete()
        }
    }
    
    private fun displayCircuitDetailsComplete() {
        Log.d("CIRCUIT_DETAIL", "=== AFFICHAGE DÉTAILS CIRCUIT ===")
        Log.d("CIRCUIT_DETAIL", "Reservation ID: ${reservation.id}")
        Log.d("CIRCUIT_DETAIL", "Nom: ${reservation.offerName}")
        Log.d("CIRCUIT_DETAIL", "Prix: ${reservation.price}")
        Log.d("CIRCUIT_DETAIL", "Adultes brut: ${reservation.adultsCount}")
        Log.d("CIRCUIT_DETAIL", "Enfants brut: ${reservation.childrenCount}")
        Log.d("CIRCUIT_DETAIL", "Ages brut: ${reservation.childrenAges}")
        Log.d("CIRCUIT_DETAIL", "Hotel brut: ${reservation.hotelLevel}")
        Log.d("CIRCUIT_DETAIL", "Vol brut: ${reservation.flightClass}")
        Log.d("CIRCUIT_DETAIL", "Activites brut: ${reservation.selectedActivities}")
        Log.d("CIRCUIT_DETAIL", "Prix detail brut: ${reservation.priceBreakdown}")
        
        // Afficher les dates du circuit
        val layoutDates = findViewById<LinearLayout>(R.id.layoutDates)
        layoutDates?.visibility = View.VISIBLE
        Log.d("CIRCUIT_DETAIL", "layoutDates rendu visible")
        
        val datesLabel = layoutDates?.getChildAt(0) as? TextView
        datesLabel?.text = "📅 Période du circuit"
        
        val startDate = reservation.startDate
        val endDate = reservation.endDate
        
        if (startDate != null && endDate != null) {
            findViewById<TextView>(R.id.tvStartDate)?.text = startDate.toString()
            findViewById<TextView>(R.id.tvEndDate)?.text = endDate.toString()
        }
        
        // Afficher les voyageurs avec enfants
        val adults = reservation.adultsCount ?: 2
        val children = reservation.childrenCount ?: 0
        
        Log.d("CIRCUIT_DETAIL", "Adultes: $adults, Enfants: $children")
        
        val childrenAges = try {
            if (!reservation.childrenAges.isNullOrEmpty()) {
                com.google.gson.Gson().fromJson(reservation.childrenAges, 
                    object : com.google.gson.reflect.TypeToken<List<Int>>() {}.type) as List<Int>
            } else {
                emptyList()
            }
        } catch (e: Exception) {
            Log.e("CIRCUIT_DETAIL", "Erreur parsing childrenAges: ${e.message}")
            emptyList()
        }
        
        Log.d("CIRCUIT_DETAIL", "Âges enfants parsés: $childrenAges")
        
        val travelersText = buildString {
            append("$adults adulte${if (adults > 1) "s" else ""}")
            if (children > 0) {
                append(", $children enfant${if (children > 1) "s" else ""}\n\n")
                if (childrenAges.isNotEmpty()) {
                    childrenAges.forEachIndexed { index, age ->
                        append("Enfant ${index + 1}: $age ans")
                        when {
                            age in 0..4 -> append(" (gratuit)")
                            age in 5..18 -> append(" (50%)")
                        }
                        if (index < childrenAges.size - 1) append("\n")
                    }
                }
            }
        }
        
        val layoutGuests = findViewById<LinearLayout>(R.id.layoutGuests)
        if (layoutGuests == null) {
            Log.e("CIRCUIT_DETAIL", "❌ layoutGuests est NULL!")
        } else {
            layoutGuests.visibility = View.VISIBLE
            Log.d("CIRCUIT_DETAIL", "✅ layoutGuests rendu visible")
        }
        
        val tvGuests = findViewById<TextView>(R.id.tvGuests)
        if (tvGuests == null) {
            Log.e("CIRCUIT_DETAIL", "❌ tvGuests est NULL!")
        } else {
            tvGuests.text = travelersText
            Log.d("CIRCUIT_DETAIL", "✅ tvGuests texte défini: $travelersText")
        }
        
        Log.d("CIRCUIT_DETAIL", "Voyageurs affiché: $travelersText")
        
        // Formule
        val layoutFormula = findViewById<LinearLayout>(R.id.layoutFormula)
        if (layoutFormula == null) {
            Log.e("CIRCUIT_DETAIL", "❌ layoutFormula est NULL!")
        } else {
            layoutFormula.visibility = View.VISIBLE
            Log.d("CIRCUIT_DETAIL", "✅ layoutFormula rendu visible")
        }
        
        val tvFormula = findViewById<TextView>(R.id.tvFormula)
        if (tvFormula == null) {
            Log.e("CIRCUIT_DETAIL", "❌ tvFormula est NULL!")
        } else {
            tvFormula.text = "Petit-déjeuner inclus"
            Log.d("CIRCUIT_DETAIL", "✅ tvFormula texte défini")
        }
        
        // Niveau d'hôtel
        val hotelLevel = reservation.hotelLevel ?: "STANDARD"
        Log.d("CIRCUIT_DETAIL", "Niveau hôtel brut: ${reservation.hotelLevel}")
        Log.d("CIRCUIT_DETAIL", "Niveau hôtel utilisé: $hotelLevel")
        
        val hotelText = when (hotelLevel.uppercase()) {
            "SUPERIOR" -> "⭐⭐ Supérieur (+200 TND/pers)"
            "LUXURY" -> "⭐⭐⭐ Luxe (+450 TND/pers)"
            else -> "⭐ Standard (inclus)"
        }
        
        Log.d("CIRCUIT_DETAIL", "Texte hôtel affiché: $hotelText")
        
        addInfoLine(layoutFormula, "🏨 Niveau d'hôtel", hotelText, true)
        
        // Classe de vol
        val flightClass = reservation.flightClass ?: "ECONOMY"
        Log.d("CIRCUIT_DETAIL", "Classe vol brute: ${reservation.flightClass}")
        Log.d("CIRCUIT_DETAIL", "Classe vol utilisée: $flightClass")
        
        val flightText = when (flightClass.uppercase()) {
            "BUSINESS" -> "✈️ Business (+400 TND/adulte)"
            "FIRST" -> "✈️ First (+900 TND/adulte)"
            else -> "✈️ Economy (inclus)"
        }
        
        Log.d("CIRCUIT_DETAIL", "Texte vol affiché: $flightText")
        
        addInfoLine(layoutFormula, "✈️ Classe de vol", flightText, false)
        
        // Programme du circuit
        displayCircuitProgram()
        
        // Activités optionnelles
        displayCircuitActivitiesComplete(childrenAges)
        
        // Détail des prix
        val duration = if (startDate != null && endDate != null) {
            java.time.temporal.ChronoUnit.DAYS.between(startDate, endDate).toInt() + 1
        } else {
            7
        }
        
        displayCircuitPriceBreakdownFinal(adults, children, duration, childrenAges)
    }
    
    private fun addInfoLine(afterLayout: LinearLayout?, label: String, value: String, bold: Boolean) {
        try {
            val parent = afterLayout?.parent as? LinearLayout
            val infoLayout = LinearLayout(this).apply {
                orientation = LinearLayout.HORIZONTAL
                setPadding(0, 16, 0, 0)
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
            }
            
            val labelView = TextView(this).apply {
                text = label
                textSize = 14f
                setTextColor(android.graphics.Color.parseColor("#757575"))
                layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
            }
            
            val valueView = TextView(this).apply {
                text = value
                textSize = 14f
                setTextColor(android.graphics.Color.parseColor("#212121"))
                if (bold) setTypeface(null, android.graphics.Typeface.BOLD)
                gravity = android.view.Gravity.END
            }
            
            infoLayout.addView(labelView)
            infoLayout.addView(valueView)
            
            val index = parent?.indexOfChild(afterLayout) ?: -1
            if (index >= 0) parent?.addView(infoLayout, index + 1)
        } catch (e: Exception) {
            Log.e("CIRCUIT_DETAIL", "Erreur ajout ligne: ${e.message}")
        }
    }
    
    private fun displayCircuitProgram() {
        val circuitId = reservation.offerId
        
        lifecycleScope.launch {
            try {
                val repository = com.example.voyageproject.repository.CircuitRepository()
                val response = repository.getCircuitProgram(circuitId)
                
                if (response.isSuccessful) {
                    val days = response.body() ?: emptyList()
                    if (days.isNotEmpty()) {
                        runOnUiThread {
                            displayCircuitProgramUI(days)
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e("CIRCUIT_DETAIL", "Erreur chargement programme: ${e.message}")
            }
        }
    }
    
    private fun displayCircuitProgramUI(days: List<com.example.voyageproject.model.CircuitDay>) {
        val programCard = androidx.cardview.widget.CardView(this).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                setMargins(0, 0, 0, 16)
            }
            radius = 16f
            cardElevation = 6f
        }
        
        val programContent = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(24, 24, 24, 24)
        }
        
        val programTitle = TextView(this).apply {
            text = "📅 Programme du circuit"
            textSize = 16f
            setTextColor(android.graphics.Color.parseColor("#673AB7"))
            setTypeface(null, android.graphics.Typeface.BOLD)
            setPadding(0, 0, 0, 16)
        }
        programContent.addView(programTitle)
        
        days.sortedBy { it.dayNumber }.forEach { day ->
            val dayTitle = TextView(this).apply {
                text = "Jour ${day.dayNumber}: ${day.title}"
                textSize = 14f
                setTextColor(android.graphics.Color.parseColor("#212121"))
                setTypeface(null, android.graphics.Typeface.BOLD)
                setPadding(0, 12, 0, 8)
            }
            programContent.addView(dayTitle)
            
            day.activities?.forEach { activity ->
                val activityText = TextView(this).apply {
                    text = "• $activity"
                    textSize = 13f
                    setTextColor(android.graphics.Color.parseColor("#757575"))
                    setPadding(16, 2, 0, 2)
                }
                programContent.addView(activityText)
            }
        }
        
        programCard.addView(programContent)
        
        val priceBreakdownLayout = findViewById<LinearLayout>(R.id.layoutPriceBreakdown)
        val parent = priceBreakdownLayout?.parent as? LinearLayout
        val insertIndex = parent?.indexOfChild(priceBreakdownLayout) ?: 0
        parent?.addView(programCard, insertIndex.coerceAtLeast(0))
        
        Log.d("CIRCUIT_DETAIL", "Programme ajouté avec ${days.size} jours")
    }
    
    private fun displayCircuitActivitiesComplete(childrenAges: List<Int>) {
        val selectedActivities = try {
            if (!reservation.selectedActivities.isNullOrEmpty()) {
                com.google.gson.Gson().fromJson(reservation.selectedActivities,
                    object : com.google.gson.reflect.TypeToken<List<Map<String, Any>>>() {}.type
                ) as List<Map<String, Any>>
            } else {
                emptyList()
            }
        } catch (e: Exception) {
            Log.e("CIRCUIT_DETAIL", "Erreur parsing activités: ${e.message}")
            emptyList()
        }
        
        if (selectedActivities.isEmpty()) {
            Log.w("CIRCUIT_DETAIL", "Aucune activité optionnelle")
            return
        }
        
        Log.d("CIRCUIT_DETAIL", "Activités trouvées: ${selectedActivities.size}")
        
        val activitiesCard = androidx.cardview.widget.CardView(this).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                setMargins(0, 0, 0, 16)
            }
            radius = 16f
            cardElevation = 6f
        }
        
        val activitiesContent = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(24, 24, 24, 24)
        }
        
        val activitiesTitle = TextView(this).apply {
            text = "🎯 Activités optionnelles"
            textSize = 16f
            setTextColor(android.graphics.Color.parseColor("#673AB7"))
            setTypeface(null, android.graphics.Typeface.BOLD)
            setPadding(0, 0, 0, 16)
        }
        activitiesContent.addView(activitiesTitle)
        
        selectedActivities.forEach { activity ->
            val title = activity["title"] as? String ?: "Activité"
            val price = (activity["price"] as? Double) ?: 0.0
            val adultCount = ((activity["adultCount"] as? Double) ?: 0.0).toInt()
            val childCount = ((activity["childCount"] as? Double) ?: 0.0).toInt()
            
            val adultPrice = price * adultCount
            var childPrice = 0.0
            for (i in 0 until childCount.coerceAtMost(childrenAges.size)) {
                val age = childrenAges[i]
                childPrice += when {
                    age in 0..4 -> 0.0
                    age in 5..18 -> price * 0.5
                    else -> price
                }
            }
            val totalActivityPrice = adultPrice + childPrice
            
            val activityLayout = LinearLayout(this).apply {
                orientation = LinearLayout.VERTICAL
                setPadding(16, 16, 16, 16)
                setBackgroundColor(android.graphics.Color.parseColor("#FFF3E0"))
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply {
                    setMargins(0, 0, 0, 12)
                }
            }
            
            val activityTitle = TextView(this).apply {
                text = title
                textSize = 14f
                setTextColor(android.graphics.Color.parseColor("#212121"))
                setTypeface(null, android.graphics.Typeface.BOLD)
            }
            activityLayout.addView(activityTitle)
            
            val participantsText = buildString {
                if (adultCount > 0) append("👨 $adultCount adulte${if (adultCount > 1) "s" else ""}")
                if (childCount > 0) {
                    if (adultCount > 0) append(", ")
                    append("👶 $childCount enfant${if (childCount > 1) "s" else ""}")
                }
            }
            
            val activityParticipants = TextView(this).apply {
                text = participantsText
                textSize = 12f
                setTextColor(android.graphics.Color.parseColor("#757575"))
                setPadding(0, 4, 0, 4)
            }
            activityLayout.addView(activityParticipants)
            
            val activityPrice = TextView(this).apply {
                text = "💰 ${String.format("%.2f", totalActivityPrice)} TND"
                textSize = 14f
                setTextColor(android.graphics.Color.parseColor("#4CAF50"))
                setTypeface(null, android.graphics.Typeface.BOLD)
            }
            activityLayout.addView(activityPrice)
            
            activitiesContent.addView(activityLayout)
        }
        
        activitiesCard.addView(activitiesContent)
        
        val priceBreakdownLayout = findViewById<LinearLayout>(R.id.layoutPriceBreakdown)
        val parent = priceBreakdownLayout?.parent as? LinearLayout
        val insertIndex = parent?.indexOfChild(priceBreakdownLayout) ?: 0
        parent?.addView(activitiesCard, insertIndex.coerceAtLeast(0))
        
        Log.d("CIRCUIT_DETAIL", "Activités ajoutées: ${selectedActivities.size}")
    }
    
    private fun displayCircuitPriceBreakdownFinal(adults: Int, children: Int, duration: Int, childrenAges: List<Int>) {
        val priceBreakdown = try {
            if (!reservation.priceBreakdown.isNullOrEmpty()) {
                com.google.gson.Gson().fromJson(reservation.priceBreakdown,
                    object : com.google.gson.reflect.TypeToken<Map<String, Double>>() {}.type
                ) as Map<String, Double>
            } else {
                null
            }
        } catch (e: Exception) {
            Log.e("CIRCUIT_DETAIL", "Erreur parsing prix: ${e.message}")
            null
        }
        
        val layoutPriceBreakdown = findViewById<LinearLayout>(R.id.layoutPriceBreakdown)
        if (layoutPriceBreakdown == null) {
            Log.e("CIRCUIT_DETAIL", "❌ layoutPriceBreakdown est NULL!")
        } else {
            layoutPriceBreakdown.visibility = View.VISIBLE
            Log.d("CIRCUIT_DETAIL", "✅ layoutPriceBreakdown rendu visible")
        }
        
        val totalPrice = reservation.price
        val basePrice = priceBreakdown?.get("basePrice") ?: (totalPrice * 0.85)
        val hotelSupplement = priceBreakdown?.get("hotelSupplement") ?: 0.0
        val flightSupplement = priceBreakdown?.get("flightSupplement") ?: 0.0
        val activitiesSupplement = priceBreakdown?.get("activitiesTotal") ?: 0.0
        
        val priceBreakdownCard = layoutPriceBreakdown.getChildAt(0) as? android.view.ViewGroup
        val priceBreakdownContent = priceBreakdownCard?.getChildAt(0) as? LinearLayout
        
        // Modifier le label "Prix de base / nuit" en "Prix de base"
        for (i in 0 until (priceBreakdownContent?.childCount ?: 0)) {
            val child = priceBreakdownContent?.getChildAt(i)
            if (child is LinearLayout) {
                val firstChild = child.getChildAt(0)
                if (firstChild is TextView && firstChild.text.contains("Prix de base")) {
                    firstChild.text = "Prix de base"
                    break
                }
            }
        }
        
        val basePriceText = buildString {
            append("$adults adulte${if (adults > 1) "s" else ""} × 2200 TND")
            if (children > 0 && childrenAges.isNotEmpty()) {
                append("\n+ $children enfant${if (children > 1) "s" else ""}")
                childrenAges.forEach { age ->
                    when {
                        age in 0..4 -> append("\n  ($age ans: gratuit)")
                        age in 5..18 -> append("\n  ($age ans: ${String.format("%.0f", 2200.0 * 0.5)} TND)")
                    }
                }
            }
            append("\n= ${String.format("%.2f", basePrice)} TND")
        }
        
        findViewById<TextView>(R.id.tvPriceBasePerNight)?.text = basePriceText
        
        // Supplément hôtel (utilise layoutViewSupplement)
        if (hotelSupplement > 0) {
            val layoutViewSupplement = findViewById<LinearLayout>(R.id.layoutViewSupplement)
            layoutViewSupplement?.visibility = View.VISIBLE
            
            for (i in 0 until (priceBreakdownContent?.childCount ?: 0)) {
                val child = priceBreakdownContent?.getChildAt(i)
                if (child is LinearLayout && child.id == R.id.layoutViewSupplement) {
                    val label = child.getChildAt(0)
                    if (label is TextView) {
                        label.text = "Supplément hôtel"
                    }
                    break
                }
            }
            
            val hotelSupText = "($adults adulte${if (adults > 1) "s" else ""} × ${String.format("%.0f", hotelSupplement / adults)} TND)"
            findViewById<TextView>(R.id.tvPriceViewSupplement)?.text = String.format("+ %.2f TND\n%s", hotelSupplement, hotelSupText)
        } else {
            findViewById<LinearLayout>(R.id.layoutViewSupplement)?.visibility = View.GONE
        }
        
        // Supplément vol (utilise layoutMealSupplement)
        if (flightSupplement > 0) {
            val layoutMealSupplement = findViewById<LinearLayout>(R.id.layoutMealSupplement)
            layoutMealSupplement?.visibility = View.VISIBLE
            
            for (i in 0 until (priceBreakdownContent?.childCount ?: 0)) {
                val child = priceBreakdownContent?.getChildAt(i)
                if (child is LinearLayout && child.id == R.id.layoutMealSupplement) {
                    val label = child.getChildAt(0)
                    if (label is TextView) {
                        label.text = "Supplément vol"
                    }
                    break
                }
            }
            
            val flightSupText = "($adults adulte${if (adults > 1) "s" else ""} × ${String.format("%.0f", flightSupplement / adults)} TND)"
            findViewById<TextView>(R.id.tvPriceMealSupplement)?.text = String.format("+ %.2f TND\n%s", flightSupplement, flightSupText)
        } else {
            findViewById<LinearLayout>(R.id.layoutMealSupplement)?.visibility = View.GONE
        }
        
        // Activités optionnelles (ajouter dynamiquement après layoutMealSupplement)
        if (activitiesSupplement > 0) {
            // Créer un nouveau layout pour les activités
            val activitiesLayout = LinearLayout(this).apply {
                id = View.generateViewId()
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply {
                    setMargins(0, 0, 0, dpToPx(8))
                }
                orientation = LinearLayout.HORIZONTAL
            }
            
            val activitiesLabel = TextView(this).apply {
                layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
                text = "Activités optionnelles"
                textSize = 14f
                setTextColor(android.graphics.Color.parseColor("#757575"))
            }
            
            val activitiesValue = TextView(this).apply {
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                text = String.format("+ %.2f TND", activitiesSupplement)
                textSize = 14f
                setTextColor(android.graphics.Color.parseColor("#212121"))
            }
            
            activitiesLayout.addView(activitiesLabel)
            activitiesLayout.addView(activitiesValue)
            
            // Insérer après layoutMealSupplement
            val layoutMealSupplement = findViewById<LinearLayout>(R.id.layoutMealSupplement)
            val insertIndex = priceBreakdownContent?.indexOfChild(layoutMealSupplement) ?: -1
            if (insertIndex >= 0) {
                priceBreakdownContent?.addView(activitiesLayout, insertIndex + 1)
            }
        }
        
        // Cacher la ligne "Prix par nuit"
        for (i in 0 until (priceBreakdownContent?.childCount ?: 0)) {
            val child = priceBreakdownContent?.getChildAt(i)
            if (child is LinearLayout) {
                val firstChild = child.getChildAt(0)
                if (firstChild is TextView && firstChild.text.contains("Prix par nuit")) {
                    child.visibility = View.GONE
                    break
                }
            }
        }
        
        // Modifier "Nombre de nuits" en "Durée"
        for (i in 0 until (priceBreakdownContent?.childCount ?: 0)) {
            val child = priceBreakdownContent?.getChildAt(i)
            if (child is LinearLayout) {
                val firstChild = child.getChildAt(0)
                if (firstChild is TextView && firstChild.text.contains("Nombre de nuits")) {
                    firstChild.text = "Durée"
                    break
                }
            }
        }
        
        findViewById<TextView>(R.id.tvNumberOfNights)?.text = "$duration jours / ${duration - 1} nuits"
        findViewById<TextView>(R.id.tvPriceTotal)?.text = String.format("%.2f TND", totalPrice)
    }
    
    private fun dpToPx(dp: Int): Int {
        return (dp * resources.displayMetrics.density).toInt()
    }
    
    private fun displayFlightDetails() {
        // Utiliser les vraies données de la réservation
        val departureDate = reservation.startDate ?: java.time.LocalDate.now().plusDays(7)
        val returnDate = reservation.endDate ?: departureDate.plusDays(8)
        
        // Modifier le label "Dates de séjour" en "Dates de vol"
        val datesLabel = findViewById<LinearLayout>(R.id.layoutDates)?.getChildAt(0) as? TextView
        datesLabel?.text = "🗓️ Dates de vol"
        
        val layoutDates = findViewById<LinearLayout>(R.id.layoutDates)
        layoutDates?.visibility = View.VISIBLE
        
        findViewById<TextView>(R.id.tvStartDate)?.text = departureDate.toString()
        findViewById<TextView>(R.id.tvEndDate)?.text = returnDate.toString()
        
        // Utiliser les vraies données de voyageurs
        val adults = reservation.adultsCount ?: 2
        val children = reservation.childrenCount ?: 0
        
        val layoutGuests = findViewById<LinearLayout>(R.id.layoutGuests)
        layoutGuests?.visibility = View.VISIBLE
        findViewById<TextView>(R.id.tvGuests)?.text = "$adults adulte(s), $children enfant(s)"
        
        // Utiliser la vraie classe de vol si disponible, sinon déduire du nom
        val flightClass = reservation.flightClass ?: when {
            reservation.offerName.contains("Premium", ignoreCase = true) -> "Premium Economy"
            reservation.offerName.contains("Business", ignoreCase = true) -> "Business"
            reservation.offerName.contains("First", ignoreCase = true) -> "First"
            else -> "Economy"
        }
        
        // Modifier le label "Formule" en "Classe"
        val layoutFormula = findViewById<LinearLayout>(R.id.layoutFormula)
        val formulaLabel = layoutFormula?.getChildAt(0) as? TextView
        formulaLabel?.text = "✈️ Classe"
        
        layoutFormula?.visibility = View.VISIBLE
        findViewById<TextView>(R.id.tvFormula)?.text = flightClass
        
        displayFlightPriceBreakdown(adults, children, flightClass)
    }
    
    private fun displayHotelDetails() {
        Log.d("HOTEL_DETAIL", "=== AFFICHAGE DÉTAILS HÔTEL ===")
        Log.d("HOTEL_DETAIL", "Reservation ID: ${reservation.id}")
        Log.d("HOTEL_DETAIL", "Nom: ${reservation.offerName}")
        Log.d("HOTEL_DETAIL", "Prix total: ${reservation.price}")
        Log.d("HOTEL_DETAIL", "startDate brut: ${reservation.startDate}")
        Log.d("HOTEL_DETAIL", "endDate brut: ${reservation.endDate}")
        Log.d("HOTEL_DETAIL", "adultsCount brut: ${reservation.adultsCount}")
        Log.d("HOTEL_DETAIL", "childrenCount brut: ${reservation.childrenCount}")
        Log.d("HOTEL_DETAIL", "formula brut: ${reservation.formula}")
        Log.d("HOTEL_DETAIL", "priceBreakdown brut: ${reservation.priceBreakdown}")
        
        // Utiliser les vraies données de la réservation
        val startDate = reservation.startDate ?: java.time.LocalDate.now().plusDays(7)
        val endDate = reservation.endDate ?: startDate.plusDays(2)
        val nights = java.time.temporal.ChronoUnit.DAYS.between(startDate, endDate).toInt()
        
        Log.d("HOTEL_DETAIL", "Dates calculées: $startDate -> $endDate ($nights nuits)")
        
        val layoutDates = findViewById<LinearLayout>(R.id.layoutDates)
        layoutDates?.visibility = View.VISIBLE
        
        findViewById<TextView>(R.id.tvStartDate)?.text = startDate.toString()
        findViewById<TextView>(R.id.tvEndDate)?.text = endDate.toString()
        
        // Utiliser les vraies données de voyageurs
        val adults = reservation.adultsCount ?: 2
        val children = reservation.childrenCount ?: 0
        
        Log.d("HOTEL_DETAIL", "Voyageurs: $adults adultes, $children enfants")
        
        val layoutGuests = findViewById<LinearLayout>(R.id.layoutGuests)
        layoutGuests?.visibility = View.VISIBLE
        findViewById<TextView>(R.id.tvGuests)?.text = "$adults adulte(s), $children enfant(s)"
        
        val formula = reservation.formula ?: "petit_dejeuner"
        
        Log.d("HOTEL_DETAIL", "Formule brute: ${reservation.formula}")
        Log.d("HOTEL_DETAIL", "Formule utilisée: $formula")
        
        val layoutFormula = findViewById<LinearLayout>(R.id.layoutFormula)
        layoutFormula?.visibility = View.VISIBLE
        
        val formulaText = when (formula.lowercase()) {
            "petit_dejeuner" -> "Petit déjeuner"
            "demi_pension" -> "Demi-pension"
            "pension_complete" -> "Pension complète"
            "all_inclusive" -> "All inclusive"
            else -> {
                Log.w("HOTEL_DETAIL", "Formule inconnue: $formula, utilisation par défaut")
                "Petit déjeuner"
            }
        }
        findViewById<TextView>(R.id.tvFormula)?.text = formulaText
        
        Log.d("HOTEL_DETAIL", "Formule affichée: $formulaText")
        
        displayPriceBreakdown(nights, formula)
    }
    
    private fun displayPriceBreakdown(nights: Int, formula: String) {
        val layoutPriceBreakdown = findViewById<LinearLayout>(R.id.layoutPriceBreakdown)
        layoutPriceBreakdown?.visibility = View.VISIBLE
        
        val totalPrice = reservation.price
        val adults = reservation.adultsCount ?: 2
        val children = reservation.childrenCount ?: 0
        
        // Essayer d'utiliser le priceBreakdown de la réservation si disponible
        val priceBreakdown = try {
            if (!reservation.priceBreakdown.isNullOrEmpty()) {
                com.google.gson.Gson().fromJson(reservation.priceBreakdown,
                    object : com.google.gson.reflect.TypeToken<Map<String, Double>>() {}.type
                ) as Map<String, Double>
            } else {
                null
            }
        } catch (e: Exception) {
            Log.e("HOTEL_DETAIL", "Erreur parsing prix: ${e.message}")
            null
        }
        
        Log.d("HOTEL_DETAIL", "=== DÉTAIL PRIX HÔTEL ===")
        Log.d("HOTEL_DETAIL", "Prix total: $totalPrice, Nuits: $nights, Voyageurs: $adults+$children")
        
        // Calculer le prix par nuit
        val pricePerNight = if (nights > 0) totalPrice / nights else totalPrice
        
        // Extraire les composants du prix
        val basePrice: Double
        val viewSupplement: Double
        val mealSupplement: Double
        
        if (priceBreakdown != null && priceBreakdown.isNotEmpty()) {
            basePrice = priceBreakdown["basePrice"] ?: 0.0
            viewSupplement = priceBreakdown["viewSupplement"] ?: 0.0
            mealSupplement = priceBreakdown["mealSupplement"] ?: 0.0
        } else {
            mealSupplement = when (formula.lowercase()) {
                "petit_dejeuner" -> 15.0
                "demi_pension" -> 35.0
                "pension_complete" -> 55.0
                "all_inclusive" -> 80.0
                else -> 0.0
            }
            viewSupplement = 0.0
            basePrice = pricePerNight - mealSupplement - viewSupplement
        }
        
        // 1. Prix de base avec description complète
        val basePriceText = buildString {
            append("${String.format("%.2f", basePrice)} TND\n")
            append("Prix de la chambre par nuit\n")
            append("pour $adults adulte${if (adults > 1) "s" else ""}")
            if (children > 0) {
                append(" + $children enfant${if (children > 1) "s" else ""}")
            }
        }
        findViewById<TextView>(R.id.tvPriceBasePerNight)?.text = basePriceText
        
        // 2. Supplément vue avec description
        if (viewSupplement > 0) {
            findViewById<LinearLayout>(R.id.layoutViewSupplement)?.visibility = View.VISIBLE
            val viewText = buildString {
                append("+ ${String.format("%.2f", viewSupplement)} TND\n")
                append("Supplément vue par nuit")
            }
            findViewById<TextView>(R.id.tvPriceViewSupplement)?.text = viewText
        } else {
            findViewById<LinearLayout>(R.id.layoutViewSupplement)?.visibility = View.GONE
        }
        
        // 3. Supplément repas avec description détaillée
        if (mealSupplement > 0) {
            findViewById<LinearLayout>(R.id.layoutMealSupplement)?.visibility = View.VISIBLE
            
            val formulaName = when (formula.lowercase()) {
                "petit_dejeuner" -> "Petit déjeuner"
                "demi_pension" -> "Demi-pension"
                "pension_complete" -> "Pension complète"
                "all_inclusive" -> "All inclusive"
                else -> "Repas"
            }
            
            // Calculer le prix par personne en tenant compte des enfants
            // Pour les hôtels : enfants 0-12 ans gratuits, 12-18 ans moitié prix
            // Comme nous n'avons pas les âges exacts, on suppose que les enfants < 12 ans
            val mealText = buildString {
                append("+ ${String.format("%.2f", mealSupplement)} TND\n")
                append("$formulaName par nuit\n")
                
                if (children > 0) {
                    // Supposer que les enfants sont < 12 ans (gratuits pour les repas)
                    val payingGuests = adults // Seuls les adultes paient
                    if (payingGuests > 0) {
                        val perAdult = mealSupplement / payingGuests
                        append("(${String.format("%.2f", perAdult)} TND × $adults adulte${if (adults > 1) "s" else ""})\n")
                        append("Enfants : gratuit")
                    }
                } else {
                    // Pas d'enfants, calcul simple
                    if (adults > 0) {
                        val perPerson = mealSupplement / adults
                        append("(${String.format("%.2f", perPerson)} TND × $adults pers.)")
                    }
                }
            }
            findViewById<TextView>(R.id.tvPriceMealSupplement)?.text = mealText
        } else {
            findViewById<LinearLayout>(R.id.layoutMealSupplement)?.visibility = View.GONE
        }
        
        // 4. Prix par nuit (sous-total) avec décomposition
        val pricePerNightText = buildString {
            append("= ${String.format("%.2f", pricePerNight)} TND\n")
            append("Sous-total par nuit\n")
            append("(")
            val parts = mutableListOf<String>()
            if (basePrice > 0) parts.add("${String.format("%.0f", basePrice)}")
            if (viewSupplement > 0) parts.add("${String.format("%.0f", viewSupplement)}")
            if (mealSupplement > 0) parts.add("${String.format("%.0f", mealSupplement)}")
            append(parts.joinToString(" + "))
            append(")")
        }
        findViewById<TextView>(R.id.tvPricePerNight)?.text = pricePerNightText
        
        // 5. Nombre de nuits avec description
        val nightsText = buildString {
            append("× $nights nuit${if (nights > 1) "s" else ""}\n")
            append("Durée du séjour")
        }
        findViewById<TextView>(R.id.tvNumberOfNights)?.text = nightsText
        
        // 6. Prix total - afficher uniquement le montant
        findViewById<TextView>(R.id.tvPriceTotal)?.text = String.format("%.2f TND", totalPrice)
    }
    
    private fun displayFlightPriceBreakdown(adults: Int, children: Int, flightClass: String) {
        val layoutPriceBreakdown = findViewById<LinearLayout>(R.id.layoutPriceBreakdown)
        layoutPriceBreakdown?.visibility = View.VISIBLE
        
        val totalPrice = reservation.price
        
        val classMultiplier = when (flightClass) {
            "Economy" -> 1.0
            "Premium Economy" -> 1.3
            "Business" -> 1.7
            "First" -> 2.5
            else -> 1.0
        }
        
        val totalPersons = adults + (children * 0.5)
        val basePricePerPerson = totalPrice / (totalPersons * classMultiplier)
        val pricePerAdult = basePricePerPerson * classMultiplier
        val pricePerChild = pricePerAdult * 0.5
        
        // Modifier le label "Prix de base / nuit" en "Prix de base"
        val priceBreakdownCard = layoutPriceBreakdown.getChildAt(0) as? android.view.ViewGroup
        val priceBreakdownContent = priceBreakdownCard?.getChildAt(0) as? LinearLayout
        
        // Trouver et modifier le label "Prix de base / nuit"
        for (i in 0 until (priceBreakdownContent?.childCount ?: 0)) {
            val child = priceBreakdownContent?.getChildAt(i)
            if (child is LinearLayout) {
                val firstChild = child.getChildAt(0)
                if (firstChild is TextView && firstChild.text.contains("Prix de base")) {
                    firstChild.text = "Prix de base"
                    break
                }
            }
        }
        
        findViewById<TextView>(R.id.tvPriceBasePerNight)?.text = String.format("%.2f TND/personne", basePricePerPerson)
        
        if (classMultiplier > 1.0) {
            val layoutViewSupplement = findViewById<LinearLayout>(R.id.layoutViewSupplement)
            layoutViewSupplement?.visibility = View.VISIBLE
            findViewById<TextView>(R.id.tvPriceViewSupplement)?.text = "Classe $flightClass (×$classMultiplier)"
        } else {
            findViewById<LinearLayout>(R.id.layoutViewSupplement)?.visibility = View.GONE
        }
        
        // Modifier le label "Supplément repas" en "Détail passagers"
        val layoutMealSupplement = findViewById<LinearLayout>(R.id.layoutMealSupplement)
        for (i in 0 until (priceBreakdownContent?.childCount ?: 0)) {
            val child = priceBreakdownContent?.getChildAt(i)
            if (child is LinearLayout && child.id == R.id.layoutMealSupplement) {
                val label = child.getChildAt(0)
                if (label is TextView) {
                    label.text = "Détail passagers"
                }
                break
            }
        }
        
        layoutMealSupplement?.visibility = View.VISIBLE
        
        val adultTotal = pricePerAdult * adults
        val childTotal = pricePerChild * children
        
        val passengersDetail = StringBuilder()
        passengersDetail.append("$adults adulte(s) × ${String.format("%.2f", pricePerAdult)} TND\n")
        passengersDetail.append("= ${String.format("%.2f", adultTotal)} TND\n\n")
        if (children > 0) {
            passengersDetail.append("$children enfant(s) × ${String.format("%.2f", pricePerChild)} TND (50%)\n")
            passengersDetail.append("= ${String.format("%.2f", childTotal)} TND")
        }
        
        findViewById<TextView>(R.id.tvPriceMealSupplement)?.text = passengersDetail.toString().trim()
        
        // Cacher complètement la ligne "Prix par nuit"
        for (i in 0 until (priceBreakdownContent?.childCount ?: 0)) {
            val child = priceBreakdownContent?.getChildAt(i)
            if (child is LinearLayout) {
                val firstChild = child.getChildAt(0)
                if (firstChild is TextView && firstChild.text.contains("Prix par nuit")) {
                    child.visibility = View.GONE
                    break
                }
            }
        }
        
        // Modifier "Nombre de nuits" en "Total passagers"
        for (i in 0 until (priceBreakdownContent?.childCount ?: 0)) {
            val child = priceBreakdownContent?.getChildAt(i)
            if (child is LinearLayout) {
                val firstChild = child.getChildAt(0)
                if (firstChild is TextView && firstChild.text.contains("Nombre de nuits")) {
                    firstChild.text = "Total passagers"
                    break
                }
            }
        }
        
        findViewById<TextView>(R.id.tvNumberOfNights)?.text = "${adults + children} passager(s)"
        findViewById<TextView>(R.id.tvPriceTotal)?.text = String.format("%.2f TND", totalPrice)
    }
    
    private fun setupCancelButton() {
        val btnCancel = findViewById<android.widget.Button>(R.id.btnCancelReservation)
        
        // Afficher le bouton seulement si la réservation est confirmée
        if (reservation.status.uppercase() == "CONFIRMED") {
            btnCancel?.visibility = View.VISIBLE
            btnCancel?.setOnClickListener {
                showCancellationConfirmDialog()
            }
        } else {
            btnCancel?.visibility = View.GONE
        }
    }
    
    private fun showCancellationConfirmDialog() {
        android.app.AlertDialog.Builder(this)
            .setTitle("Annuler la réservation")
            .setMessage("Êtes-vous sûr de vouloir annuler cette réservation ?\n\nLes conditions d'annulation s'appliqueront selon le délai restant.")
            .setPositiveButton("Oui, annuler") { _, _ ->
                cancelReservation()
            }
            .setNegativeButton("Non", null)
            .show()
    }
    
    private fun cancelReservation() {
        lifecycleScope.launch {
            try {
                val repository = com.example.voyageproject.repository.ReservationRepository()
                val response = repository.cancelReservation(reservation.id)
                
                if (response.isSuccessful) {
                    val result = response.body()
                    if (result?.success == true) {
                        // Annulation réussie
                        showCancellationSuccessDialog(result)
                    } else {
                        // Annulation refusée
                        showCancellationErrorDialog(result?.error ?: "Annulation impossible")
                    }
                } else {
                    showCancellationErrorDialog("Erreur serveur: ${response.code()}")
                }
            } catch (e: Exception) {
                Log.e("CANCEL_RESERVATION", "Erreur: ${e.message}")
                showCancellationErrorDialog("Erreur: ${e.message}")
            }
        }
    }
    
    private fun showCancellationSuccessDialog(result: com.example.voyageproject.model.CancellationResponse) {
        val message = buildString {
            append("✅ Réservation annulée avec succès\n\n")
            append("${result.offerName}\n\n")
            
            if (result.penaltyAmount != null && result.penaltyAmount > 0) {
                append("⚠️ PÉNALITÉ D'ANNULATION\n\n")
                append("Prix original: ${String.format("%.2f", result.originalPrice)} TND\n")
                append("Pénalité (${String.format("%.0f", 100 - (result.refundPercentage ?: 0.0))}%): ${String.format("%.2f", result.penaltyAmount)} TND\n")
                append("Montant remboursé: ${String.format("%.2f", result.refundAmount)} TND\n\n")
            } else {
                append("Remboursement intégral: ${String.format("%.2f", result.refundAmount)} TND\n\n")
            }
            
            append(result.penaltyMessage ?: "")
        }
        
        android.app.AlertDialog.Builder(this)
            .setTitle("Annulation confirmée")
            .setMessage(message)
            .setPositiveButton("OK") { _, _ ->
                // Retourner à l'historique
                finish()
            }
            .setCancelable(false)
            .show()
    }
    
    private fun showCancellationErrorDialog(error: String) {
        android.app.AlertDialog.Builder(this)
            .setTitle("❌ Annulation impossible")
            .setMessage(error)
            .setPositiveButton("OK", null)
            .show()
    }
}
