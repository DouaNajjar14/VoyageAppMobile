package com.example.voyageproject.model

data class Flight(
    val id: String,
    val airline: String,
    val flightNumber: String,
    val origin: String,
    val destination: String,
    val departureTime: String,
    val arrivalTime: String,
    val price: Double,
    val seatsAvailable: Int
)