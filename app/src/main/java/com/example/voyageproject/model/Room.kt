package com.example.voyageproject.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Room(
    val id: String,
    val name: String,
    val price: Double,
    val capacity: Int,
    val size: Int? = null, // en m²
    val bedType: String? = null, // "Double", "Twin", "King", "Single"
    val amenities: List<String>? = null,
    val available: Boolean = true,
    val maxGuests: Int? = null,
    val imageUrl: String? = null,
    val description: String? = null,
    val cancellationPolicy: String? = null,
    val breakfastIncluded: Boolean = false,
    
    // Nouvelles options
    val roomType: String? = null, // "Single", "Double", "Triple", "Quadruple"
    val hasAirConditioning: Boolean = true,
    val hasPoolView: Boolean = false,
    val hasSeaView: Boolean = false,
    val hasCityView: Boolean = false,
    val hasGardenView: Boolean = false,
    val hasBalcony: Boolean = false,
    val hasBathroom: Boolean = true,
    val hasTV: Boolean = true,
    val hasWifi: Boolean = true,
    val hasMinibar: Boolean = false,
    val hasSafe: Boolean = false,
    val smokingAllowed: Boolean = false,
    val floor: Int? = null,
    
    // Prix supplémentaires
    val poolViewPrice: Double = 0.0,
    val seaViewPrice: Double = 0.0,
    val balconyPrice: Double = 0.0,
    val extraAdultPrice: Double = 0.0,
    val extraChildPrice: Double = 0.0
) : Parcelable
