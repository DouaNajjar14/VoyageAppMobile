package com.example.voyageproject.model

data class BookingRequest(
    val email: String,
    val offerType: String, // "hotel", "flight", "circuit"
    val offerId: String,
    val paymentMethod: String, // "card", "paypal", "wallet"
    val paymentDetails: Map<String, String>? = null
)
