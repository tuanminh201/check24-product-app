package com.example.c24productapp.model

data class Product(
    val id: Int,
    val name: String,
    val type: String,
    val color: String,
    val imageURL: String,
    val colorCode: String,
    val available: Boolean,
    val releaseDate: Long,
    val description: String,
    val longDescription: String,
    val rating: Double,
    val price: Price,
    var isFavorite: Boolean = false
)

data class Price(
    val value: Double,
    val currency: String
)
