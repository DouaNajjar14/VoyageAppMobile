package com.example.voyageproject.model

enum class FlightClass(
    val displayName: String,
    val priceMultiplier: Double,
    val baggage: String,
    val seat: String,
    val meal: String,
    val refundable: Boolean
) {
    ECONOMY(
        displayName = "Économique",
        priceMultiplier = 1.0,
        baggage = "1 cabine",
        seat = "Standard",
        meal = "Simple",
        refundable = false
    ),
    PREMIUM_ECONOMY(
        displayName = "Économie Premium",
        priceMultiplier = 1.3,
        baggage = "1 cabine + 1 soute",
        seat = "Large avec plus d'espace",
        meal = "Premium",
        refundable = false
    ),
    BUSINESS(
        displayName = "Affaires",
        priceMultiplier = 1.7,
        baggage = "2 soutes",
        seat = "Inclinable (lie-flat)",
        meal = "Gourmet",
        refundable = true
    ),
    FIRST(
        displayName = "Première Classe",
        priceMultiplier = 2.5,
        baggage = "3 soutes",
        seat = "Suite privée",
        meal = "Chef",
        refundable = true
    );

    fun getFeatures(): List<String> {
        val features = mutableListOf<String>()
        features.add("✈️ Siège: $seat")
        features.add("🧳 Bagages: $baggage")
        features.add("🍽️ Repas: $meal")
        
        when (this) {
            ECONOMY -> {
                features.add("💺 Siège standard")
                features.add("📱 Divertissement de base")
            }
            PREMIUM_ECONOMY -> {
                features.add("✨ Plus d'espace jambes")
                features.add("🎯 Priorité embarquement")
                features.add("📱 Divertissement premium")
            }
            BUSINESS -> {
                features.add("🛋️ Salon VIP inclus")
                features.add("🔄 Billet flexible")
                features.add("🎁 Kit de voyage offert")
                features.add("📶 Wi-Fi gratuit")
            }
            FIRST -> {
                features.add("🏆 Suite privée")
                features.add("🚗 Chauffeur privé")
                features.add("👔 Service de conciergerie")
                features.add("🍾 Service luxe")
            }
        }
        
        if (refundable) {
            features.add("✅ Remboursable")
        } else {
            features.add("❌ Non remboursable")
        }
        
        return features
    }
}
