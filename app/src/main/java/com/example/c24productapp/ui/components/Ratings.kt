package com.example.c24productapp.ui.components

import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

@Composable
fun RatingStars(rating: Double) {
    val fullStars = rating.toInt()
    val hasHalfStar = (rating - fullStars) >= 0.5

    Row {
        for (i in 1..5) {
            when {
                i <= fullStars -> Text("★", color = Color(0xFFFFC107)) // filled
                i == fullStars + 1 && hasHalfStar -> Text("☆", color = Color(0xFFFFC107)) // half
                else -> Text("☆", color = Color.Gray)
            }
        }
    }
}