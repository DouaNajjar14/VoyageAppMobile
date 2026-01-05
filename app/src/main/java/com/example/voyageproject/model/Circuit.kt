package com.example.voyageproject.model

import com.google.gson.annotations.JsonAdapter
import com.example.voyageproject.utils.CircuitDeserializer

@JsonAdapter(CircuitDeserializer::class)
data class Circuit(
    val id: String,
    val title: String,
    val description: String,
    val duree: Int,
    val prix: Double,
    val imageUrl: String? = null,
    val destinations: List<String>? = null,
    val includes: List<String>? = null,
    val hotelNames: List<String> = emptyList()
)
