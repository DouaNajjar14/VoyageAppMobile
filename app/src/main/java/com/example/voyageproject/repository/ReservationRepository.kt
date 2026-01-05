package com.example.voyageproject.repository

import com.example.voyageproject.model.Reservation
import com.example.voyageproject.network.RetrofitClient
import retrofit2.Call
import retrofit2.Response

class ReservationRepository {
    private val api = RetrofitClient.api

    suspend fun bookOffer(body: Map<String, String>): Response<Void> {
        return api.bookOffer(body)
    }

    fun getHistory(email: String): Call<List<Reservation>> {
        return api.getReservations(email)
    }
}
