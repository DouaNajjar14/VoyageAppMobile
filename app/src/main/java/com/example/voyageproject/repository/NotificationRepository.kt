package com.example.voyageproject.repository

import com.example.voyageproject.model.NotificationData
import com.example.voyageproject.network.RetrofitClient
import retrofit2.Response

class NotificationRepository {
    private val api = RetrofitClient.api

    suspend fun getNotifications(email: String): Response<List<NotificationData>> {
        return api.getNotifications(email)
    }

    suspend fun markAsRead(notificationId: String): Response<Void> {
        return api.markNotificationAsRead(notificationId)
    }
}
