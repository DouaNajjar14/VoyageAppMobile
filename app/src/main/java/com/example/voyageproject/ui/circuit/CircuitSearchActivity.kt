package com.example.voyageproject.ui.circuit

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.voyageproject.R
import com.google.android.material.datepicker.MaterialDatePicker
import java.text.SimpleDateFormat
import java.util.*

class CircuitSearchActivity : AppCompatActivity() {

    private var adults = 2
    private var children = 0
    private var departureDate: Date? = null
    private var duration = 7
    private var circuitType = "MIXTE"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_circuit_search)

        setupViews()
        setupListeners()
    }

    private fun setupViews() {
        // Date par défaut (dans 30 jours)
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_YEAR, 30)
        departureDate = calendar.time
        updateDateDisplay()

        // Voyageurs
        findViewById<TextView>(R.id.tvAdultsCount).text = adults.toString()
        findViewById<TextView>(R.id.tvChildrenCount).text = children.toString()

        // Durée par défaut
        findViewById<RadioGroup>(R.id.rgDuration).check(R.id.rbDuration7)

        // Type par défaut
        findViewById<RadioGroup>(R.id.rgCircuitType).check(R.id.rbTypeMixte)
    }

    private fun setupListeners() {
        findViewById<ImageButton>(R.id.btnBack).setOnClickListener { finish() }

        // Adultes
        findViewById<ImageButton>(R.id.btnAdultsPlus).setOnClickListener {
            if (adults < 10) {
                adults++
                findViewById<TextView>(R.id.tvAdultsCount).text = adults.toString()
            }
        }

        findViewById<ImageButton>(R.id.btnAdultsMinus).setOnClickListener {
            if (adults > 1) {
                adults--
                findViewById<TextView>(R.id.tvAdultsCount).text = adults.toString()
            }
        }

        // Enfants
        findViewById<ImageButton>(R.id.btnChildrenPlus).setOnClickListener {
            if (children < 10) {
                children++
                findViewById<TextView>(R.id.tvChildrenCount).text = children.toString()
            }
        }

        findViewById<ImageButton>(R.id.btnChildrenMinus).setOnClickListener {
            if (children > 0) {
                children--
                findViewById<TextView>(R.id.tvChildrenCount).text = children.toString()
            }
        }

        // Date
        findViewById<LinearLayout>(R.id.layoutDepartureDate).setOnClickListener {
            showDatePicker()
        }

        // Durée
        findViewById<RadioGroup>(R.id.rgDuration).setOnCheckedChangeListener { _, checkedId ->
            duration = when (checkedId) {
                R.id.rbDuration3 -> 3
                R.id.rbDuration7 -> 7
                R.id.rbDuration10 -> 10
                else -> 7
            }
        }

        // Type de circuit
        findViewById<RadioGroup>(R.id.rgCircuitType).setOnCheckedChangeListener { _, checkedId ->
            circuitType = when (checkedId) {
                R.id.rbTypeCulturel -> "CULTUREL"
                R.id.rbTypeAventure -> "AVENTURE"
                R.id.rbTypeDetente -> "DETENTE"
                R.id.rbTypeMixte -> "MIXTE"
                else -> "MIXTE"
            }
        }

        // Bouton rechercher
        findViewById<Button>(R.id.btnSearch).setOnClickListener {
            searchCircuits()
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

    private fun searchCircuits() {
        val destination = findViewById<EditText>(R.id.etDestination).text.toString()
        val budgetText = findViewById<EditText>(R.id.etBudget).text.toString()
        val budget = budgetText.toDoubleOrNull()

        // Lancer l'activité de résultats avec les critères
        val intent = Intent(this, CircuitResultsActivity::class.java)
        intent.putExtra("destination", destination)
        intent.putExtra("duration", duration)
        intent.putExtra("budget", budget ?: 0.0)
        intent.putExtra("circuitType", circuitType)
        intent.putExtra("adults", adults)
        intent.putExtra("children", children)
        intent.putExtra("departureDate", departureDate?.time ?: 0L)
        startActivity(intent)
    }
}
