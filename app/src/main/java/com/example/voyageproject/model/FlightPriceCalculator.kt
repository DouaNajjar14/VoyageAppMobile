package com.example.voyageproject.model

object FlightPriceCalculator {
    
    fun calculatePrice(
        basePrice: Double,
        flightClass: FlightClass,
        adults: Int,
        children: List<PassengerChild>
    ): FlightPriceBreakdown {
        
        // Prix par personne selon la classe
        val pricePerPerson = basePrice * flightClass.priceMultiplier
        
        // Prix des adultes
        val adultsPrice = adults * pricePerPerson
        
        // Prix des enfants (0-4 gratuit, 5-18 = 50%)
        val freeChildren = children.filter { it.isFree() }
        val paidChildren = children.filter { !it.isFree() }
        
        val childrenPrice = paidChildren.sumOf { 
            pricePerPerson * it.getPriceMultiplier() 
        }
        
        // Total
        val totalPrice = adultsPrice + childrenPrice
        
        return FlightPriceBreakdown(
            basePrice = basePrice,
            flightClass = flightClass,
            pricePerPerson = pricePerPerson,
            adults = adults,
            adultsPrice = adultsPrice,
            paidChildren = paidChildren.size,
            childrenPrice = childrenPrice,
            freeChildren = freeChildren.size,
            totalPrice = totalPrice
        )
    }
}

data class FlightPriceBreakdown(
    val basePrice: Double,
    val flightClass: FlightClass,
    val pricePerPerson: Double,
    val adults: Int,
    val adultsPrice: Double,
    val paidChildren: Int,
    val childrenPrice: Double,
    val freeChildren: Int,
    val totalPrice: Double
) {
    fun getFormattedBreakdown(): List<String> {
        val breakdown = mutableListOf<String>()
        
        breakdown.add("Prix de base: ${String.format("%.2f", basePrice)} TND")
        breakdown.add("Classe ${flightClass.displayName} (×${flightClass.priceMultiplier}): ${String.format("%.2f", pricePerPerson)} TND/personne")
        breakdown.add("")
        
        if (adults > 0) {
            breakdown.add("$adults adulte${if (adults > 1) "s" else ""}: ${String.format("%.2f", adultsPrice)} TND")
        }
        
        if (paidChildren > 0) {
            breakdown.add("$paidChildren enfant${if (paidChildren > 1) "s" else ""} (50%): ${String.format("%.2f", childrenPrice)} TND")
        }
        
        if (freeChildren > 0) {
            breakdown.add("$freeChildren bébé${if (freeChildren > 1) "s" else ""} (gratuit): 0 TND")
        }
        
        breakdown.add("")
        breakdown.add("TOTAL: ${String.format("%.2f", totalPrice)} TND")
        
        return breakdown
    }
}
