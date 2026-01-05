package com.example.voyageproject.ui.circuit

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.voyageproject.R
import com.example.voyageproject.model.ChildInfo
import com.example.voyageproject.model.Circuit
import com.example.voyageproject.model.CircuitActivity
import com.google.android.material.datepicker.MaterialDatePicker
import java.text.SimpleDateFormat
import java.util.*

class CircuitBookingActivity : AppCompatActivity() {

    private lateinit var circuit: Circuit
    private var adults = 2
    private val childrenAges = mutableListOf<ChildInfo>()
    private var hotelLevel = "STANDARD"
    private var flightClass = "ECONOMY"
    private val selectedActivities = mutableListOf<CircuitActivity>()
    private var departureDate: Date? = null
    private var allActivities = listOf<CircuitActivity>()
    private val repository = com.example.voyageproject.repository.CircuitRepository()
    private lateinit var activityAdapter: CircuitActivityAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_circuit_booking)

        val circuitJson = intent.getStringExtra("circuitData")
        circuit = com.google.gson.Gson().fromJson(circuitJson, Circuit::class.java) ?: run {
            finish()
            return
        }
        
        // Récupérer les critères de recherche
        adults = intent.getIntExtra("adults", 2)
        val childrenCount = intent.getIntExtra("children", 0)
        
        // Initialiser les enfants avec âge par défaut (10 ans)
        for (i in 0 until childrenCount) {
            childrenAges.add(ChildInfo(10))
        }
        
        val dateMillis = intent.getLongExtra("departureDate", 0L)
        if (dateMillis > 0) {
            departureDate = Date(dateMillis)
        }
        
        // Récupérer les activités
        val activitiesJson = intent.getStringExtra("activities")
        if (!activitiesJson.isNullOrEmpty()) {
            try {
                val type = object : com.google.gson.reflect.TypeToken<List<CircuitActivity>>() {}.type
                allActivities = com.google.gson.Gson().fromJson(activitiesJson, type) ?: emptyList()
            } catch (e: Exception) {
                android.util.Log.e("CIRCUIT_BOOKING", "Erreur parsing activités: ${e.message}")
            }
        }

        setupViews()
        setupListeners()
        updatePrice()
    }

    private fun setupViews() {
        findViewById<TextView>(R.id.tvCircuitTitle).text = circuit.title
        findViewById<TextView>(R.id.tvCircuitDuration).text = "${circuit.duree} jours"
        
        // Voyageurs
        findViewById<TextView>(R.id.tvAdultsCount).text = adults.toString()
        findViewById<TextView>(R.id.tvChildrenCount).text = childrenAges.size.toString()
        
        // Afficher les âges des enfants
        updateChildrenAgesDisplay()
        
        // Date par défaut (dans 7 jours)
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_YEAR, 7)
        departureDate = calendar.time
        updateDateDisplay()
        
        // Niveau d'hôtel
        val rgHotelLevel = findViewById<RadioGroup>(R.id.rgHotelLevel)
        rgHotelLevel.check(R.id.rbHotelStandard)
        
        // Classe de vol
        val rgFlightClass = findViewById<RadioGroup>(R.id.rgFlightClass)
        rgFlightClass.check(R.id.rbFlightEconomy)
        
        // Activités optionnelles
        setupActivities()
    }

    private fun setupListeners() {
        findViewById<ImageButton>(R.id.btnBack).setOnClickListener { finish() }
        
        // Adultes
        findViewById<ImageButton>(R.id.btnAdultsPlus).setOnClickListener {
            if (adults < 10) {
                adults++
                findViewById<TextView>(R.id.tvAdultsCount).text = adults.toString()
                setupActivities() // Recréer l'adapter avec le nouveau max
                updatePrice()
            }
        }
        
        findViewById<ImageButton>(R.id.btnAdultsMinus).setOnClickListener {
            if (adults > 1) {
                adults--
                findViewById<TextView>(R.id.tvAdultsCount).text = adults.toString()
                setupActivities() // Recréer l'adapter avec le nouveau max
                updatePrice()
            }
        }
        
        // Enfants
        findViewById<ImageButton>(R.id.btnChildrenPlus).setOnClickListener {
            if (childrenAges.size < 10) {
                showChildAgeDialog(childrenAges.size)
            }
        }
        
        findViewById<ImageButton>(R.id.btnChildrenMinus).setOnClickListener {
            if (childrenAges.isNotEmpty()) {
                childrenAges.removeAt(childrenAges.size - 1)
                findViewById<TextView>(R.id.tvChildrenCount).text = childrenAges.size.toString()
                updateChildrenAgesDisplay()
                setupActivities() // Recréer l'adapter avec la nouvelle liste d'enfants
                updatePrice()
            }
        }
        
        // Date
        findViewById<LinearLayout>(R.id.layoutDepartureDate).setOnClickListener {
            showDatePicker()
        }
        
        // Niveau d'hôtel
        findViewById<RadioGroup>(R.id.rgHotelLevel).setOnCheckedChangeListener { _, checkedId ->
            hotelLevel = when (checkedId) {
                R.id.rbHotelStandard -> "STANDARD"
                R.id.rbHotelSuperior -> "SUPERIOR"
                R.id.rbHotelLuxury -> "LUXURY"
                else -> "STANDARD"
            }
            updatePrice()
        }
        
        // Classe de vol
        findViewById<RadioGroup>(R.id.rgFlightClass).setOnCheckedChangeListener { _, checkedId ->
            flightClass = when (checkedId) {
                R.id.rbFlightEconomy -> "ECONOMY"
                R.id.rbFlightBusiness -> "BUSINESS"
                R.id.rbFlightFirst -> "FIRST"
                else -> "ECONOMY"
            }
            updatePrice()
        }
        
        // Bouton continuer
        findViewById<Button>(R.id.btnContinue).setOnClickListener {
            goToSummary()
        }
    }
    
    private fun showChildAgeDialog(childIndex: Int) {
        val dialogView = layoutInflater.inflate(R.layout.dialog_child_age, null)
        val etChildAge = dialogView.findViewById<EditText>(R.id.etChildAge)
        
        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .create()
        
        dialogView.findViewById<Button>(R.id.btnCancel).setOnClickListener {
            dialog.dismiss()
        }
        
        dialogView.findViewById<Button>(R.id.btnConfirm).setOnClickListener {
            val ageText = etChildAge.text.toString()
            if (ageText.isNotEmpty()) {
                val age = ageText.toIntOrNull()
                if (age != null && age in 0..18) {
                    childrenAges.add(ChildInfo(age))
                    findViewById<TextView>(R.id.tvChildrenCount).text = childrenAges.size.toString()
                    updateChildrenAgesDisplay()
                    setupActivities() // Recréer l'adapter avec la nouvelle liste d'enfants
                    updatePrice()
                    dialog.dismiss()
                } else {
                    Toast.makeText(this, "Âge invalide (0-18 ans)", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Veuillez entrer un âge", Toast.LENGTH_SHORT).show()
            }
        }
        
        dialog.show()
    }
    
    private fun updateChildrenAgesDisplay() {
        val tvChildrenAges = findViewById<TextView>(R.id.tvChildrenAges)
        if (childrenAges.isEmpty()) {
            tvChildrenAges?.visibility = android.view.View.GONE
        } else {
            tvChildrenAges?.visibility = android.view.View.VISIBLE
            val agesText = childrenAges.mapIndexed { index, child ->
                "Enfant ${index + 1}: ${child.age} ans"
            }.joinToString(", ")
            tvChildrenAges?.text = agesText
        }
    }

    private fun setupActivities() {
        val activitiesContainer = findViewById<LinearLayout>(R.id.activitiesContainer)
        activitiesContainer.removeAllViews()
        
        // Filtrer les activités optionnelles
        val optionalActivities = allActivities.filter { it.isOptional }
        
        if (optionalActivities.isEmpty()) {
            // Afficher un message si aucune activité
            val textView = TextView(this).apply {
                text = "Aucune activité optionnelle disponible"
                textSize = 14f
                setTextColor(android.graphics.Color.parseColor("#757575"))
                setPadding(16, 16, 16, 16)
            }
            activitiesContainer.addView(textView)
        } else {
            // Utiliser RecyclerView pour les activités
            val recyclerView = androidx.recyclerview.widget.RecyclerView(this).apply {
                layoutManager = androidx.recyclerview.widget.LinearLayoutManager(this@CircuitBookingActivity)
                activityAdapter = CircuitActivityAdapter(optionalActivities, adults, childrenAges) {
                    // Callback appelé quand une activité change
                    updatePrice()
                }
                adapter = activityAdapter
            }
            activitiesContainer.addView(recyclerView)
        }
    }

    private fun showDatePicker() {
        val picker = MaterialDatePicker.Builder.datePicker()
            .setTitleText("Date de départ")
            .setSelection(departureDate?.time ?: MaterialDatePicker.todayInUtcMilliseconds())
            .build()
        
        picker.addOnPositiveButtonClickListener { selection ->
            departureDate = Date(selection)
            updateDateDisplay()
        }
        
        picker.show(supportFragmentManager, "DATE_PICKER")
    }

    private fun updateDateDisplay() {
        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        findViewById<TextView>(R.id.tvDepartureDate).text = sdf.format(departureDate)
    }

    private fun updatePrice() {
        val basePrice = circuit.prix
        
        // Prix par voyageur
        val adultPrice = basePrice
        
        // Prix enfants selon l'âge
        val childrenPrice = childrenAges.sumOf { it.getPrice(basePrice) }
        
        // Suppléments
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
        
        // Calcul total
        // IMPORTANT: Les suppléments hôtel et vol sont SEULEMENT pour les adultes
        // Les enfants ne paient PAS ces suppléments
        val baseTotalPrice = (adults * adultPrice) + childrenPrice
        val hotelTotal = adults * hotelSupplement  // Seulement adultes
        val flightTotal = adults * flightSupplement  // Seulement adultes
        
        // Prix des activités
        val activitiesTotal = if (::activityAdapter.isInitialized) {
            activityAdapter.getTotalActivitiesPrice()
        } else {
            0.0
        }
        
        val total = baseTotalPrice + hotelTotal + flightTotal + activitiesTotal
        
        findViewById<TextView>(R.id.tvTotalPrice).text = String.format("%.2f TND", total)
    }

    private fun goToSummary() {
        val intent = Intent(this, CircuitBookingSummaryActivity::class.java)
        intent.putExtra("circuit", com.google.gson.Gson().toJson(circuit))
        intent.putExtra("adults", adults)
        intent.putExtra("childrenAges", ArrayList(childrenAges.map { it.age }))
        intent.putExtra("hotelLevel", hotelLevel)
        intent.putExtra("flightClass", flightClass)
        intent.putExtra("departureDate", departureDate?.time ?: 0L)
        
        // Passer les activités sélectionnées avec leurs détails
        if (::activityAdapter.isInitialized) {
            val selectedActivitiesWithCount = activityAdapter.getSelectedActivitiesWithCount()
            
            // Créer une structure simple pour le JSON
            val activitiesData = mutableListOf<Map<String, Any>>()
            selectedActivitiesWithCount.forEach { (activity, counts) ->
                val (adultCount, childCount) = counts
                activitiesData.add(mapOf(
                    "id" to activity.id,
                    "title" to activity.title,
                    "price" to activity.price,
                    "adultCount" to adultCount,
                    "childCount" to childCount
                ))
            }
            
            intent.putExtra("selectedActivities", com.google.gson.Gson().toJson(activitiesData))
        }
        
        intent.putExtra("totalPrice", calculateTotalPrice())
        startActivity(intent)
    }

    private fun calculateTotalPrice(): Double {
        val basePrice = circuit.prix
        val adultPrice = basePrice
        val childrenPrice = childrenAges.sumOf { it.getPrice(basePrice) }
        
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
        
        // IMPORTANT: Les suppléments hôtel et vol sont SEULEMENT pour les adultes
        val baseTotalPrice = (adults * adultPrice) + childrenPrice
        val hotelTotal = adults * hotelSupplement  // Seulement adultes
        val flightTotal = adults * flightSupplement  // Seulement adultes
        
        val activitiesTotal = if (::activityAdapter.isInitialized) {
            activityAdapter.getTotalActivitiesPrice()
        } else {
            0.0
        }
        
        return baseTotalPrice + hotelTotal + flightTotal + activitiesTotal
    }
}
