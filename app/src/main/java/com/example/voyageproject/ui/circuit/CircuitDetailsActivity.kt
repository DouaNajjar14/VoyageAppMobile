package com.example.voyageproject.ui.circuit

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.voyageproject.R
import com.example.voyageproject.model.*
import com.example.voyageproject.repository.CircuitRepository
import com.google.gson.Gson
import kotlinx.coroutines.launch

class CircuitDetailsActivity : AppCompatActivity() {

    private lateinit var ivCircuitImage: ImageView
    private lateinit var tvCircuitTitle: TextView
    private lateinit var tvCircuitDuration: TextView
    private lateinit var tvCircuitPrice: TextView
    private lateinit var tvCircuitDescription: TextView
    private lateinit var tvCircuitIncluded: TextView
    private lateinit var rvProgram: RecyclerView
    private lateinit var rvActivities: RecyclerView
    private lateinit var cardActivities: CardView
    private lateinit var btnReserve: Button
    
    private lateinit var circuit: Circuit
    private val repository = CircuitRepository()
    
    private var programDays = listOf<CircuitDay>()
    private var activities = listOf<CircuitActivity>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_circuit_details)

        // Récupérer le circuit
        val circuitData = intent.getStringExtra("circuitData")
        if (circuitData == null) {
            Toast.makeText(this, "Erreur: données manquantes", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        circuit = Gson().fromJson(circuitData, Circuit::class.java)
        
        initViews()
        setupToolbar()
        displayCircuitInfo()
        loadProgramAndActivities()
        setupReserveButton()
    }

    private fun initViews() {
        ivCircuitImage = findViewById(R.id.ivCircuitImage)
        tvCircuitTitle = findViewById(R.id.tvCircuitTitle)
        tvCircuitDuration = findViewById(R.id.tvCircuitDuration)
        tvCircuitPrice = findViewById(R.id.tvCircuitPrice)
        tvCircuitDescription = findViewById(R.id.tvCircuitDescription)
        tvCircuitIncluded = findViewById(R.id.tvCircuitIncluded)
        rvProgram = findViewById(R.id.rvProgram)
        rvActivities = findViewById(R.id.rvActivities)
        cardActivities = findViewById(R.id.cardActivities)
        btnReserve = findViewById(R.id.btnReserve)
    }

    private fun setupToolbar() {
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Détails du circuit"
    }

    private fun displayCircuitInfo() {
        // Image
        Glide.with(this)
            .load(circuit.imageUrl)
            .placeholder(R.drawable.gradient_red_orange)
            .into(ivCircuitImage)

        // Infos de base
        tvCircuitTitle.text = circuit.title
        tvCircuitDuration.text = "🗓️ ${circuit.duree} jours / ${circuit.duree - 1} nuits"
        tvCircuitPrice.text = "À partir de ${String.format("%.0f", circuit.prix)} TND"
        tvCircuitDescription.text = circuit.description

        // Ce qui est inclus
        val included = circuit.includes ?: listOf(
            "Vol aller-retour",
            "Hôtel 4★ centre-ville",
            "Petit-déjeuner quotidien",
            "Guide francophone",
            "Transferts aéroport"
        )
        tvCircuitIncluded.text = included.joinToString("\n") { "• $it" }
    }

    private fun loadProgramAndActivities() {
        lifecycleScope.launch {
            try {
                // Charger le programme
                val programResponse = repository.getCircuitProgram(circuit.id)
                if (programResponse.isSuccessful) {
                    programDays = programResponse.body() ?: emptyList()
                    setupProgramRecyclerView()
                } else {
                    Log.e("CIRCUIT", "Erreur programme: ${programResponse.code()}")
                }

                // Charger les activités (mais ne pas les afficher ici)
                val activitiesResponse = repository.getCircuitActivities(circuit.id)
                if (activitiesResponse.isSuccessful) {
                    activities = activitiesResponse.body() ?: emptyList()
                    // Masquer la section activités dans les détails
                    cardActivities.visibility = View.GONE
                } else {
                    Log.e("CIRCUIT", "Erreur activités: ${activitiesResponse.code()}")
                    cardActivities.visibility = View.GONE
                }
            } catch (e: Exception) {
                Log.e("CIRCUIT", "Erreur: ${e.message}", e)
                Toast.makeText(this@CircuitDetailsActivity, 
                    "Erreur de chargement des détails", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupProgramRecyclerView() {
        rvProgram.layoutManager = LinearLayoutManager(this)
        rvProgram.adapter = CircuitDayAdapter(programDays)
    }

    private fun setupActivitiesRecyclerView() {
        rvActivities.layoutManager = LinearLayoutManager(this)
        // Adapter simple pour l'aperçu (sans checkbox)
        rvActivities.adapter = CircuitDayAdapter(emptyList()) // Placeholder
        
        // Afficher les activités de manière simple
        val activitiesText = activities.joinToString("\n") { 
            "• ${it.title} (+${String.format("%.0f", it.price)} TND)" 
        }
        // Pour simplifier, on peut juste afficher le texte
        // ou créer un adapter spécifique pour l'aperçu
    }

    private fun setupReserveButton() {
        btnReserve.setOnClickListener {
            // Lancer l'activité de personnalisation de réservation
            val intent = Intent(this, CircuitBookingActivity::class.java)
            intent.putExtra("circuitData", Gson().toJson(circuit))
            intent.putExtra("programDays", Gson().toJson(programDays))
            intent.putExtra("activities", Gson().toJson(activities))
            
            // Passer les critères de recherche si disponibles
            intent.putExtra("adults", this.intent.getIntExtra("adults", 2))
            intent.putExtra("children", this.intent.getIntExtra("children", 0))
            intent.putExtra("departureDate", this.intent.getLongExtra("departureDate", 0L))
            
            startActivity(intent)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}
