package com.example.voyageproject.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class Reservation(
    val id: String,
    val clientEmail: String,
    val offerType: String, // "hotel", "flight", "circuit"
    val offerId: String,
    val offerName: String,
    val price: Double,
    val bookingDate: String,
    val status: String, // "confirmed", "cancelled", "pending"
    val paymentMethod: String? = null,
    val details: Map<String, Any>? = null,
    val formula: String? = null, // "petit_dejeuner", "demi_pension", "pension_complete", "all_inclusive"
    val startDate: java.time.LocalDate? = null,
    val endDate: java.time.LocalDate? = null,
    @SerializedName("adultsCount") val adultsCount: Int? = null,
    @SerializedName("childrenCount") val childrenCount: Int? = null,
    @SerializedName("childrenAges") val childrenAges: String? = null, // JSON array des âges
    @SerializedName("hotelLevel") val hotelLevel: String? = null, // "STANDARD", "SUPERIOR", "LUXURY"
    @SerializedName("flightClass") val flightClass: String? = null, // "ECONOMY", "BUSINESS", "FIRST"
    @SerializedName("selectedActivities") val selectedActivities: String? = null, // JSON des activités
    @SerializedName("priceBreakdown") val priceBreakdown: String? = null // JSON du détail des prix
) : Serializable