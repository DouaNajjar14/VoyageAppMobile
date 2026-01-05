package com.example.voyageproject.model

data class Client(
    val id: String? = null,
    val firstName: String,
    val lastName: String,
    val email: String,
    val password: String,
    val telephone: String,
    val enabled: Boolean = false,
    val preferences: TravelPreferences? = null
)

data class TravelPreferences(
    val budget: Double? = null,
    val preferredLanguages: List<String>? = null,
    val preferredDestinations: List<String>? = null,
    val travelStyle: String? = null // "luxury", "budget", "adventure", "relaxation"
)