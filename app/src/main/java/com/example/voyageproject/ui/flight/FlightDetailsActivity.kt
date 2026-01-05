package com.example.voyageproject.ui.flight

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.voyageproject.R
import com.example.voyageproject.model.*
import java.text.SimpleDateFormat
import java.util.*

class FlightDetailsActivity : AppCompatActivity() {

    private lateinit var tvAirline: TextView
    private lateinit var tvFlightNumber: TextView
    private lateinit var tvDepartureInfo: TextView
    private lateinit var tvArrivalInfo: TextView
    private lateinit var tvDuration: TextView
    private lateinit var tvPassengers: TextView
    
    // Type de billet et dates
    private lateinit var rgTripType: RadioGroup
    private lateinit var rbOneWay: RadioButton
    private lateinit var rbRoundTrip: RadioButton
    private lateinit var etDepartureDate: EditText
    private lateinit var etReturnDate: EditText
    private lateinit var layoutReturnDate: View
    
    private var departureDate: String = ""
    private var returnDate: String? = null
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    private val displayDateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    
    // Gestion des passagers
    private lateinit var tvAdultsCount: TextView
    private lateinit var tvChildrenCount: TextView
    private lateinit var btnAdultsMinus: Button
    private lateinit var btnAdultsPlus: Button
    private lateinit var btnChildrenMinus: Button
    private lateinit var btnChildrenPlus: Button
    
    private var adultsCount = 1
    private val childrenList = mutableListOf<PassengerChild>()
    
    private lateinit var rgFlightClass: RadioGroup
    private lateinit var rbEconomy: RadioButton
    private lateinit var rbPremiumEconomy: RadioButton
    private lateinit var rbBusiness: RadioButton
    private lateinit var rbFirst: RadioButton
    
    private lateinit var tvEconomyFeatures: TextView
    private lateinit var tvPremiumFeatures: TextView
    private lateinit var tvBusinessFeatures: TextView
    private lateinit var tvFirstFeatures: TextView
    
    private lateinit var tvEstimatedPrice: TextView
    private lateinit var btnContinue: Button
    
