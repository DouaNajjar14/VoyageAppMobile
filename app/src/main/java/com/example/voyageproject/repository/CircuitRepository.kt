package com.example.voyageproject.repository

import com.example.voyageproject.model.*
import com.example.voyageproject.network.RetrofitClient
import retrofit2.Response

class CircuitRepository {
    
    private val api = RetrofitClient.api
    
    suspend fun getAllCircuits(): Response<List<Circuit>> {
        return api.getCircuits()
    }
    
    suspend fun searchCircuits(
        destination: String? = null,
        duration: Int? = null,
        minPrice: Double? = null,
        maxPrice: Double? = null,
        sortBy: String? = null
    ): Response<List<Circuit>> {
        return api.searchCircuits(destination, duration, minPrice, maxPrice, sortBy)
    }
    
    suspend fun getCircuitProgram(circuitId: String): Response<List<CircuitDay>> {
        return api.getCircuitProgram(circuitId)
    }
    
    suspend fun getCircuitActivities(circuitId: String): Response<List<CircuitActivity>> {
        return api.getCircuitActivities(circuitId)
    }
    
    suspend fun calculatePrice(
        circuitId: String,
        adults: Int,
        children: List<Int>,
        hotelLevel: String,
        flightClass: String,
        selectedActivities: List<String>
    ): Response<Map<String, Any>> {
        val request = mapOf(
            "adults" to adults,
            "children" to children,
            "hotelLevel" to hotelLevel,
            "flightClass" to flightClass,
            "selectedActivities" to selectedActivities
        )
        return api.calculateCircuitPrice(circuitId, request)
    }
}
