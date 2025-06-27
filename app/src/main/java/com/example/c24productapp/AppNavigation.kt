package com.example.c24productapp

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.c24productapp.navigation.Screen
import com.example.c24productapp.ui.productdetails.ProductDetailsScreen
import com.example.c24productapp.ui.productoverview.ProductOverviewScreen
import com.example.c24productapp.viewmodel.ProductListViewModel

@Composable
fun AppNavigation(viewModel: ProductListViewModel) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = Screen.ProductList.route) {
        composable(Screen.ProductList.route) {
            ProductOverviewScreen(viewModel, navController)
        }
        composable(Screen.ProductDetails.route) {
            ProductDetailsScreen(viewModel)
        }
    }
}
