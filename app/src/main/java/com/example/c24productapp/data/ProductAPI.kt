package com.example.c24productapp.data

import com.example.c24productapp.model.ProductResponse
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

interface ProductAPI {
    @GET("products-test.json")
    suspend fun getProducts(): ProductResponse
}

object ProductService {
    private val retrofit = Retrofit.Builder()
        .baseUrl("https://app.check24.de/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val api: ProductAPI = retrofit.create(ProductAPI::class.java)
}
