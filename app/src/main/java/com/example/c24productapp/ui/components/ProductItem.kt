package com.example.c24productapp.ui.components

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.text.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.*
import coil.compose.rememberAsyncImagePainter
import com.example.c24productapp.model.Product
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun ProductItem(
    product: Product,
    isFavorited: Boolean,
    onClick: () -> Unit
) {
    val painter = rememberAsyncImagePainter(getImageUrl(product))
    val backgroundColor = getCardBackgroundColor(isFavorited)
    val releaseDate = formatDate(product.releaseDate)

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
                ProductImage(painter)
                Spacer(modifier = Modifier.width(8.dp))
                ProductInfoColumn(product, releaseDate, modifier = Modifier.weight(1f))
            } else {
                ProductInfoColumn(product, releaseDate, modifier = Modifier.weight(1f))
                Spacer(modifier = Modifier.width(8.dp))
                ProductImage(painter)
            }
        }
    }
}

@Composable
fun ProductImage(painter: Painter) {
    Image(
        painter = painter,
        contentDescription = "Product Image",
        modifier = Modifier
            .size(80.dp)
            .clip(MaterialTheme.shapes.small)
            .padding(4.dp)
    )
}

@Composable
fun ProductInfoColumn(
    product: Product,
    releaseDate: String,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(product.name, fontWeight = FontWeight.Bold)
            Text(releaseDate, style = MaterialTheme.typography.labelSmall)
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
}

fun formatDate(timestampSeconds: Long): String {
    val formatter = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
    return formatter.format(Date(timestampSeconds * 1000))
}

fun getCardBackgroundColor(isFavorited: Boolean): Color {
    return if (isFavorited) Color(0xFFD1C4E9) else Color.White
}

fun getImageUrl(product: Product): String {
    return "https://upload.wikimedia.org/wikipedia/commons/7/70/Example.png"
}
