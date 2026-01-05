package com.example.voyageproject.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

// Type de circuit
enum class CircuitType(val displayName: String) {
    CULTUREL("Culturel"),
    AVENTURE("Aventure"),
    DETENTE("Détente"),
    MIXTE("Mixte")
}

// Niveau d'hôtel pour circuit
enum class CircuitHotelLevel(val displayName: String, val priceExtra: Double) {
    STANDARD("Standard (inclus)", 0.0),
    SUPERIOR("Supérieur", 200.0),
    LUXURY("Luxe", 450.0)
}

// Classe de vol pour circuit
enum class CircuitFlightClass(val displayName: String, val priceExtra: Double) {
    ECONOMY("Économique (inclus)", 0.0),
    BUSINESS("Affaires", 400.0),
    FIRST("Première", 900.0)
}

// Programme journalier
@Parcelize
data class CircuitDay(
    val dayNumber: Int,
    val title: String,
    val description: String,
    val meals: List<String> = emptyList(),
    val activities: List<String> = emptyList()
) : Parcelable

// Activité du circuit
@Parcelize
data class CircuitActivity(
    val id: String,
    val title: String,
    val description: String,
    val duration: String,
    val price: Double,
    val isOptional: Boolean,
    val imageUrl: String? = null
) : Parcelable

// Critères de réservation
@Parcelize
data class CircuitBookingCriteria(
    val circuitId: String,
    val departureDate: String,
    val adults: Int,
    val children: List<Int>,  // Âges des enfants
    val hotelLevel: CircuitHotelLevel,
    val flightClass: CircuitFlightClass,
    val selectedActivities: List<String>  // IDs des activités optionnelles
) : Parcelable

// Détail des prix
data class CircuitPriceBreakdown(
    val basePrice: Double,
    val adults: Int,
    val adultsPrice: Double,
    val children: List<Int>,
    val childrenPrice: Double,
    val freeChildren: Int,
    val hotelLevel: CircuitHotelLevel,
    val hotelExtra: Double,
    val flightClass: CircuitFlightClass,
    val flightExtra: Double,
    val activitiesPrice: Double,
    val totalPrice: Double
) {
    fun getFormattedBreakdown(): List<String> {
        val breakdown = mutableListOf<String>()
        
        breakdown.add("Prix de base: ${String.format("%.2f", basePrice)} TND/adulte")
        breakdown.add("")
        
        breakdown.add("Voyageurs:")
        if (adults > 0) {
            breakdown.add("  • $adults adulte${if (adults > 1) "s" else ""}: ${String.format("%.2f", adultsPrice)} TND")
        }
        
        val paidChildren = children.filter { it > 4 }
        paidChildren.forEach { age ->
            val childPrice = basePrice * 0.7
            breakdown.add("  • 1 enfant ($age ans, 70%): ${String.format("%.2f", childPrice)} TND")
        }
        
        if (freeChildren > 0) {
            breakdown.add("  • $freeChildren bébé${if (freeChildren > 1) "s" else ""} (0-4 ans): Gratuit")
        }
        
        breakdown.add("")
        breakdown.add("Suppléments:")
        
        if (hotelExtra > 0) {
            breakdown.add("  • Hôtel ${hotelLevel.displayName}: ${String.format("%.2f", hotelExtra)} TND")
        }
        
        if (flightExtra > 0) {
            breakdown.add("  • Vol ${flightClass.displayName}: ${String.format("%.2f", flightExtra)} TND")
        }
        
        if (activitiesPrice > 0) {
            breakdown.add("  • Activités optionnelles: ${String.format("%.2f", activitiesPrice)} TND")
        }
        
        breakdown.add("")
        breakdown.add("═══════════════════════════")
        breakdown.add("TOTAL: ${String.format("%.2f", totalPrice)} TND")
        
        return breakdown
    }
}

// Calculateur de prix pour circuits
object CircuitPriceCalculator {
    
    fun calculatePrice(
        basePrice: Double,
        adults: Int,
        children: List<Int>,
        hotelLevel: CircuitHotelLevel,
        flightClass: CircuitFlightClass,
        selectedActivities: List<CircuitActivity>
    ): CircuitPriceBreakdown {
        
        // 1. Prix des adultes
        val adultsPrice = adults * basePrice
        
        // 2. Prix des enfants (0-4 gratuit, 5-18 = 70%)
        val freeChildren = children.count { it <= 4 }
        val paidChildren = children.filter { it > 4 }
        
        val childrenPrice = paidChildren.sumOf { age ->
            when {
                age <= 4 -> 0.0
                age <= 18 -> basePrice * 0.7
                else -> basePrice
            }
        }
        
        // 3. Supplément hôtel (par personne payante)
        val totalPayingPersons = adults + paidChildren.size
        val hotelExtra = hotelLevel.priceExtra * totalPayingPersons
        
        // 4. Supplément vol (par personne payante)
        val flightExtra = flightClass.priceExtra * totalPayingPersons
        
        // 5. Activités optionnelles
        val activitiesPrice = selectedActivities.sumOf { it.price }
        
        // Total
        val totalPrice = adultsPrice + childrenPrice + hotelExtra + flightExtra + activitiesPrice
        
        return CircuitPriceBreakdown(
            basePrice = basePrice,
            adults = adults,
            adultsPrice = adultsPrice,
            children = children,
            childrenPrice = childrenPrice,
            freeChildren = freeChildren,
            hotelLevel = hotelLevel,
            hotelExtra = hotelExtra,
            flightClass = flightClass,
            flightExtra = flightExtra,
            activitiesPrice = activitiesPrice,
            totalPrice = totalPrice
        )
    }
}
