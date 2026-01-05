package com.example.voyageproject.utils

import com.example.voyageproject.model.Reservation
import com.google.gson.*
import java.lang.reflect.Type

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
            details = null
        )
    }
}
