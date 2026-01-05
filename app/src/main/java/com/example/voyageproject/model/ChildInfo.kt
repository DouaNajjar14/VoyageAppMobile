package com.example.voyageproject.model

data class ChildInfo(
    val age: Int
) {
    fun getPrice(basePrice: Double): Double {
        return when {
            age in 0..4 -> 0.0  // Gratuit
            age in 5..18 -> basePrice * 0.5  // 50% du prix
            else -> basePrice
        }
    }
    
    fun isFree(): Boolean = age in 0..4
    
    fun getActivityPrice(activityPrice: Double): Double {
        return when {
            age in 0..4 -> 0.0  // Activités gratuites
            age in 5..18 -> activityPrice * 0.5  // 50% du prix
            else -> activityPrice
        }
    }
}
