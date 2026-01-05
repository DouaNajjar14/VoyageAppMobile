package com.example.voyageproject.ui.circuit

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.voyageproject.R
import com.example.voyageproject.model.Circuit
import com.example.voyageproject.repository.CircuitRepository
import com.google.gson.Gson
import kotlinx.coroutines.launch
import java.util.Date

class CircuitResultsActivity : AppCompatActivity() {

    private lateinit var rvCircuits: RecyclerView
    private lateinit var layoutNoResults: LinearLayout
    private lateinit var tvSearchCriteria: TextView
    private val repository = CircuitRepository()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_circuit_results)

        setupViews()
        displaySearchCriteria()
        loadCircuits()
    }

    private fun setupViews() {
        findViewById<ImageButton>(R.id.btnBack).setOnClickListener { finish() }
        
        rvCircuits = findViewById(R.id.rvCircuits)
        layoutNoResults = findViewById(R.id.layoutNoResults)
        tvSearchCriteria = findViewById(R.id.tvSearchCriteria)
        
        rvCircuits.layoutManager = LinearLayoutManager(this)
    }

    private fun displaySearchCriteria() {
        val adults = intent.getIntExtra("adults", 2)
        val children = intent.getIntExtra("children", 0)
        val duration = intent.getIntExtra("duration", 7)
        
        val criteria = buildString {
            append("$adults adulte${if (adults > 1) "s" else ""}")
            if (children > 0) {
                append(", $children enfant${if (children > 1) "s" else ""}")
            }
            append(" • $duration jours")
        }
        
        tvSearchCriteria.text = criteria
    }

    private fun loadCircuits() {
        lifecycleScope.launch {
            try {
                val response = repository.getAllCircuits()
                if (response.isSuccessful) {
                    val circuits = response.body() ?: emptyList()
                    
                    // Filtrer selon les critères
                    val filteredCircuits = filterCircuits(circuits)
                    
                    if (filteredCircuits.isNotEmpty()) {
                        displayCircuits(filteredCircuits)
                    } else {
                        showNoResults()
                    }
                } else {
                    Toast.makeText(
                        this@CircuitResultsActivity,
                        "Erreur de chargement",
                        Toast.LENGTH_SHORT
                    ).show()
                    showNoResults()
                }
            } catch (e: Exception) {
                Toast.makeText(
                    this@CircuitResultsActivity,
                    "Erreur: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
                showNoResults()
            }
        }
    }

    private fun filterCircuits(circuits: List<Circuit>): List<Circuit> {
        val duration = intent.getIntExtra("duration", 0)
        val budget = intent.getDoubleExtra("budget", 0.0)
        val destination = intent.getStringExtra("destination") ?: ""
        
        return circuits.filter { circuit ->
            // Filtre par durée
            val durationMatch = if (duration > 0) {
                circuit.duree == duration
            } else true
            
            // Filtre par budget
            val budgetMatch = if (budget > 0) {
                circuit.prix <= budget
            } else true
            
            // Filtre par destination
            val destinationMatch = if (destination.isNotEmpty()) {
                circuit.destinations?.any { 
                    it.contains(destination, ignoreCase = true) 
                } == true || circuit.title.contains(destination, ignoreCase = true)
            } else true
            
            durationMatch && budgetMatch && destinationMatch
        }
    }

    private fun displayCircuits(circuits: List<Circuit>) {
        rvCircuits.visibility = View.VISIBLE
        layoutNoResults.visibility = View.GONE
        
        // Récupérer la date de départ
        val dateMillis = intent.getLongExtra("departureDate", 0L)
        val departureDate = if (dateMillis > 0) Date(dateMillis) else null
        
        val adapter = CircuitCardAdapter(circuits, departureDate) { circuit ->
            openCircuitDetails(circuit)
        }
        rvCircuits.adapter = adapter
    }

    private fun showNoResults() {
        rvCircuits.visibility = View.GONE
        layoutNoResults.visibility = View.VISIBLE
    }

    private fun openCircuitDetails(circuit: Circuit) {
        val intent = Intent(this, CircuitDetailsActivity::class.java)
        intent.putExtra("circuitData", Gson().toJson(circuit))
        
        // Passer aussi les critères de recherche
        intent.putExtra("adults", this.intent.getIntExtra("adults", 2))
        intent.putExtra("children", this.intent.getIntExtra("children", 0))
        intent.putExtra("departureDate", this.intent.getLongExtra("departureDate", 0L))
        
        startActivity(intent)
    }
}
