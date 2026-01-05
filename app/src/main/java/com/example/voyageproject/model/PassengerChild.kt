package com.example.voyageproject.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class PassengerChild(
    val age: Int
) : Parcelable {
    fun isFree(): Boolean = age in 0..4
    
    fun getPriceMultiplier(): Double {
        return when {
            age in 0..4 -> 0.0  // Gratuit
            age in 5..18 -> 0.5 // 50% du prix
            else -> 1.0         // Prix plein (ne devrait pas arriver)
        }
    }
    
    fun getAgeCategory(): String {
        return when {
            age in 0..4 -> "Bébé (0-4 ans) - Gratuit"
            age in 5..18 -> "Enfant (5-18 ans) - 50% du tarif"
            else -> "Adulte"
        }
    }
}
