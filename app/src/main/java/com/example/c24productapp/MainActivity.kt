package com.example.c24productapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import com.example.c24productapp.navigation.AppNavigation
import com.example.c24productapp.ui.theme.C24ProductAppTheme
import com.example.c24productapp.viewmodel.ProductListViewModel

class MainActivity : ComponentActivity() {
    private val viewModel: ProductListViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            C24ProductAppTheme {
                Surface(color = MaterialTheme.colorScheme.background) {
                    AppNavigation(viewModel)
                }
            }
        }
    }
}
