package com.example.voyageproject.model

data class Hotel(
    val id: String,
    val name: String,
    val address: String,
    val city: String,
    val country: String,
    val etoile: Int,
    val rooms: List<Room>
)