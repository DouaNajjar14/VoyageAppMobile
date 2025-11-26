package com.example.voyageproject.model

data class RegisterRequest(
    val email: String,
    val password: String,
    val firstName: String,
    val telephone :String,
    val lastName: String
)