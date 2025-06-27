package com.example.c24productapp.model

data class ProductResponse(
    val header: Header,
    val filters: List<String>,
    val products: List<Product>
)

data class Header(
    val headerTitle: String,
    val headerDescription: String
)
