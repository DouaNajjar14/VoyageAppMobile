package com.example.voyageproject.model

data class NotificationData(
    val id: String,
    val title: String,
    val message: String,
    val type: String, // "reminder", "offer", "checkin", "recommendation"
    val timestamp: String,
    val isRead: Boolean = false
)
