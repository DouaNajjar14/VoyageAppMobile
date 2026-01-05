package com.example.voyageproject.model

import com.google.gson.annotations.SerializedName

data class ReservationDetails(
    val id: String,
    val clientEmail: String,
    val offerType: String,
    val offerName: String,
    val price: Double,
    val bookingDate: String,
    val status: Any?, // Peut être String ou Object
    val paymentMethod: Any?, // Peut être String ou Object
    val startDate: String?,
    val endDate: String?,
    val adultsCount: Int?,
    val childrenCount: Int?,
    val formula: Any?, // Peut être String ou Array
    val hotel: HotelDetails?,
    val room: RoomDetails?,
    val payment: PaymentDetails?
) {
    fun getPaymentMethodString(): String {
        return when (paymentMethod) {
            is String -> paymentMethod
            else -> paymentMethod?.toString() ?: "Non spécifié"
        }
    }
    
    fun getStatusString(): String {
        return when (status) {
            is String -> status
            else -> status?.toString() ?: "Unknown"
        }
    }
    
    fun getFormulaString(): String? {
        return when (formula) {
            is String -> formula
            is List<*> -> (formula as? List<*>)?.firstOrNull()?.toString()
            else -> null
        }
    }
}

data class HotelDetails(
    val name: String,
    val address: String,
    val city: String,
    val country: String,
    val stars: Int,
    val phone: String?,
    val email: String?,
    val checkInTime: String?,
    val checkOutTime: String?,
    val imageUrl: Any? // Peut être String ou Array
) {
    fun getImageUrlString(): String? {
        return when (imageUrl) {
            is String -> imageUrl
            is List<*> -> (imageUrl as? List<*>)?.firstOrNull()?.toString()
            else -> null
        }
    }
}

data class RoomDetails(
    val roomNumber: String,
    val roomType: Any?, // Peut être String ou Array
    val maxOccupancy: Int?,
    val viewType: Any?, // Peut être String ou Array
    val bedType: Any?, // Peut être String ou Array
    val sizeSqm: Double?,
    val description: String?,
    val pricePerNight: Double?,
    val viewSupplement: Double?
) {
    fun getRoomTypeString(): String {
        return when (roomType) {
            is String -> roomType
            is List<*> -> (roomType as? List<*>)?.firstOrNull()?.toString() ?: "Standard"
            else -> "Standard"
        }
    }
    
    fun getViewTypeString(): String? {
        return when (viewType) {
            is String -> viewType
            is List<*> -> (viewType as? List<*>)?.firstOrNull()?.toString()
            else -> null
        }
    }
    
    fun getBedTypeString(): String? {
        return when (bedType) {
            is String -> bedType
            is List<*> -> (bedType as? List<*>)?.firstOrNull()?.toString()
            else -> null
        }
    }
}

data class PaymentDetails(
    val id: String,
    val amount: Double,
    val paymentMethod: Any, // Peut être String ou Object
    val status: Any, // Peut être String ou Object
    val transactionId: String?,
    val paymentDate: String?
) {
    fun getPaymentMethodString(): String {
        return when (paymentMethod) {
            is String -> paymentMethod
            else -> paymentMethod.toString()
        }
    }
    
    fun getStatusString(): String {
        return when (status) {
            is String -> status
            else -> status.toString()
        }
    }
}
