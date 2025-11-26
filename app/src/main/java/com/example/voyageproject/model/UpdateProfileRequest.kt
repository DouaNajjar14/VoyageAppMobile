package com.example.voyageproject.model

data class UpdateProfileRequest(
    val firstName: String,
    val lastName: String,
    val email: String,
    val telephone: String?
)
