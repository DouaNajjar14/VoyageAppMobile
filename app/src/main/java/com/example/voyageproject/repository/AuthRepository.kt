package com.example.voyageproject.repository

import com.example.voyageproject.model.*
import com.example.voyageproject.network.RetrofitClient
import retrofit2.Response

class AuthRepository {
    private val api = RetrofitClient.api





    suspend fun changePassword(email: String, newPassword: String): Response<Unit> {
        val body = mapOf("newPassword" to newPassword)
        return api.changePassword(email, body)
    }

    suspend fun register(req: RegisterRequest): Response<Client> = api.register(req)
    suspend fun confirm(token: String): Response<String> = api.confirm(token)
    suspend fun forgot(email: String): Response<Map<String, String>> {
        val body = mapOf("email" to email)
        return api.forgotPassword(body)
    }


    suspend fun resetPassword(token: String, newPassword: String): Response<Map<String, String>> {
        val body = mapOf(
            "token" to token,
            "newPassword" to newPassword
        )
        return api.resetPassword(body)
    }



    suspend fun login(email: String, password: String): Response<Client> =
        api.login(mapOf("email" to email, "password" to password))
    suspend fun getProfile(email: String) = api.getProfile(email)

    suspend fun updateProfile(email: String, req: UpdateProfileRequest) =
        api.updateProfile(email, req)


}