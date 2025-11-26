package com.example.voyageproject.model

data class Client(
    val id: String? = null,
    val firstName: String,
    val lastName: String,
    val email: String,
    val password: String,
    val telephone: String,
    val enabled: Boolean = false
)