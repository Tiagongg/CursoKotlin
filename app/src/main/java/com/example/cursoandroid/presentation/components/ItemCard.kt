package com.example.cursoandroid.presentation.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.cursoandroid.data.model.Item
import androidx.compose.ui.tooling.preview.Preview

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ItemCard(
    item: Item,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        onClick = onClick
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Imagen del item
            item.imageUrl?.let { url ->
                AsyncImage(
                    model = url,
                    contentDescription = item.title,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    contentScale = ContentScale.Crop
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
            
            // Información del item
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = item.title,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                Text(
                    text = item.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Categoría
                    AssistChip(
                        onClick = { },
                        label = { Text(item.category) }
                    )
                    
                    // Precio
                    Text(
                        text = "€${item.price}",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                
                // Ubicación si existe
                if (item.latitude != 0.0 && item.longitude != 0.0) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.LocationOn,
                            contentDescription = "Ubicación",
                            modifier = Modifier.size(16.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Ubicación disponible",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ItemCardPreview() {
    MaterialTheme {
        ItemCard(
            item = Item(
                id = 1,
                title = "iPhone 13 Pro Max",
                description = "Excelente estado, incluye cargador original y caja. Usado por solo 6 meses.",
                price = 850.00,
                imageUrl = "https://via.placeholder.com/300x200",
                latitude = 40.4168,
                longitude = -3.7038,
                userId = 1,
                category = "Electrónicos"
            ),
            onClick = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ItemCardWithoutImagePreview() {
    MaterialTheme {
        ItemCard(
            item = Item(
                id = 2,
                title = "Bicicleta de montaña Trek",
                description = "Bicicleta en perfecto estado, ideal para rutas de montaña.",
                price = 450.00,
                imageUrl = null,
                latitude = 0.0,
                longitude = 0.0,
                userId = 2,
                category = "Deportes"
            ),
            onClick = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ItemCardNoLocationPreview() {
    MaterialTheme {
        ItemCard(
            item = Item(
                id = 3,
                title = "Libro de programación Android",
                description = "Manual completo para desarrolladores Android, edición 2023.",
                price = 25.00,
                imageUrl = "https://via.placeholder.com/300x200",
                latitude = 0.0,
                longitude = 0.0,
                userId = 3,
                category = "Libros"
            ),
            onClick = {}
        )
    }
} 