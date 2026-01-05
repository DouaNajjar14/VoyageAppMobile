package com.example.voyageproject.ui.circuit

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.lifecycle.lifecycleScope
import com.example.voyageproject.R
import com.example.voyageproject.model.ChildInfo
import com.example.voyageproject.model.Circuit
import com.example.voyageproject.model.CircuitActivity
import com.example.voyageproject.model.CircuitDay
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class CircuitBookingSummaryActivity : AppCompatActivity() {

    private lateinit var circuit: Circuit
    private var adults = 0
    private val childrenAges = mutableListOf<Int>()
    private var hotelLevel = ""
    private var flightClass = ""
    private var departureDate: Date? = null
    private var totalPrice = 0.0
    private var activitiesTotal = 0.0
    private val repository = com.example.voyageproject.repository.CircuitRepository()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_circuit_booking_summary)

        val circuitJson = intent.getStringExtra("circuit")
        circuit = Gson().fromJson(circuitJson, Circuit::class.java) ?: run {
            finish()
            return
        }

        adults = intent.getIntExtra("adults", 2)
        
        // Récupérer les âges des enfants
        val childrenAgesList = intent.getIntegerArrayListExtra("childrenAges")
        if (childrenAgesList != null) {
            childrenAges.addAll(childrenAgesList)
        }
        
        hotelLevel = intent.getStringExtra("hotelLevel") ?: "STANDARD"
        flightClass = intent.getStringExtra("flightClass") ?: "ECONOMY"
        totalPrice = intent.getDoubleExtra("totalPrice", 0.0)
        
        val dateMillis = intent.getLongExtra("departureDate", 0L)
        departureDate = if (dateMillis > 0) Date(dateMillis) else null

        displaySummary()
        loadProgram()
        displayActivities()
        setupListeners()
    }

    private fun displaySummary() {
        // Circuit info
        findViewById<TextView>(R.id.tvCircuitTitle).text = circuit.title
        findViewById<TextView>(R.id.tvCircuitDestination).text = circuit.destinations?.firstOrNull() ?: "Destination"
        findViewById<TextView>(R.id.tvCircuitDuration).text = "${circuit.duree} jours / ${circuit.duree - 1} nuits"
        
        // Période du circuit
        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val startDate = departureDate ?: Date()
        val calendar = Calendar.getInstance()
        calendar.time = startDate
        calendar.add(Calendar.DAY_OF_YEAR, circuit.duree - 1)
        val endDate = calendar.time
        
        findViewById<TextView>(R.id.tvDepartureDate).text = "Du ${sdf.format(startDate)} au ${sdf.format(endDate)}"
        
        // Voyageurs avec détail des âges
        val travelersText = buildString {
            append("$adults adulte${if (adults > 1) "s" else ""}")
            if (childrenAges.isNotEmpty()) {
                append(", ${childrenAges.size} enfant${if (childrenAges.size > 1) "s" else ""}\n\n")
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
        findViewById<TextView>(R.id.tvTravelers).text = travelersText
        
        // Formule (pension)
        findViewById<TextView>(R.id.tvMealPlan).text = "Petit-déjeuner inclus"
        
        // Hôtel
        val hotelText = when (hotelLevel) {
            "STANDARD" -> "⭐ Standard (inclus)"
            "SUPERIOR" -> "⭐⭐ Supérieur (+200 TND/pers)"
            "LUXURY" -> "⭐⭐⭐ Luxe (+450 TND/pers)"
            else -> "Standard"
        }
        findViewById<TextView>(R.id.tvHotelLevel).text = hotelText
        
        // Vol
        val flightText = when (flightClass) {
            "ECONOMY" -> "Economy (inclus)"
            "BUSINESS" -> "Business (+400 TND/pers)"
            "FIRST" -> "First (+900 TND/pers)"
            else -> "Economy"
        }
        findViewById<TextView>(R.id.tvFlightClass).text = flightText
        
        // Prix détaillé
        displayPriceBreakdown()
        
        // Prix total
        findViewById<TextView>(R.id.tvTotalPrice).text = String.format("%.2f TND", totalPrice)
    }
    
    private fun loadProgram() {
        lifecycleScope.launch {
            try {
                val response = repository.getCircuitProgram(circuit.id)
                if (response.isSuccessful) {
                    val days = response.body() ?: emptyList()
                    if (days.isNotEmpty()) {
                        displayProgram(days)
                    } else {
                        findViewById<CardView>(R.id.cardProgram).visibility = View.GONE
                    }
                } else {
                    findViewById<CardView>(R.id.cardProgram).visibility = View.GONE
                }
            } catch (e: Exception) {
                android.util.Log.e("CIRCUIT_SUMMARY", "Erreur chargement programme: ${e.message}")
                findViewById<CardView>(R.id.cardProgram).visibility = View.GONE
            }
        }
    }
    
    private fun displayProgram(days: List<CircuitDay>) {
        findViewById<CardView>(R.id.cardProgram).visibility = View.VISIBLE
        val layoutProgramList = findViewById<LinearLayout>(R.id.layoutProgramList)
        layoutProgramList.removeAllViews()
        
        days.sortedBy { it.dayNumber }.forEach { day ->
            val dayView = layoutInflater.inflate(
                android.R.layout.simple_list_item_2,
                layoutProgramList,
                false
            )
            
            val text1 = dayView.findViewById<TextView>(android.R.id.text1)
            val text2 = dayView.findViewById<TextView>(android.R.id.text2)
            
            text1.text = "Jour ${day.dayNumber}: ${day.title}"
            text1.textSize = 14f
            text1.setTextColor(android.graphics.Color.parseColor("#212121"))
            
            // Formater la description en points
            val formattedDescription = day.description?.split(".")
                ?.filter { it.isNotBlank() }
                ?.joinToString("\n") { "• ${it.trim()}" }
            
            text2.text = formattedDescription
            text2.textSize = 12f
            text2.setTextColor(android.graphics.Color.parseColor("#757575"))
            
            layoutProgramList.addView(dayView)
            
            // Ajouter un séparateur
            if (day != days.last()) {
                val separator = View(this)
                separator.layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    2
                ).apply {
                    setMargins(0, 8, 0, 8)
                }
                separator.setBackgroundColor(android.graphics.Color.parseColor("#E0E0E0"))
                layoutProgramList.addView(separator)
            }
        }
    }
    
    private fun displayActivities() {
        val selectedActivitiesJson = intent.getStringExtra("selectedActivities")
        if (selectedActivitiesJson.isNullOrEmpty()) {
            findViewById<CardView>(R.id.cardActivities).visibility = View.GONE
            return
        }
        
        try {
            // Parser les activités sélectionnées
            val gson = Gson()
            val type = object : TypeToken<List<Map<String, Any>>>() {}.type
            val activitiesData: List<Map<String, Any>> = gson.fromJson(selectedActivitiesJson, type)
            
            if (activitiesData.isEmpty()) {
                findViewById<CardView>(R.id.cardActivities).visibility = View.GONE
                return
            }
            
            findViewById<CardView>(R.id.cardActivities).visibility = View.VISIBLE
            val layoutActivitiesList = findViewById<LinearLayout>(R.id.layoutActivitiesList)
            layoutActivitiesList.removeAllViews()
            
            activitiesTotal = 0.0
            
            activitiesData.forEach { activityMap ->
                val title = activityMap["title"] as? String ?: "Activité"
                val price = (activityMap["price"] as? Double) ?: 0.0
                val adultCount = ((activityMap["adultCount"] as? Double) ?: 0.0).toInt()
                val childCount = ((activityMap["childCount"] as? Double) ?: 0.0).toInt()
                
                // Calculer le prix
                val adultPrice = price * adultCount
                var childPrice = 0.0
                for (i in 0 until childCount.coerceAtMost(childrenAges.size)) {
                    val childInfo = ChildInfo(childrenAges[i])
                    childPrice += childInfo.getActivityPrice(price)
                }
                val totalActivityPrice = adultPrice + childPrice
                activitiesTotal += totalActivityPrice
                
                // Créer la vue
                val activityView = layoutInflater.inflate(
                    R.layout.item_activity_summary,
                    layoutActivitiesList,
                    false
                )
                
                activityView.findViewById<TextView>(R.id.tvActivityTitle).text = title
                
                val participantsText = buildString {
                    if (adultCount > 0) {
                        append("👨 $adultCount adulte${if (adultCount > 1) "s" else ""}")
                    }
                    if (childCount > 0) {
                        if (adultCount > 0) append(", ")
                        append("👶 $childCount enfant${if (childCount > 1) "s" else ""}")
                    }
                }
                activityView.findViewById<TextView>(R.id.tvActivityParticipants).text = participantsText
                
                activityView.findViewById<TextView>(R.id.tvActivityPrice).text = 
                    "💰 ${String.format("%.2f", totalActivityPrice)} TND"
                
                layoutActivitiesList.addView(activityView)
            }
            
            // Mettre à jour le prix des activités dans le détail
            if (activitiesTotal > 0) {
                findViewById<LinearLayout>(R.id.layoutActivitiesSupplement).visibility = View.VISIBLE
                findViewById<TextView>(R.id.tvActivitiesSupplement).text = 
                    String.format("+ %.2f TND", activitiesTotal)
            }
        } catch (e: Exception) {
            android.util.Log.e("CIRCUIT_SUMMARY", "Erreur parsing activités: ${e.message}")
            e.printStackTrace()
            findViewById<CardView>(R.id.cardActivities).visibility = View.GONE
        }
    }

    private fun displayPriceBreakdown() {
        val basePrice = circuit.prix
        val adultPrice = basePrice
        
        // Prix enfants selon l'âge
        val childrenPrice = childrenAges.sumOf { age ->
            val childInfo = ChildInfo(age)
            childInfo.getPrice(basePrice)
        }
        
        val hotelSupplement = when (hotelLevel) {
            "STANDARD" -> 0.0
            "SUPERIOR" -> 200.0
            "LUXURY" -> 450.0
            else -> 0.0
        }
        
        val flightSupplement = when (flightClass) {
            "ECONOMY" -> 0.0
            "BUSINESS" -> 400.0
            "FIRST" -> 900.0
            else -> 0.0
        }
        
        val totalPersons = adults + childrenAges.size
        val baseTotalPrice = (adults * adultPrice) + childrenPrice
        // IMPORTANT: Les suppléments hôtel et vol sont SEULEMENT pour les adultes
        // Les enfants ne paient PAS ces suppléments
        val hotelTotal = adults * hotelSupplement  // Seulement adultes
        val flightTotal = adults * flightSupplement  // Seulement adultes
        
        // Afficher les détails
        val basePriceText = buildString {
            append("$adults adulte${if (adults > 1) "s" else ""} × ${String.format("%.0f", adultPrice)} TND")
            if (childrenAges.isNotEmpty()) {
                append("\n+ ${childrenAges.size} enfant${if (childrenAges.size > 1) "s" else ""}")
                childrenAges.forEach { age ->
                    val childInfo = ChildInfo(age)
                    val price = childInfo.getPrice(basePrice)
                    if (price > 0) {
                        append("\n  ($age ans: ${String.format("%.0f", price)} TND)")
                    } else {
                        append("\n  ($age ans: gratuit)")
                    }
                }
            }
            append("\n= ${String.format("%.2f", baseTotalPrice)} TND")
        }
        findViewById<TextView>(R.id.tvBasePrice).text = basePriceText
        
        if (hotelTotal > 0) {
            findViewById<LinearLayout>(R.id.layoutHotelSupplement).visibility = View.VISIBLE
            val hotelText = "$adults adulte${if (adults > 1) "s" else ""} × ${String.format("%.0f", hotelSupplement)} TND"
            findViewById<TextView>(R.id.tvHotelSupplement).text = String.format("+ %.2f TND\n(%s)", hotelTotal, hotelText)
        }
        
        if (flightTotal > 0) {
            findViewById<LinearLayout>(R.id.layoutFlightSupplement).visibility = View.VISIBLE
            val flightText = "$adults adulte${if (adults > 1) "s" else ""} × ${String.format("%.0f", flightSupplement)} TND"
            findViewById<TextView>(R.id.tvFlightSupplement).text = String.format("+ %.2f TND\n(%s)", flightTotal, flightText)
        }
    }

    private fun setupListeners() {
        findViewById<ImageButton>(R.id.btnBack).setOnClickListener { finish() }
        
        findViewById<Button>(R.id.btnPay).setOnClickListener {
            goToPayment()
        }
    }

    private fun goToPayment() {
        val intent = Intent(this, com.example.voyageproject.ui.payment.PaymentActivity::class.java)
        intent.putExtra("offerType", "circuit")
        intent.putExtra("offerId", circuit.id)
        intent.putExtra("offerName", circuit.title)
        intent.putExtra("offerPrice", totalPrice)  // Utiliser offerPrice au lieu de totalPrice
        intent.putExtra("totalPrice", totalPrice)  // Garder aussi totalPrice pour compatibilité
        
        // Passer tous les détails pour l'affichage
        intent.putExtra("adults", adults)
        intent.putExtra("childrenAges", ArrayList(childrenAges))
        intent.putExtra("hotelLevel", hotelLevel)
        intent.putExtra("flightClass", flightClass)
        intent.putExtra("departureDate", departureDate?.time ?: 0L)
        intent.putExtra("circuitDuration", circuit.duree)
        intent.putExtra("circuitDestination", circuit.destinations?.firstOrNull() ?: "")
        
        // Passer le détail des prix
        val basePrice = circuit.prix
        val adultPrice = basePrice * adults
        val childrenPrice = childrenAges.sumOf { age ->
            val childInfo = ChildInfo(age)
            childInfo.getPrice(basePrice)
        }
        
        val hotelSupplement = when (hotelLevel) {
            "STANDARD" -> 0.0
            "SUPERIOR" -> 200.0
            "LUXURY" -> 450.0
            else -> 0.0
        }
        
        val flightSupplement = when (flightClass) {
            "ECONOMY" -> 0.0
            "BUSINESS" -> 400.0
            "FIRST" -> 900.0
            else -> 0.0
        }
        
        val hotelTotal = adults * hotelSupplement
        val flightTotal = adults * flightSupplement
        
        intent.putExtra("basePrice", adultPrice + childrenPrice)
        intent.putExtra("hotelSupplement", hotelTotal)
        intent.putExtra("flightSupplement", flightTotal)
        intent.putExtra("activitiesTotal", activitiesTotal)
        
        // Passer les activités sélectionnées
        val selectedActivitiesJson = this.intent.getStringExtra("selectedActivities")
        if (!selectedActivitiesJson.isNullOrEmpty()) {
            intent.putExtra("selectedActivities", selectedActivitiesJson)
        }
        
        startActivity(intent)
    }
}
