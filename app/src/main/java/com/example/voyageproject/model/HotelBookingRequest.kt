package com.example.voyageproject.model

data class HotelBookingRequest(
    val hotelId: String,
    val hotelName: String,
    val roomId: String,
    val roomName: String,
    val checkInDate: String,
    val checkOutDate: String,
    val numberOfNights: Int,
    val numberOfAdults: Int,
    val numberOfChildren: Int,
    val totalGuests: Int,
    val pricePerNight: Double,
    val totalPrice: Double,
    val guestInfo: GuestInfo,
    val specialRequests: String? = null,
    val paymentMethod: String
)

data class GuestInfo(
    val firstName: String,
    val lastName: String,
    val email: String,
    val phone: String,
    val country: String? = null,
    val arrivalTime: String? = null
)
