package com.example.c24productapp.model

data class ProductResponse(
    val header: Header,
    val filters: List<String>,
    val products: List<Product>
)
