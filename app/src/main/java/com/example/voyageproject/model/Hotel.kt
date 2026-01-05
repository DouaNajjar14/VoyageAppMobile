package com.example.voyageproject.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Hotel(
    val id: String,
    val name: String,
    val address: String,
    val city: String,
    val country: String,
    val etoile: Int,
    val pricePerNight: Double? = null,
    val imageUrl: String? = null,
    val description: String? = null,
    val amenities: List<String>? = null,
    val rating: Double = etoile.toDouble(),
    val rooms: List<Room>? = null,
    val phone: String? = null,
    val email: String? = null,
    val website: String? = null,
    val checkInTime: String? = null,
    val checkOutTime: String? = null,
    val latitude: Double? = null,
    val longitude: Double? = null,
    val isActive: Boolean? = true
) : Parcelable
