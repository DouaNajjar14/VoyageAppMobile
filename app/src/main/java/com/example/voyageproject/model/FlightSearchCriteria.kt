package com.example.voyageproject.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class FlightSearchCriteria(
    val origin: String,              // "TUN"
    val destination: String,         // "CDG"
    val tripType: TripType,          // ONE_WAY ou ROUND_TRIP
    val departureDate: String,       // "2026-01-15"
    val returnDate: String?,         // null si aller simple
    val adults: Int,                 // Nombre d'adultes (18+)
    val children: List<PassengerChild>, // Liste des enfants avec âges
    val selectedClass: FlightClass   // Classe de vol sélectionnée
) : Parcelable {
    fun getTotalPassengers(): Int {
        return adults + children.size
    }
    
    fun getPassengersSummary(): String {
        val parts = mutableListOf<String>()
        
        if (adults > 0) {
            parts.add("$adults adulte${if (adults > 1) "s" else ""}")
        }
        
        val freeChildren = children.count { it.isFree() }
        val paidChildren = children.count { !it.isFree() }
        
        if (paidChildren > 0) {
            parts.add("$paidChildren enfant${if (paidChildren > 1) "s" else ""} (5-18 ans)")
        }
        
        if (freeChildren > 0) {
            parts.add("$freeChildren bébé${if (freeChildren > 1) "s" else ""} (0-4 ans)")
        }
        
        return parts.joinToString(", ")
    }
}