    private lateinit var flight: Flight
    private lateinit var searchCriteria: FlightSearchCriteria
    private var selectedClass: FlightClass = FlightClass.ECONOMY

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_flight_details)

        // Gérer les deux cas: depuis la recherche OU depuis la liste principale
        if (intent.hasExtra("flight") && intent.hasExtra("search_criteria")) {
            // Cas 1: Depuis FlightResultsActivity (avec critères de recherche)
            flight = intent.getSerializableExtra("flight") as Flight
            searchCriteria = intent.getParcelableExtra("search_criteria")!!
            selectedClass = searchCriteria.selectedClass
        } else if (intent.hasExtra("flightData")) {
            // Cas 2: Depuis PremiumOffersAdapter (sans critères de recherche)
            val flightJson = intent.getStringExtra("flightData")
            flight = com.google.gson.Gson().fromJson(flightJson, Flight::class.java)
            
            // Initialiser avec des valeurs par défaut modifiables
            adultsCount = 1
            childrenList.clear()
            
            // Créer des critères par défaut
            searchCriteria = FlightSearchCriteria(
                origin = flight.origin,
                destination = flight.destination,
                tripType = TripType.ONE_WAY,
                departureDate = java.time.LocalDate.now().toString(),
                returnDate = null,
                adults = adultsCount,
                children = childrenList.toList(),
                selectedClass = FlightClass.ECONOMY
            )
            selectedClass = FlightClass.ECONOMY
        } else {
            // Erreur: aucune donnée
            finish()
            return
        }

        initViews()
        setupTripTypeAndDates()
        setupPassengerControls()
        displayFlightInfo()
        setupClassSelection()
        updatePrice()
    }

    private fun initViews() {
        tvAirline = findViewById(R.id.tvAirline)
        tvFlightNumber = findViewById(R.id.tvFlightNumber)
        tvDepartureInfo = findViewById(R.id.tvDepartureInfo)
        tvArrivalInfo = findViewById(R.id.tvArrivalInfo)
        tvDuration = findViewById(R.id.tvDuration)
        tvPassengers = findViewById(R.id.tvPassengers)
        
        // Type de billet et dates
        rgTripType = findViewById(R.id.rgTripType)
        rbOneWay = findViewById(R.id.rbOneWay)
        rbRoundTrip = findViewById(R.id.rbRoundTrip)
        etDepartureDate = findViewById(R.id.etDepartureDate)
        etReturnDate = findViewById(R.id.etReturnDate)
        layoutReturnDate = findViewById(R.id.layoutReturnDate)
        
        // Contrôles passagers
        tvAdultsCount = findViewById(R.id.tvAdultsCount)
        tvChildrenCount = findViewById(R.id.tvChildrenCount)
        btnAdultsMinus = findViewById(R.id.btnAdultsMinus)
        btnAdultsPlus = findViewById(R.id.btnAdultsPlus)
        btnChildrenMinus = findViewById(R.id.btnChildrenMinus)
        btnChildrenPlus = findViewById(R.id.btnChildrenPlus)
        
        rgFlightClass = findViewById(R.id.rgFlightClass)
        rbEconomy = findViewById(R.id.rbEconomy)
        rbPremiumEconomy = findViewById(R.id.rbPremiumEconomy)
        rbBusiness = findViewById(R.id.rbBusiness)
        rbFirst = findViewById(R.id.rbFirst)
        
        tvEconomyFeatures = findViewById(R.id.tvEconomyFeatures)
        tvPremiumFeatures = findViewById(R.id.tvPremiumFeatures)
        tvBusinessFeatures = findViewById(R.id.tvBusinessFeatures)
        tvFirstFeatures = findViewById(R.id.tvFirstFeatures)
        
        tvEstimatedPrice = findViewById(R.id.tvEstimatedPrice)
        btnContinue = findViewById(R.id.btnContinue)
        
        // Initialiser les compteurs depuis searchCriteria
        adultsCount = searchCriteria.adults
        childrenList.clear()
        childrenList.addAll(searchCriteria.children)
        
        supportActionBar?.title = "Détails du vol"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }
    
    private fun setupTripTypeAndDates() {
        // Initialiser les dates depuis searchCriteria
        departureDate = searchCriteria.departureDate
        etDepartureDate.setText(try {
            val date = dateFormat.parse(departureDate)
            displayDateFormat.format(date!!)
        } catch (e: Exception) {
            departureDate
        })
        
        // Initialiser le type de billet
        when (searchCriteria.tripType) {
            TripType.ONE_WAY -> {
                rbOneWay.isChecked = true
                layoutReturnDate.visibility = View.GONE
            }
            TripType.ROUND_TRIP -> {
                rbRoundTrip.isChecked = true
                layoutReturnDate.visibility = View.VISIBLE
                searchCriteria.returnDate?.let { retDate ->
                    returnDate = retDate
                    etReturnDate.setText(try {
                        val date = dateFormat.parse(retDate)
                        displayDateFormat.format(date!!)
                    } catch (e: Exception) {
                        retDate
                    })
                }
            }
        }
        
        // Gérer le changement de type de billet
        rgTripType.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.rbOneWay -> {
                    layoutReturnDate.visibility = View.GONE
                    returnDate = null
                    etReturnDate.text.clear()
                    updateSearchCriteria()
                }
                R.id.rbRoundTrip -> {
                    layoutReturnDate.visibility = View.VISIBLE
                    updateSearchCriteria()
                }
            }
        }
        
        // Sélection de la date de départ
        etDepartureDate.setOnClickListener {
            showDatePicker(null) { date ->
                departureDate = dateFormat.format(date)
                etDepartureDate.setText(displayDateFormat.format(date))
                
                // Si date de retour existe et est avant la nouvelle date de départ, la réinitialiser
                returnDate?.let { retDate ->
                    try {
                        val retDateObj = dateFormat.parse(retDate)
                        if (retDateObj!!.before(date)) {
                            returnDate = null
                            etReturnDate.text.clear()
                        }
                    } catch (e: Exception) {}
                }
                
                updateSearchCriteria()
            }
        }
        
        // Sélection de la date de retour
        etReturnDate.setOnClickListener {
            if (departureDate.isEmpty()) {
                Toast.makeText(this, "Sélectionnez d'abord la date de départ", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            
            val minDate = try {
                dateFormat.parse(departureDate)
            } catch (e: Exception) {
                Date()
            }
            
            showDatePicker(minDate) { date ->
                returnDate = dateFormat.format(date)
                etReturnDate.setText(displayDateFormat.format(date))
                updateSearchCriteria()
            }
        }
    }
    
    private fun showDatePicker(minDate: Date?, onDateSelected: (Date) -> Unit) {
        val calendar = Calendar.getInstance()
        if (minDate != null) {
            calendar.time = minDate
        }

        val picker = android.app.DatePickerDialog(
            this,
            { _, year, month, day ->
                calendar.set(year, month, day)
                onDateSelected(calendar.time)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )

        if (minDate != null) {
            picker.datePicker.minDate = minDate.time
        } else {
            picker.datePicker.minDate = System.currentTimeMillis()
        }

        picker.show()
    }
    
    private fun updateSearchCriteria() {
        val tripType = if (rbRoundTrip.isChecked) TripType.ROUND_TRIP else TripType.ONE_WAY
        
        searchCriteria = searchCriteria.copy(
            tripType = tripType,
            departureDate = departureDate,
            returnDate = returnDate,
            adults = adultsCount,
            children = childrenList.toList()
        )
        
        updatePrice()
    }
    
    private fun setupPassengerControls() {
        updatePassengersDisplay()
        
        btnAdultsMinus.setOnClickListener {
            if (adultsCount > 1) {
                adultsCount--
                updatePassengersAndPrice()
            }
        }
        
        btnAdultsPlus.setOnClickListener {
            if (adultsCount < 9) {
                adultsCount++
                updatePassengersAndPrice()
            }
        }
        
        btnChildrenMinus.setOnClickListener {
            if (childrenList.isNotEmpty()) {
                childrenList.removeAt(childrenList.size - 1)
                updatePassengersAndPrice()
            }
        }
        
        btnChildrenPlus.setOnClickListener {
            if (childrenList.size < 9) {
                showChildAgeDialog()
            }
        }
    }
    
    private fun showChildAgeDialog() {
        val ages = (0..18).map { "$it ans" }.toTypedArray()
        
        androidx.appcompat.app.AlertDialog.Builder(this)
            .setTitle("Âge de l'enfant")
            .setItems(ages) { _, which ->
                childrenList.add(PassengerChild(which))
                updatePassengersAndPrice()
            }
            .setNegativeButton("Annuler", null)
            .show()
    }
    
    private fun updatePassengersDisplay() {
        tvAdultsCount.text = adultsCount.toString()
        tvChildrenCount.text = childrenList.size.toString()
        
        btnAdultsMinus.isEnabled = adultsCount > 1
        btnChildrenMinus.isEnabled = childrenList.isNotEmpty()
        
        // Mettre à jour le résumé
        val freeChildren = childrenList.count { it.isFree() }
        val paidChildren = childrenList.count { !it.isFree() }
        
        val parts = mutableListOf<String>()
        if (adultsCount > 0) {
            parts.add("$adultsCount adulte${if (adultsCount > 1) "s" else ""}")
        }
        if (paidChildren > 0) {
            parts.add("$paidChildren enfant${if (paidChildren > 1) "s" else ""} (5-18 ans)")
        }
        if (freeChildren > 0) {
            parts.add("$freeChildren bébé${if (freeChildren > 1) "s" else ""} (0-4 ans)")
        }
        
        tvPassengers.text = "👥 ${parts.joinToString(", ")}"
    }
    
    private fun updatePassengersAndPrice() {
        updatePassengersDisplay()
        updateSearchCriteria()
        updateClassPrices()
    }

    private fun displayFlightInfo() {
        tvAirline.text = "${flight.airline} - ${flight.flightNumber}"
        tvFlightNumber.text = "Vol ${flight.flightNumber}"
        
        val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
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
        
        tvDepartureInfo.text = "🛫 DÉPART\n$departureTime - ${flight.origin}"
        tvArrivalInfo.text = "🛬 ARRIVÉE\n$arrivalTime - ${flight.destination}"
        tvDuration.text = "⏱️ Durée: ${flight.duration ?: "4h"}"
        tvPassengers.text = "👥 ${searchCriteria.getPassengersSummary()}"
        
        // Afficher les caractéristiques de chaque classe
        tvEconomyFeatures.text = FlightClass.ECONOMY.getFeatures().joinToString("\n")
        tvPremiumFeatures.text = FlightClass.PREMIUM_ECONOMY.getFeatures().joinToString("\n")
        tvBusinessFeatures.text = FlightClass.BUSINESS.getFeatures().joinToString("\n")
        tvFirstFeatures.text = FlightClass.FIRST.getFeatures().joinToString("\n")
        
        // Mettre à jour les prix sur les RadioButtons
        updateClassPrices()
    }

    private fun updateClassPrices() {
        FlightClass.values().forEach { flightClass ->
            val priceBreakdown = FlightPriceCalculator.calculatePrice(
                basePrice = flight.price,
                flightClass = flightClass,
                adults = searchCriteria.adults,
                children = searchCriteria.children
            )
            
            val priceText = "${flightClass.displayName} - ${String.format("%.2f", priceBreakdown.totalPrice)} TND"
            
            when (flightClass) {
                FlightClass.ECONOMY -> rbEconomy.text = priceText
                FlightClass.PREMIUM_ECONOMY -> rbPremiumEconomy.text = priceText
                FlightClass.BUSINESS -> rbBusiness.text = priceText
                FlightClass.FIRST -> rbFirst.text = priceText
            }
        }
    }

    private fun setupClassSelection() {
        // Sélectionner la classe par défaut
        when (selectedClass) {
            FlightClass.ECONOMY -> rbEconomy.isChecked = true
            FlightClass.PREMIUM_ECONOMY -> rbPremiumEconomy.isChecked = true
            FlightClass.BUSINESS -> rbBusiness.isChecked = true
            FlightClass.FIRST -> rbFirst.isChecked = true
        }
        
        rgFlightClass.setOnCheckedChangeListener { _, checkedId ->
            selectedClass = when (checkedId) {
                R.id.rbEconomy -> FlightClass.ECONOMY
                R.id.rbPremiumEconomy -> FlightClass.PREMIUM_ECONOMY
                R.id.rbBusiness -> FlightClass.BUSINESS
                R.id.rbFirst -> FlightClass.FIRST
                else -> FlightClass.ECONOMY
            }
            updatePrice()
        }
        
        btnContinue.setOnClickListener {
            openSummary()
        }
    }

    private fun updatePrice() {
        val priceBreakdown = FlightPriceCalculator.calculatePrice(
            basePrice = flight.price,
            flightClass = selectedClass,
            adults = searchCriteria.adults,
            children = searchCriteria.children
        )
        
        tvEstimatedPrice.text = "Prix total: ${String.format("%.2f", priceBreakdown.totalPrice)} TND"
    }

    private fun openSummary() {
        val updatedCriteria = searchCriteria.copy(selectedClass = selectedClass)
        
        val intent = Intent(this, FlightBookingSummaryActivity::class.java)
        intent.putExtra("flight", flight)
        intent.putExtra("search_criteria", updatedCriteria)
        startActivity(intent)
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}
