package com.example.c24productapp.ui.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.*
import androidx.compose.ui.unit.*
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import com.example.c24productapp.ui.components.AppFooter
import com.example.c24productapp.ui.components.AppHeader
import com.example.c24productapp.ui.components.RatingStars
import com.example.c24productapp.viewmodel.ProductListViewModel
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun ProductDetailsScreen(viewModel: ProductListViewModel,  navController: NavHostController) {
    val product by viewModel.selectedProduct.collectAsState()
    val favoriteProducts by viewModel.favoriteProducts.collectAsState()

    product?.let {
        val isFavorited = favoriteProducts.contains(it)
        val imageLink = "https://upload.wikimedia.org/wikipedia/commons/7/70/Example.png"
        val painter = rememberAsyncImagePainter(imageLink)
        val date = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
            .format(Date(it.releaseDate * 1000))

        Column(
            modifier = Modifier
                .fillMaxSize()
        ) {
            AppHeader()

            Spacer(modifier = Modifier.height(16.dp))

            // Product Info
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, Color.LightGray)
                    .padding(8.dp)
            ) {
                Image(
                    painter = painter,
                    contentDescription = "Product Image",
                    modifier = Modifier
                        .size(80.dp)
                        .clip(MaterialTheme.shapes.small)
                        .background(Color(0xFF1976D2))
                )

                Spacer(modifier = Modifier.width(8.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = it.name,
                        fontWeight = FontWeight.Bold,
                        color = if (isFavorited) Color(0xFF1976D2) else Color.Unspecified
                    )

                    Text(
                        "Preis: %.2f %s".format(it.price.value, it.price.currency),
                        fontSize = 13.sp,
                        color = Color.Gray
                    )

                    RatingStars(it.rating)

                    Text(
                        text = it.description,
                        style = MaterialTheme.typography.bodySmall,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Text(
                    text = date,
                    style = MaterialTheme.typography.labelSmall,
                    modifier = Modifier.align(Alignment.Top),
                    color = Color.Gray
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Vormerken / Vergessen button
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Button(
                    onClick = { viewModel.toggleFavorite(it) },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isFavorited) Color.Gray else Color(0xFF1976D2)
                    ),
                    contentPadding = PaddingValues(horizontal = 60.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = if (isFavorited) "Vergessen" else "Vormerken",
                        color = Color.White
                    )
                }

            }

            Spacer(modifier = Modifier.height(16.dp))

            // Description
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                Text(
                    text = "Beschreibung",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = it.longDescription,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray,
                    maxLines = 4,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            // Footer
            AppFooter(navController = navController)
        }
    }
}
