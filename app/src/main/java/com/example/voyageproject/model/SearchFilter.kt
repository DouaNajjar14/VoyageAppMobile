package com.example.voyageproject.model

data class SearchFilter(
    var destination: String? = null,
    var minPrice: Double? = null,
    var maxPrice: Double? = null,
    var startDate: String? = null,
    var endDate: String? = null,
    var sortBy: String = "price", // "price", "rating", "popularity"
    var sortOrder: String = "asc" // "asc", "desc"
)
