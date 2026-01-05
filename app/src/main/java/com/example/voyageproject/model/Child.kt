package com.example.voyageproject.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Child(
    val age: Int
) : Parcelable {
    fun isFree(): Boolean = age < 12
    fun getDiscountFactor(): Double = if (age < 12) 0.0 else 0.5
}
