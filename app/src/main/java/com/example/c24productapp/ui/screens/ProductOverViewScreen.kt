package com.example.c24productapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.c24productapp.model.Product
import com.example.c24productapp.navigation.Screen
import com.example.c24productapp.ui.components.*
import com.example.c24productapp.viewmodel.ProductListViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductOverviewScreen(
    viewModel: ProductListViewModel,
    navController: NavHostController
) {
    val filters by viewModel.filters.collectAsState()
    val selectedFilter by viewModel.selectedFilter.collectAsState()
    val productList by viewModel.filteredProducts.collectAsState()
    val favoriteProducts by viewModel.favoriteProducts.collectAsState()
    val isRefreshing by viewModel.isRefreshing.collectAsState()
    val hasError by viewModel.hasError.collectAsState()
    val title by viewModel.headerTitle.collectAsState()
    val subtitle by viewModel.headerSubtitle.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadProducts()
    }

    Column(modifier = Modifier.fillMaxSize()) {
        AppHeader()

        FilterRow(
            filters = filters,
            selectedFilter = selectedFilter,
            hasError = hasError,
            onSelect = { viewModel.setFilter(it) }
        )

        Column(modifier = Modifier.padding(12.dp)) {
            Text(text = title, style = MaterialTheme.typography.headlineSmall)
            Text(text = subtitle, style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
        }

        if (!hasError) {
            PullToRefreshBox(
                isRefreshing = isRefreshing,
                onRefresh = { viewModel.refreshProducts() },
                modifier = Modifier.fillMaxSize()
            ) {
                ProductListContent(
                    products = productList,
                    favoriteProducts = favoriteProducts,
                    onProductClick = {
                        viewModel.selectProduct(it)
                        navController.navigate(Screen.ProductDetails.route)
                    },
                    navController = navController
                )
            }
        } else {
            ErrorBox(onRetry = { viewModel.loadProducts() })
        }
    }
}

@Composable
fun FilterRow(
    filters: List<String>,
    selectedFilter: String,
    hasError: Boolean,
    onSelect: (String) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFE0E0E0))
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        filters.forEach { filter ->
            val isSelected = selectedFilter.equals(filter, ignoreCase = true)
            FilterTab(
                text = filter,
                isSelected = isSelected,
                onClick = { if (!hasError) onSelect(filter) }
            )
        }
    }
}

@Composable
fun ProductListContent(
    products: List<Product>,
    favoriteProducts: Set<Product>,
    onProductClick: (Product) -> Unit,
    navController: NavHostController
) {
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(products, key = { it.id }) { product ->
            ProductItem(
                product = product,
                isFavorited = favoriteProducts.contains(product),
                onClick = { onProductClick(product) }
            )
            Spacer(modifier = Modifier.height(12.dp))
        }
        item {
            AppFooter(navController = navController)
        }
    }
}

@Composable
fun FilterTab(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val bgColor = if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent
    val textColor = if (isSelected) Color.White else Color.Black

    Box(
        modifier = Modifier
            .clip(MaterialTheme.shapes.small)
            .background(bgColor)
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Text(text = text, color = textColor)
    }
}
