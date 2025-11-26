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


    @GET("/api/client/profile")
    suspend fun getProfile(
        @Query("email") email: String
    ): Response<Client>

    @PUT("/api/client/profile")
    suspend fun updateProfile(
        @Query("email") email: String,
        @Body request: UpdateProfileRequest
    ): Response<Client>
    @PUT("/api/client/change-password")
        suspend fun changePassword(@Query("email") email: String, @Body body: Map<String, String>): Response<Unit>




        @POST("api/reservation")
        suspend fun bookOffer(@Body body: Map<String, String>): Response<Void>

    @GET("api/hotels")
    suspend fun getHotels(): Response<List<Hotel>>

    @GET("api/circuits")
    suspend fun getCircuits(): Response<List<Circuit>>

    @GET("api/flights")
    suspend fun getFlights(): Response<List<Flight>>


}

