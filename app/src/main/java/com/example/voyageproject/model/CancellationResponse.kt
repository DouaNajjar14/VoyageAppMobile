package com.example.voyageproject.model

data class CancellationResponse(
    val success: Boolean,
    val message: String? = null,
    val error: String? = null,
    val reservationId: String? = null,
    val offerType: String? = null,
    val offerName: String? = null,
    val originalPrice: Double? = null,
    val refundPercentage: Double? = null,
    val refundAmount: Double? = null,
    val penaltyAmount: Double? = null,
    val penaltyMessage: String? = null,
    val daysUntilStart: Long? = null,
    val hoursUntilStart: Long? = null,
    val canCancel: Boolean? = null,
    val clientEmail: String? = null,
    val currentStatus: String? = null
)
