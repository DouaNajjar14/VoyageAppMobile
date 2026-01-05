package com.example.voyageproject.ui.flight

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.voyageproject.R
import com.example.voyageproject.model.*
import java.text.SimpleDateFormat
import java.util.*

class FlightSearchActivity : AppCompatActivity() {

    private lateinit var etOrigin: AutoCompleteTextView
    private lateinit var etDestination: AutoCompleteTextView
    private lateinit var rgTripType: RadioGroup
    private lateinit var rbOneWay: RadioButton
    private lateinit var rbRoundTrip: RadioButton
    private lateinit var etDepartureDate: EditText
    private lateinit var etReturnDate: EditText
    private lateinit var layoutReturnDate: View
    
    private lateinit var tvAdultsCount: TextView
    private lateinit var tvChildrenCount: TextView
    private lateinit var btnAdultsMinus: Button
    private lateinit var btnAdultsPlus: Button
    private lateinit var btnChildrenMinus: Button
    private lateinit var btnChildrenPlus: Button
    
    private lateinit var spinnerClass: Spinner
    private lateinit var btnSearch: Button

    private var adultsCount = 1
    private val childrenList = mutableListOf<PassengerChild>()
    
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    private val displayDateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    
    private var departureDate: Date? = null
    private var returnDate: Date? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_flight_search)

        initViews()
        setupOriginDestination()
        setupTripType()
        setupDates()
        setupPassengers()
        setupFlightClass()
        setupSearchButton()
    }

    private fun initViews() {
        etOrigin = findViewById(R.id.etOrigin)
        etDestination = findViewById(R.id.etDestination)
        rgTripType = findViewById(R.id.rgTripType)
        rbOneWay = findViewById(R.id.rbOneWay)
        rbRoundTrip = findViewById(R.id.rbRoundTrip)
        etDepartureDate = findViewById(R.id.etDepartureDate)
        etReturnDate = findViewById(R.id.etReturnDate)
        layoutReturnDate = findViewById(R.id.layoutReturnDate)
        
        tvAdultsCount = findViewById(R.id.tvAdultsCount)
        tvChildrenCount = findViewById(R.id.tvChildrenCount)
        btnAdultsMinus = findViewById(R.id.btnAdultsMinus)
        btnAdultsPlus = findViewById(R.id.btnAdultsPlus)
        btnChildrenMinus = findViewById(R.id.btnChildrenMinus)
        btnChildrenPlus = findViewById(R.id.btnChildrenPlus)
        
        spinnerClass = findViewById(R.id.spinnerClass)
        btnSearch = findViewById(R.id.btnSearch)
    }

    private fun setupOriginDestination() {
        val cities = arrayOf(
            "TUN - Tunis",
            "CDG - Paris",
            "LHR - Londres",
            "DXB - Dubaï",
            "IST - Istanbul",
            "FCO - Rome",
            "MAD - Madrid",
            "BCN - Barcelone"
        )
        
        val adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, cities)
        etOrigin.setAdapter(adapter)
        etDestination.setAdapter(adapter)
    }

    private fun setupTripType() {
        rgTripType.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.rbOneWay -> {
                    layoutReturnDate.visibility = View.GONE
                    returnDate = null
                    etReturnDate.text.clear()
                }
                R.id.rbRoundTrip -> {
                    layoutReturnDate.visibility = View.VISIBLE
                }
            }
        }
    }

    private fun setupDates() {
        etDepartureDate.setOnClickListener {
            showDatePicker { date ->
                departureDate = date
                etDepartureDate.setText(displayDateFormat.format(date))
            }
        }

        etReturnDate.setOnClickListener {
            if (departureDate == null) {
                Toast.makeText(this, "Sélectionnez d'abord la date de départ", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            
            showDatePicker(minDate = departureDate) { date ->
                returnDate = date
                etReturnDate.setText(displayDateFormat.format(date))
            }
        }
    }

    private fun showDatePicker(minDate: Date? = null, onDateSelected: (Date) -> Unit) {
        val calendar = Calendar.getInstance()
        if (minDate != null) {
            calendar.time = minDate
        }

        val picker = DatePickerDialog(
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

    private fun setupPassengers() {
        updatePassengersDisplay()

        btnAdultsMinus.setOnClickListener {
            if (adultsCount > 1) {
                adultsCount--
                updatePassengersDisplay()
            }
        }

        btnAdultsPlus.setOnClickListener {
            if (adultsCount < 9) {
                adultsCount++
                updatePassengersDisplay()
            }
        }

        btnChildrenMinus.setOnClickListener {
            if (childrenList.isNotEmpty()) {
                childrenList.removeAt(childrenList.size - 1)
                updatePassengersDisplay()
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
        
        AlertDialog.Builder(this)
            .setTitle("Âge de l'enfant")
            .setItems(ages) { _, which ->
                childrenList.add(PassengerChild(which))
                updatePassengersDisplay()
            }
            .setNegativeButton("Annuler", null)
            .show()
    }

    private fun updatePassengersDisplay() {
        tvAdultsCount.text = adultsCount.toString()
        tvChildrenCount.text = childrenList.size.toString()
        
        btnAdultsMinus.isEnabled = adultsCount > 1
        btnChildrenMinus.isEnabled = childrenList.isNotEmpty()
    }

    private fun setupFlightClass() {
        val classes = FlightClass.values().map { it.displayName }
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, classes)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerClass.adapter = adapter
    }

    private fun setupSearchButton() {
        btnSearch.setOnClickListener {
            if (validateInputs()) {
                performSearch()
            }
        }
    }

    private fun validateInputs(): Boolean {
        if (etOrigin.text.toString().isEmpty()) {
            Toast.makeText(this, "Sélectionnez une origine", Toast.LENGTH_SHORT).show()
            return false
        }

        if (etDestination.text.toString().isEmpty()) {
            Toast.makeText(this, "Sélectionnez une destination", Toast.LENGTH_SHORT).show()
            return false
        }

        if (etOrigin.text.toString() == etDestination.text.toString()) {
            Toast.makeText(this, "L'origine et la destination doivent être différentes", Toast.LENGTH_SHORT).show()
            return false
        }

        if (departureDate == null) {
            Toast.makeText(this, "Sélectionnez une date de départ", Toast.LENGTH_SHORT).show()
            return false
        }

        if (rbRoundTrip.isChecked && returnDate == null) {
            Toast.makeText(this, "Sélectionnez une date de retour", Toast.LENGTH_SHORT).show()
            return false
        }

        if (adultsCount == 0 && childrenList.isEmpty()) {
            Toast.makeText(this, "Ajoutez au moins un passager", Toast.LENGTH_SHORT).show()
            return false
        }

        return true
    }

    private fun performSearch() {
        val origin = etOrigin.text.toString().substring(0, 3) // Extraire le code (TUN, CDG, etc.)
        val destination = etDestination.text.toString().substring(0, 3)
        
        val tripType = if (rbRoundTrip.isChecked) TripType.ROUND_TRIP else TripType.ONE_WAY
        
        val selectedClass = FlightClass.values()[spinnerClass.selectedItemPosition]
        
        val criteria = FlightSearchCriteria(
            origin = origin,
            destination = destination,
            tripType = tripType,
            departureDate = dateFormat.format(departureDate!!),
            returnDate = returnDate?.let { dateFormat.format(it) },
            adults = adultsCount,
            children = childrenList.toList(),
            selectedClass = selectedClass
        )

        val intent = Intent(this, FlightResultsActivity::class.java)
        intent.putExtra("search_criteria", criteria)
        startActivity(intent)
    }
}
