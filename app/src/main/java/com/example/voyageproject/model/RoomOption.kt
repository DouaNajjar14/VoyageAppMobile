package com.example.voyageproject.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

data class RoomOption(
    val id: String,
    val hotelId: String,
    val roomType: String,
    val capacity: Int,
    val bedType: String,
    val basePrice: Double,
    val imageUrl: String,
    val description: String,
    val amenities: List<String>,
    val viewOptions: List<ViewOption>,
    val mealOptions: List<MealOption>
)

data class ViewOption(
    val type: String, // "garden", "sea", "pool", "none"
    val label: String,
    val pricePerNight: Double
)

data class MealOption(
    val type: String, // "none", "breakfast", "half_board", "all_inclusive"
    val label: String,
    val pricePerNight: Double
)

@Parcelize
data class BookingDetails(
    val hotelId: String,
    val hotelName: String,
    val hotelCity: String,
    val roomType: String,
    val checkInDate: String,
    val checkOutDate: String,
    val numberOfNights: Int,
    val numberOfAdults: Int,
    val numberOfChildren: Int,
    val viewType: String,
    val viewLabel: String,
    val mealPlan: String,
    val mealLabel: String,
    val basePrice: Double,
    val viewPrice: Double,
    val mealPrice: Double,
    val totalPrice: Double
) : Parcelable
