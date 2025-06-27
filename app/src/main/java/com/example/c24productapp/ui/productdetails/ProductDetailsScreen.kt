package com.example.c24productapp.ui.productdetails

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.*
import androidx.compose.ui.unit.*
import coil.compose.rememberAsyncImagePainter
import com.example.c24productapp.viewmodel.ProductListViewModel
import com.example.c24productapp.ui.productoverview.RatingStars
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun ProductDetailsScreen(viewModel: ProductListViewModel) {
    val product by viewModel.selectedProduct.collectAsState()
    val favoriteProducts by viewModel.favoriteProducts.collectAsState()

    product?.let {
        val isFavorited = favoriteProducts.contains(it)
        val imageLink = "https://upload.wikimedia.org/wikipedia/commons/7/70/Example.png"
        val painter = rememberAsyncImagePainter(imageLink)
        val date = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
            .format(Date(it.releaseDate * 1000))
        val uriHandler = LocalUriHandler.current

        Column(
            modifier = Modifier
                .fillMaxSize()
        ) {
            // AppBar
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

            Spacer(modifier = Modifier.height(16.dp))

            // Product Info Block
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

            // Favorite / Vergessen button
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

            // Beschreibung section
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
    } ?: Text("No product selected.")
}
