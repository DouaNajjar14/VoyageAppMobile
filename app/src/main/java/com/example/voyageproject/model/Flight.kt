package com.example.voyageproject.model

import java.io.Serializable

data class Flight(
    val id: String,
    val airline: String,
    val flightNumber: String,
    val origin: String,
    val destination: String,
    val departureTime: String,
    val arrivalTime: String,
    val price: Double,
    val seatsAvailable: Int,
    val imageUrl: String? = null,
    val duration: String? = null,
    val class_type: String? = "Economy"
) : Serializable