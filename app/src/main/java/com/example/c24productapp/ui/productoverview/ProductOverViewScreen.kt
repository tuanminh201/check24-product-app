package com.example.c24productapp.ui.productoverview

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.example.c24productapp.model.Product
import com.example.c24productapp.viewmodel.ProductListViewModel
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun ProductOverviewScreen(viewModel: ProductListViewModel) {
    val filters by viewModel.filters.collectAsState()
    val selectedFilter by viewModel.selectedFilter.collectAsState()
    val productList by viewModel.filteredProducts.collectAsState()
    val title by viewModel.headerTitle.collectAsState()
    val subtitle by viewModel.headerSubtitle.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadProducts()
    }

    Column(modifier = Modifier.fillMaxSize()) {

        // AppBar Title
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF1976D2)) // Blue
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Check24 ProductApp",
                style = MaterialTheme.typography.titleLarge,
                color = Color.White,
            )
        }

        // Filter Tabs
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
                    onClick = { viewModel.setFilter(filter) }
                )
            }
        }

        // Header title/subtitle
        Column(modifier = Modifier.padding(12.dp)) {
            Text(text = title, style = MaterialTheme.typography.headlineSmall)
            Text(text = subtitle, style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
        }

        // Product List
        LazyColumn(
            modifier = Modifier.fillMaxSize(),) {
            items(productList) { product ->
                ProductItem(product)
                Spacer(modifier = Modifier.height(12.dp))
            }
            item {
                val uriHandler = LocalUriHandler.current

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
fun ProductItem(product: Product) {
    val date = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
        .format(Date(product.releaseDate * 1000))

    val imageLink = "https://upload.wikimedia.org/wikipedia/commons/7/70/Example.png"
    val painter = rememberAsyncImagePainter(imageLink)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, Color.LightGray)
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (product.available) {
            Image(
                painter = painter,
                contentDescription = "Product Image",
                modifier = Modifier
                    .size(64.dp)
                    .clip(MaterialTheme.shapes.small)
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

            Text(
                text = product.description,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.bodySmall
            )

            Text(
                buildAnnotatedString {
                    withStyle(
                        style = SpanStyle(
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    ) {
                        append("Preis: ")
                    }
                    withStyle(
                        style = SpanStyle(fontSize = 13.sp)
                    ) {
                        append("%.2f %s".format(product.price.value, product.price.currency))
                    }
                }
            )

            RatingStars(product.rating)
        }

        if (!product.available) {
            Spacer(modifier = Modifier.width(8.dp))
            Image(
                painter = painter,
                contentDescription = "Product Image",
                modifier = Modifier
                    .size(64.dp)
                    .clip(MaterialTheme.shapes.small)
            )
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