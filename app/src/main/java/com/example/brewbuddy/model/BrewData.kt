package com.example.brewbuddy.model

data class BrewData(
    var key: String? = null,
    val name: String,
    val notes: String,
    val rating: Double
)
