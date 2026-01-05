package com.example.voyageproject.utils

import com.example.voyageproject.model.Circuit
import com.google.gson.*
import java.lang.reflect.Type
import android.util.Log

class CircuitDeserializer : JsonDeserializer<Circuit> {
    override fun deserialize(
        json: JsonElement,
        typeOfT: Type,
        context: JsonDeserializationContext
    ): Circuit {
        Log.d("CIRCUIT_DESER", "========================================")
        Log.d("CIRCUIT_DESER", "=== DÉBUT PARSING CIRCUIT ===")
        
        return try {
            // Log du JSON brut
            val jsonString = json.toString()
            Log.d("CIRCUIT_DESER", "JSON brut (premiers 500 chars): ${jsonString.take(500)}")
            
            val obj = json.asJsonObject
            Log.d("CIRCUIT_DESER", "✅ JSON est un objet")
            
            // ID
            val id = try {
                val value = if (obj.has("id") && !obj.get("id").isJsonNull) {
                    obj.get("id").asString
                } else {
                    ""
                }
                Log.d("CIRCUIT_DESER", "✅ id: $value")
                value
            } catch (e: Exception) {
                Log.e("CIRCUIT_DESER", "❌ Erreur id: ${e.message}")
                ""
            }
            
            // Title
            val title = try {
                val value = if (obj.has("title") && !obj.get("title").isJsonNull) {
                    obj.get("title").asString
                } else {
                    "Circuit sans titre"
                }
                Log.d("CIRCUIT_DESER", "✅ title: $value")
                value
            } catch (e: Exception) {
                Log.e("CIRCUIT_DESER", "❌ Erreur title: ${e.message}")
                "Circuit sans titre"
            }
            
            // Description
            val description = try {
                val value = if (obj.has("description") && !obj.get("description").isJsonNull) {
                    obj.get("description").asString
                } else {
                    ""
                }
                Log.d("CIRCUIT_DESER", "✅ description: ${value.take(50)}...")
                value
            } catch (e: Exception) {
                Log.e("CIRCUIT_DESER", "❌ Erreur description: ${e.message}")
                ""
            }
            
            // Duree
            val duree = try {
                val value = if (obj.has("duree") && !obj.get("duree").isJsonNull) {
                    obj.get("duree").asInt
                } else {
                    0
                }
                Log.d("CIRCUIT_DESER", "✅ duree: $value")
                value
            } catch (e: Exception) {
                Log.e("CIRCUIT_DESER", "❌ Erreur duree: ${e.message}")
                0
            }
            
            // Prix
            val prix = try {
                val value = if (obj.has("prix") && !obj.get("prix").isJsonNull) {
                    obj.get("prix").asDouble
                } else {
                    0.0
                }
                Log.d("CIRCUIT_DESER", "✅ prix: $value")
                value
            } catch (e: Exception) {
                Log.e("CIRCUIT_DESER", "❌ Erreur prix: ${e.message}")
                0.0
            }
            
            // ImageUrl
            val imageUrl = try {
                val value = if (obj.has("imageUrl") && !obj.get("imageUrl").isJsonNull) {
                    obj.get("imageUrl").asString
                } else {
                    null
                }
                Log.d("CIRCUIT_DESER", "✅ imageUrl: $value")
                value
            } catch (e: Exception) {
                Log.e("CIRCUIT_DESER", "❌ Erreur imageUrl: ${e.message}")
                null
            }
            
            // Destinations
            val destinations = try {
                if (obj.has("destinations") && obj.get("destinations").isJsonArray) {
                    val array = obj.getAsJsonArray("destinations")
                    val list = mutableListOf<String>()
                    for (i in 0 until array.size()) {
                        try {
                            list.add(array.get(i).asString)
                        } catch (e: Exception) {
                            Log.w("CIRCUIT_DESER", "Erreur destination[$i]: ${e.message}")
                        }
                    }
                    Log.d("CIRCUIT_DESER", "✅ destinations: ${list.size} éléments")
                    if (list.isNotEmpty()) list else null
                } else {
                    Log.d("CIRCUIT_DESER", "⚠️ destinations: absent ou null")
                    null
                }
            } catch (e: Exception) {
                Log.e("CIRCUIT_DESER", "❌ Erreur destinations: ${e.message}")
                null
            }
            
            // Includes
            val includes = try {
                if (obj.has("includes") && obj.get("includes").isJsonArray) {
                    val array = obj.getAsJsonArray("includes")
                    val list = mutableListOf<String>()
                    for (i in 0 until array.size()) {
                        try {
                            list.add(array.get(i).asString)
                        } catch (e: Exception) {
                            Log.w("CIRCUIT_DESER", "Erreur include[$i]: ${e.message}")
                        }
                    }
                    Log.d("CIRCUIT_DESER", "✅ includes: ${list.size} éléments")
                    if (list.isNotEmpty()) list else null
                } else {
                    Log.d("CIRCUIT_DESER", "⚠️ includes: absent ou null")
                    null
                }
            } catch (e: Exception) {
                Log.e("CIRCUIT_DESER", "❌ Erreur includes: ${e.message}")
                null
            }
            
            // Hotels
            val hotelNames = try {
                if (obj.has("hotels") && obj.get("hotels").isJsonArray) {
                    val array = obj.getAsJsonArray("hotels")
                    val names = mutableListOf<String>()
                    Log.d("CIRCUIT_DESER", "📋 Parsing ${array.size()} hôtels...")
                    for (i in 0 until array.size()) {
                        try {
                            val hotel = array.get(i)
                            if (hotel.isJsonObject) {
                                val hotelObj = hotel.asJsonObject
                                if (hotelObj.has("name") && !hotelObj.get("name").isJsonNull) {
                                    val name = hotelObj.get("name").asString
                                    names.add(name)
                                    Log.d("CIRCUIT_DESER", "  ✅ Hotel[$i]: $name")
                                }
                            }
                        } catch (e: Exception) {
                            Log.w("CIRCUIT_DESER", "  ⚠️ Erreur hotel[$i]: ${e.message}")
                        }
                    }
                    Log.d("CIRCUIT_DESER", "✅ hotels: ${names.size} noms extraits")
                    names
                } else {
                    Log.d("CIRCUIT_DESER", "⚠️ hotels: absent ou null")
                    emptyList()
                }
            } catch (e: Exception) {
                Log.e("CIRCUIT_DESER", "❌ Erreur hotels: ${e.message}")
                emptyList()
            }
            
            val circuit = Circuit(
                id = id,
                title = title,
                description = description,
                duree = duree,
                prix = prix,
                imageUrl = imageUrl,
                destinations = destinations,
                includes = includes,
                hotelNames = hotelNames
            )
            
            Log.d("CIRCUIT_DESER", "========================================")
            Log.d("CIRCUIT_DESER", "✅✅✅ CIRCUIT PARSÉ: $title")
            Log.d("CIRCUIT_DESER", "========================================")
            
            circuit
            
        } catch (e: Exception) {
            Log.e("CIRCUIT_DESER", "========================================")
            Log.e("CIRCUIT_DESER", "❌❌❌ ERREUR TOTALE")
            Log.e("CIRCUIT_DESER", "Type: ${e.javaClass.simpleName}")
            Log.e("CIRCUIT_DESER", "Message: ${e.message}")
            Log.e("CIRCUIT_DESER", "========================================", e)
            
            // Retourner un circuit minimal
            Circuit(
                id = "",
                title = "Erreur de chargement",
                description = "Erreur: ${e.message}",
                duree = 0,
                prix = 0.0,
                imageUrl = null,
                destinations = null,
                includes = null,
                hotelNames = emptyList()
            )
        }
    }
}
