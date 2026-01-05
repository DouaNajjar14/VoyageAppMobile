package com.example.voyageproject.model

import com.google.gson.*
import java.lang.reflect.Type

class ReservationDetailsDeserializer : JsonDeserializer<ReservationDetails> {
    override fun deserialize(
        json: JsonElement,
        typeOfT: Type,
        context: JsonDeserializationContext
    ): ReservationDetails {
        val jsonObject = json.asJsonObject
        
        return ReservationDetails(
            id = jsonObject.get("id").asString,
            clientEmail = jsonObject.get("clientEmail").asString,
            offerType = jsonObject.get("offerType").asString,
            offerName = jsonObject.get("offerName").asString,
            price = jsonObject.get("price").asDouble,
            bookingDate = jsonObject.get("bookingDate").asString,
            status = getStringOrFirst(jsonObject.get("status")),
            paymentMethod = getStringOrFirst(jsonObject.get("paymentMethod")),
            startDate = getStringOrNull(jsonObject.get("startDate")),
            endDate = getStringOrNull(jsonObject.get("endDate")),
            adultsCount = getIntOrNull(jsonObject.get("adultsCount")),
            childrenCount = getIntOrNull(jsonObject.get("childrenCount")),
            formula = getStringOrFirst(jsonObject.get("formula")),
            hotel = if (jsonObject.has("hotel") && !jsonObject.get("hotel").isJsonNull) {
                deserializeHotel(jsonObject.getAsJsonObject("hotel"))
            } else null,
            room = if (jsonObject.has("room") && !jsonObject.get("room").isJsonNull) {
                deserializeRoom(jsonObject.getAsJsonObject("room"))
            } else null,
            payment = if (jsonObject.has("payment") && !jsonObject.get("payment").isJsonNull) {
                deserializePayment(jsonObject.getAsJsonObject("payment"))
            } else null
        )
    }
    
    private fun getStringOrNull(element: JsonElement?): String? {
        if (element == null || element.isJsonNull) return null
        return try {
            if (element.isJsonArray) {
                val array = element.asJsonArray
                if (array.size() > 0) array[0].asString else null
            } else {
                element.asString
            }
        } catch (e: Exception) {
            null
        }
    }
    
    private fun getStringOrFirst(element: JsonElement?): Any? {
        if (element == null || element.isJsonNull) return null
        return try {
            if (element.isJsonArray) {
                val array = element.asJsonArray
                if (array.size() > 0) array[0].asString else null
            } else if (element.isJsonPrimitive) {
                element.asString
            } else {
                element.toString()
            }
        } catch (e: Exception) {
            null
        }
    }
    
    private fun getIntOrNull(element: JsonElement?): Int? {
        if (element == null || element.isJsonNull) return null
        return try {
            element.asInt
        } catch (e: Exception) {
            null
        }
    }
    
    private fun getDoubleOrNull(element: JsonElement?): Double? {
        if (element == null || element.isJsonNull) return null
        return try {
            element.asDouble
        } catch (e: Exception) {
            null
        }
    }
    
    private fun deserializeHotel(json: JsonObject): HotelDetails {
        return HotelDetails(
            name = json.get("name").asString,
            address = json.get("address").asString,
            city = json.get("city").asString,
            country = json.get("country").asString,
            stars = json.get("stars").asInt,
            phone = getStringOrNull(json.get("phone")),
            email = getStringOrNull(json.get("email")),
            checkInTime = getStringOrNull(json.get("checkInTime")),
            checkOutTime = getStringOrNull(json.get("checkOutTime")),
            imageUrl = getStringOrFirst(json.get("imageUrl"))
        )
    }
    
    private fun deserializeRoom(json: JsonObject): RoomDetails {
        return RoomDetails(
            roomNumber = json.get("roomNumber").asString,
            roomType = getStringOrFirst(json.get("roomType")),
            maxOccupancy = getIntOrNull(json.get("maxOccupancy")),
            viewType = getStringOrFirst(json.get("viewType")),
            bedType = getStringOrFirst(json.get("bedType")),
            sizeSqm = getDoubleOrNull(json.get("sizeSqm")),
            description = getStringOrNull(json.get("description")),
            pricePerNight = getDoubleOrNull(json.get("pricePerNight")),
            viewSupplement = getDoubleOrNull(json.get("viewSupplement"))
        )
    }
    
    private fun deserializePayment(json: JsonObject): PaymentDetails {
        return PaymentDetails(
            id = json.get("id").asString,
            amount = json.get("amount").asDouble,
            paymentMethod = getStringOrFirst(json.get("paymentMethod")) ?: "CARD",
            status = getStringOrFirst(json.get("status")) ?: "COMPLETED",
            transactionId = getStringOrNull(json.get("transactionId")),
            paymentDate = getStringOrNull(json.get("paymentDate"))
        )
    }
}
