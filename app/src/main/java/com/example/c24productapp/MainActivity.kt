package com.example.c24productapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.c24productapp.ui.productoverview.ProductOverviewScreen
import com.example.c24productapp.ui.theme.C24ProductAppTheme
import com.example.c24productapp.viewmodel.ProductListViewModel

class MainActivity : ComponentActivity() {

    private val viewModel = ProductListViewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.loadProducts() // <-- gọi API khi mở app

        setContent {
            C24ProductAppTheme {
                ProductOverviewScreen(viewModel)
            }
        }
    }
}
