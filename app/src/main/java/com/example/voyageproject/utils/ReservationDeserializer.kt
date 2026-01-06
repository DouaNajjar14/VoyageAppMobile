package com.example.voyageproject.utils

import com.example.voyageproject.model.Reservation
import com.google.gson.*
import java.lang.reflect.Type
import java.time.LocalDate

class ReservationDeserializer : JsonDeserializer<Reservation> {
    override fun deserialize(
        json: JsonElement,
        typeOfT: Type,
        context: JsonDeserializationContext
    ): Reservation {
        val obj = json.asJsonObject
        
        // Convertir bookingDate depuis array vers String
        val bookingDateElement = obj.get("bookingDate")
        val bookingDate = when {
            bookingDateElement.isJsonArray -> {
                val arr = bookingDateElement.asJsonArray
                // Format: [year, month, day, hour, minute, second, nano]
                if (arr.size() >= 6) {
                    val year = arr[0].asInt
                    val month = arr[1].asInt
                    val day = arr[2].asInt
                    val hour = arr[3].asInt
                    val minute = arr[4].asInt
                    val second = arr[5].asInt
                    String.format("%04d-%02d-%02dT%02d:%02d:%02d", year, month, day, hour, minute, second)
                } else {
                    "N/A"
                }
            }
            bookingDateElement.isJsonPrimitive -> bookingDateElement.asString
            else -> "N/A"
        }
        
        // Convertir startDate et endDate depuis String vers LocalDate
        val startDate = obj.get("startDate")?.asString?.let { 
            try { LocalDate.parse(it) } catch (e: Exception) { null }
        }
        
        val endDate = obj.get("endDate")?.asString?.let { 
            try { LocalDate.parse(it) } catch (e: Exception) { null }
        }
        
        return Reservation(
            id = obj.get("id").asString,
            clientEmail = obj.get("clientEmail").asString,
            offerType = obj.get("offerType").asString,
            offerId = obj.get("offerId").asString,
            offerName = obj.get("offerName").asString,
            price = obj.get("price").asDouble,
            bookingDate = bookingDate,
            status = obj.get("status").asString,
            paymentMethod = obj.get("paymentMethod")?.asString,
            details = null,
            formula = obj.get("formula")?.asString,
            startDate = startDate,
            endDate = endDate,
            adultsCount = obj.get("adultsCount")?.asInt,
            childrenCount = obj.get("childrenCount")?.asInt,
            childrenAges = obj.get("childrenAges")?.asString,
            hotelLevel = obj.get("hotelLevel")?.asString,
            flightClass = obj.get("flightClass")?.asString,
            selectedActivities = obj.get("selectedActivities")?.asString,
            priceBreakdown = obj.get("priceBreakdown")?.asString
        )
    }
}
