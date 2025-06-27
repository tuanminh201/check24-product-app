package com.example.c24productapp.ui.productoverview

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.*
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.*
import androidx.compose.ui.unit.*
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import com.example.c24productapp.model.Product
import com.example.c24productapp.navigation.Screen
import com.example.c24productapp.viewmodel.ProductListViewModel
import java.text.SimpleDateFormat
import java.util.*

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
    val uriHandler = LocalUriHandler.current

    LaunchedEffect(Unit) {
        viewModel.loadProducts()
    }

    Column(modifier = Modifier.fillMaxSize()) {

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF1976D2))
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Check24 ProductApp",
                style = MaterialTheme.typography.titleLarge,
                color = Color.White
            )
        }

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
                    onClick = { if (!hasError) viewModel.setFilter(filter) }
                )
            }
        }

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
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(productList) { product ->
                        ProductItem(
                            product = product,
                            isFavorited = favoriteProducts.contains(product),
                            onClick = {
                                viewModel.selectProduct(product)
                                navController.navigate(Screen.ProductDetails.route)
                            }
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                    }
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "© 2016 Check24",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.Gray,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.clickable {
                                    uriHandler.openUri("https://m.check24.de/rechtliche-hinweise/?deviceoutput=app")
                                }
                            )
                        }
                    }
                }
            }
        } else {
            // Error UI
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Warning,
                    contentDescription = "Error",
                    tint = Color.Red,
                    modifier = Modifier.size(64.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Probleme",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Die Daten konnten nicht geladen werden.",
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = { viewModel.loadProducts() }) {
                    Text("Neuladen")
                }
            }
        }
    }
}

@Composable
fun FilterTab(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val bgColor = if (isSelected) Color(0xFF1976D2) else Color.Transparent
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

@Composable
fun ProductItem(
    product: Product,
    isFavorited: Boolean,
    onClick: () -> Unit
) {
    val date = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
        .format(Date(product.releaseDate * 1000))

    val imageLink = "https://upload.wikimedia.org/wikipedia/commons/7/70/Example.png"
    val painter = rememberAsyncImagePainter(imageLink)

    val backgroundColor = if (isFavorited) Color(0xFFD1C4E9) else Color.White

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp)
            .clickable { onClick() }
            .border(1.dp, Color.LightGray, MaterialTheme.shapes.small)
            .clip(MaterialTheme.shapes.medium)
            .background(backgroundColor)
            .padding(8.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (product.available) {
                Image(
                    painter = painter,
                    contentDescription = "Product Image",
                    modifier = Modifier
                        .size(80.dp)
                        .clip(MaterialTheme.shapes.small)
                        .padding(4.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
            }

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(product.name, fontWeight = FontWeight.Bold)
                    Text(date, style = MaterialTheme.typography.labelSmall)
                }

                Text(buildAnnotatedString {
                    withStyle(
                        style = SpanStyle(fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    ) { append("Preis: ") }
                    withStyle(
                        style = SpanStyle(fontSize = 13.sp)
                    ) { append("%.2f %s".format(product.price.value, product.price.currency)) }
                })

                RatingStars(product.rating)

                Text(
                    text = product.description,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            }

            if (!product.available) {
                Spacer(modifier = Modifier.width(8.dp))
                Image(
                    painter = painter,
                    contentDescription = "Product Image",
                    modifier = Modifier
                        .size(80.dp)
                        .clip(MaterialTheme.shapes.small)
                        .padding(4.dp)
                )
            }
        }
    }
}

@Composable
fun RatingStars(rating: Double) {
    val fullStars = rating.toInt()
    val hasHalfStar = (rating - fullStars) >= 0.5

    Row {
        for (i in 1..5) {
            when {
                i <= fullStars -> Text("★", color = Color(0xFFFFC107))
                i == fullStars + 1 && hasHalfStar -> Text("☆", color = Color(0xFFFFC107))
                else -> Text("☆", color = Color.Gray)
            }
        }
    }
}
