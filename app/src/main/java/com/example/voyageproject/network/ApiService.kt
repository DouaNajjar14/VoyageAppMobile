package com.example.voyageproject.network

import com.example.voyageproject.model.*
import retrofit2.Response
import retrofit2.http.*

interface ApiService {
    @POST("api/client/register")
    suspend fun register(@Body request: RegisterRequest): Response<Client>

    @GET("api/client/confirm")
    suspend fun confirm(@Query("token") token: String): Response<String>

    @POST("api/client/forgot-password")
    suspend fun forgotPassword(@Body request: Map<String, String>): Response<Map<String, String>>

    @POST("api/client/reset-password")
    suspend fun resetPassword(@Body request: Map<String, String>): Response<Map<String, String>>

    @POST("api/client/login")
    suspend fun login(@Body request: Map<String, String>): Response<Client>

    @GET("api/client/profile")
    suspend fun getProfile(
        @Query("email") email: String
    ): Response<Client>

    @PUT("api/client/profile")
    suspend fun updateProfile(
        @Query("email") email: String,
        @Body request: UpdateProfileRequest
    ): Response<Client>
    
    @PUT("api/client/change-password")
    suspend fun changePassword(@Query("email") email: String, @Body body: Map<String, String>): Response<Unit>

    @POST("api/reservation")
    suspend fun bookOffer(@Body body: Map<String, String>): Response<Void>

    @GET("api/hotels")
    suspend fun getHotels(): Response<List<Hotel>>

    @GET("api/circuits")
    suspend fun getCircuits(): Response<List<Circuit>>

    @GET("api/flights")
    suspend fun getFlights(): Response<List<Flight>>

    // Recherche avec filtres
    @GET("api/hotels/search")
    suspend fun searchHotels(
        @Query("destination") destination: String?,
        @Query("minPrice") minPrice: Double?,
        @Query("maxPrice") maxPrice: Double?,
        @Query("sortBy") sortBy: String?,
        @Query("sortOrder") sortOrder: String?
    ): Response<List<Hotel>>

    @GET("api/flights/search")
    suspend fun searchFlights(
        @Query("origin") origin: String?,
        @Query("destination") destination: String?,
        @Query("minPrice") minPrice: Double?,
        @Query("maxPrice") maxPrice: Double?,
        @Query("sortBy") sortBy: String?,
        @Query("departureDate") departureDate: String?,
        @Query("totalPassengers") totalPassengers: Int?
    ): List<Flight>

    @GET("api/circuits/search")
    suspend fun searchCircuits(
        @Query("destination") destination: String?,
        @Query("duration") duration: Int?,
        @Query("minPrice") minPrice: Double?,
        @Query("maxPrice") maxPrice: Double?,
        @Query("sortBy") sortBy: String?
    ): Response<List<Circuit>>
    
    // Endpoints circuits détaillés
    @GET("api/circuits/{id}/program")
    suspend fun getCircuitProgram(@Path("id") id: String): Response<List<CircuitDay>>
    
    @GET("api/circuits/{id}/activities")
    suspend fun getCircuitActivities(@Path("id") id: String): Response<List<CircuitActivity>>
    
    @POST("api/circuits/{id}/calculate-price")
    suspend fun calculateCircuitPrice(
        @Path("id") id: String,
        @Body request: Map<String, Any>
    ): Response<Map<String, Any>>

    // Historique des réservations
    @GET("api/reservation")
    fun getReservations(@Query("email") email: String): retrofit2.Call<List<Reservation>>
    
    // Détails complets d'une réservation
    @GET("api/reservation/{id}/details")
    suspend fun getReservationDetails(@Path("id") reservationId: String): Response<ReservationDetails>
    
    // Annulation de réservation
    @DELETE("api/reservation/{id}")
    suspend fun cancelReservation(@Path("id") reservationId: String): Response<CancellationResponse>

    // Notifications
    @GET("api/notifications")
    suspend fun getNotifications(@Query("email") email: String): Response<List<NotificationData>>

    @PUT("api/notifications/{id}/read")
    suspend fun markNotificationAsRead(@Path("id") notificationId: String): Response<Void>
}

