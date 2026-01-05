package com.example.voyageproject.ui.flight

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.voyageproject.R
import com.example.voyageproject.model.Flight
import com.example.voyageproject.model.FlightSearchCriteria
import com.example.voyageproject.network.RetrofitClient
import kotlinx.coroutines.launch

class FlightResultsActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var tvNoResults: TextView
    private lateinit var tvResultsCount: TextView
    
    private lateinit var searchCriteria: FlightSearchCriteria
    private val flights = mutableListOf<Flight>()
    private lateinit var adapter: FlightResultAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_flight_results)

        searchCriteria = intent.getParcelableExtra("search_criteria")!!

        initViews()
        setupRecyclerView()
        searchFlights()
    }

    private fun initViews() {
        recyclerView = findViewById(R.id.recyclerViewFlights)
        progressBar = findViewById(R.id.progressBar)
        tvNoResults = findViewById(R.id.tvNoResults)
        tvResultsCount = findViewById(R.id.tvResultsCount)
        
        supportActionBar?.title = "${searchCriteria.origin} → ${searchCriteria.destination}"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    private fun setupRecyclerView() {
        adapter = FlightResultAdapter(flights, searchCriteria) { flight ->
            openFlightDetails(flight)
        }
        
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter
    }

    private fun searchFlights() {
        progressBar.visibility = View.VISIBLE
        tvNoResults.visibility = View.GONE
        recyclerView.visibility = View.GONE

        lifecycleScope.launch {
            try {
                val response = RetrofitClient.api.searchFlights(
                    origin = searchCriteria.origin,
                    destination = searchCriteria.destination,
                    departureDate = searchCriteria.departureDate,
                    totalPassengers = searchCriteria.getTotalPassengers(),
                    minPrice = null,
                    maxPrice = null,
                    sortBy = null
                )

                progressBar.visibility = View.GONE

                if (response.isNotEmpty()) {
                    flights.clear()
                    flights.addAll(response)
                    adapter.notifyDataSetChanged()
                    
                    recyclerView.visibility = View.VISIBLE
                    tvResultsCount.visibility = View.VISIBLE
                    tvResultsCount.text = "${response.size} vol(s) trouvé(s)"
                } else {
                    tvNoResults.visibility = View.VISIBLE
                    tvResultsCount.visibility = View.GONE
                }

            } catch (e: Exception) {
                progressBar.visibility = View.GONE
                tvNoResults.visibility = View.VISIBLE
                tvNoResults.text = "Erreur: ${e.message}"
                Toast.makeText(this@FlightResultsActivity, "Erreur: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun openFlightDetails(flight: Flight) {
        val intent = Intent(this, FlightDetailsActivity::class.java)
        intent.putExtra("flight", flight)
        intent.putExtra("search_criteria", searchCriteria)
        startActivity(intent)
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}
