package com.example.voyageproject.utils

import com.example.voyageproject.model.Flight
import com.google.gson.*
import java.lang.reflect.Type
import android.util.Log

class FlightDeserializer : JsonDeserializer<Flight> {
    override fun deserialize(
        json: JsonElement,
        typeOfT: Type,
        context: JsonDeserializationContext
    ): Flight {
        Log.d("FLIGHT_DESER", "=== PARSING FLIGHT ===")
        
        return try {
            val obj = json.asJsonObject
            
            val id = safeString(obj, "id", "")
            val airline = safeString(obj, "airline", "Airline")
            val flightNumber = safeString(obj, "flightNumber", "")
            val origin = safeString(obj, "origin", "")
            val destination = safeString(obj, "destination", "")
            val departureTime = safeString(obj, "departureTime", "")
            val arrivalTime = safeString(obj, "arrivalTime", "")
            val price = safeDouble(obj, "price", 0.0)
            val seatsAvailable = safeInt(obj, "seatsAvailable", 0)
            val classType = safeString(obj, "class_type", null) ?: safeString(obj, "classType", null)
            val duration = safeString(obj, "duration", null)
            
            Log.d("FLIGHT_DESER", "✅ Flight: $airline $flightNumber")
            
            Flight(
                id = id ?: "",
                airline = airline ?: "Airline",
                flightNumber = flightNumber ?: "",
                origin = origin ?: "",
                destination = destination ?: "",
                departureTime = departureTime ?: "",
                arrivalTime = arrivalTime ?: "",
                price = price,
                seatsAvailable = seatsAvailable,
                class_type = classType,
                duration = duration
            )
        } catch (e: Exception) {
            Log.e("FLIGHT_DESER", "❌ Erreur: ${e.message}", e)
            Flight(
                id = "",
                airline = "Error",
                flightNumber = "",
                origin = "",
                destination = "",
                departureTime = "",
                arrivalTime = "",
                price = 0.0,
                seatsAvailable = 0,
                class_type = null,
                duration = null
            )
        }
    }
    
    private fun safeString(obj: JsonObject, key: String, default: String?): String? {
        return try {
            if (obj.has(key) && !obj.get(key).isJsonNull) {
                obj.get(key).asString
            } else {
                default
            }
        } catch (e: Exception) {
            Log.w("FLIGHT_DESER", "Erreur $key: ${e.message}")
            default
        }
    }
    
    private fun safeInt(obj: JsonObject, key: String, default: Int): Int {
        return try {
            if (obj.has(key) && !obj.get(key).isJsonNull) {
                obj.get(key).asInt
            } else {
                default
            }
        } catch (e: Exception) {
            Log.w("FLIGHT_DESER", "Erreur $key: ${e.message}")
            default
        }
    }
    
    private fun safeDouble(obj: JsonObject, key: String, default: Double): Double {
        return try {
            if (obj.has(key) && !obj.get(key).isJsonNull) {
                obj.get(key).asDouble
            } else {
                default
            }
        } catch (e: Exception) {
            Log.w("FLIGHT_DESER", "Erreur $key: ${e.message}")
            default
        }
    }
}
