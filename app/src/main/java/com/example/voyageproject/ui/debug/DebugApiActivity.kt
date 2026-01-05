package com.example.voyageproject.ui.debug

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.voyageproject.databinding.ActivityDebugApiBinding
import com.example.voyageproject.network.RetrofitClient
import kotlinx.coroutines.launch

class DebugApiActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDebugApiBinding
    private val api = RetrofitClient.api

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDebugApiBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnTestHotels.setOnClickListener { testHotels() }
        binding.btnTestFlights.setOnClickListener { testFlights() }
        binding.btnTestCircuits.setOnClickListener { testCircuits() }
        binding.btnTestAll.setOnClickListener { testAll() }
    }

    private fun testHotels() {
        binding.tvResults.text = "Test des hôtels...\n"
        lifecycleScope.launch {
            try {
                val response = api.getHotels()
                binding.tvResults.append("Code: ${response.code()}\n")
                binding.tvResults.append("Success: ${response.isSuccessful}\n")
                binding.tvResults.append("Body: ${response.body()}\n")
                binding.tvResults.append("Size: ${response.body()?.size}\n")
            } catch (e: Exception) {
                binding.tvResults.append("ERREUR: ${e.message}\n")
                binding.tvResults.append("Type: ${e.javaClass.simpleName}\n")
                e.printStackTrace()
            }
        }
    }

    private fun testFlights() {
        binding.tvResults.text = "Test des vols...\n"
        lifecycleScope.launch {
            try {
                val response = api.getFlights()
                binding.tvResults.append("Code: ${response.code()}\n")
                binding.tvResults.append("Success: ${response.isSuccessful}\n")
                binding.tvResults.append("Body: ${response.body()}\n")
                binding.tvResults.append("Size: ${response.body()?.size}\n")
            } catch (e: Exception) {
                binding.tvResults.append("ERREUR: ${e.message}\n")
                binding.tvResults.append("Type: ${e.javaClass.simpleName}\n")
                e.printStackTrace()
            }
        }
    }

    private fun testCircuits() {
        binding.tvResults.text = "Test des circuits...\n"
        binding.tvResults.append("URL: ${RetrofitClient.api}\n\n")
        
        lifecycleScope.launch {
            try {
                binding.tvResults.append("Appel de l'API...\n")
                val response = api.getCircuits()
                
                binding.tvResults.append("\n=== RÉSULTAT ===\n")
                binding.tvResults.append("Code HTTP: ${response.code()}\n")
                binding.tvResults.append("Message: ${response.message()}\n")
                binding.tvResults.append("Success: ${response.isSuccessful}\n")
                binding.tvResults.append("Body: ${response.body()}\n")
                binding.tvResults.append("Body null?: ${response.body() == null}\n")
                binding.tvResults.append("Size: ${response.body()?.size}\n")
                
                if (!response.isSuccessful) {
                    binding.tvResults.append("\nError Body: ${response.errorBody()?.string()}\n")
                }
                
                response.body()?.forEach { circuit ->
                    binding.tvResults.append("\nCircuit: ${circuit.title}\n")
                }
                
            } catch (e: Exception) {
                binding.tvResults.append("\n=== EXCEPTION ===\n")
                binding.tvResults.append("Message: ${e.message}\n")
                binding.tvResults.append("Type: ${e.javaClass.simpleName}\n")
                binding.tvResults.append("Cause: ${e.cause}\n")
                binding.tvResults.append("\nStack trace:\n")
                binding.tvResults.append(e.stackTraceToString())
                e.printStackTrace()
            }
        }
    }

    private fun testAll() {
        binding.tvResults.text = "Test de tous les endpoints...\n\n"
        
        lifecycleScope.launch {
            // Test Hotels
            binding.tvResults.append("=== HOTELS ===\n")
            try {
                val hotelsResponse = api.getHotels()
                binding.tvResults.append("✓ Code: ${hotelsResponse.code()}, Size: ${hotelsResponse.body()?.size}\n\n")
            } catch (e: Exception) {
                binding.tvResults.append("✗ Erreur: ${e.message}\n\n")
            }

            // Test Flights
            binding.tvResults.append("=== FLIGHTS ===\n")
            try {
                val flightsResponse = api.getFlights()
                binding.tvResults.append("✓ Code: ${flightsResponse.code()}, Size: ${flightsResponse.body()?.size}\n\n")
            } catch (e: Exception) {
                binding.tvResults.append("✗ Erreur: ${e.message}\n\n")
            }

            // Test Circuits
            binding.tvResults.append("=== CIRCUITS ===\n")
            try {
                val circuitsResponse = api.getCircuits()
                binding.tvResults.append("✓ Code: ${circuitsResponse.code()}, Size: ${circuitsResponse.body()?.size}\n\n")
            } catch (e: Exception) {
                binding.tvResults.append("✗ Erreur: ${e.message}\n\n")
            }
        }
    }
}
