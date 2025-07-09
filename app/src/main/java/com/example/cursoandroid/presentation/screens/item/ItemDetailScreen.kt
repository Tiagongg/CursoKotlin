package com.example.cursoandroid.presentation.screens.item

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
// import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.compose.ui.tooling.preview.Preview
import coil.compose.AsyncImage
import com.example.cursoandroid.data.model.Item
import com.example.cursoandroid.presentation.navigation.Screen
import com.example.cursoandroid.presentation.viewmodel.ItemViewModel
import com.example.cursoandroid.presentation.viewmodel.UserViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ItemDetailScreen(
    navController: NavController,
    itemViewModel: ItemViewModel,
    userViewModel: UserViewModel,
    itemId: Long
) {
    val items by itemViewModel.items.collectAsState()
    val item = items.find { it.id == itemId }
    val currentUser by userViewModel.currentUser.collectAsState()
    val isOwner = item?.userId == currentUser?.id

    if (item == null) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Detalle del Item") },
                    navigationIcon = {
                        IconButton(onClick = { navController.navigateUp() }) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                        }
                    }
                )
            }
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Text("Item no encontrado")
            }
        }
    } else {
        ItemDetailScreenContent(
            item = item,
            isOwner = isOwner,
            onBackClick = { navController.navigateUp() },
            onEditClick = { navController.navigate("edit_item/${itemId}") },
            onDeleteClick = {
                itemViewModel.deleteItem(item.id)
                navController.navigate(Screen.Home.route) {
                    popUpTo(Screen.Home.route) { inclusive = true }
                }
            },
            onViewOnMapClick = {
                navController.navigate("${Screen.Map.route.replace("{itemId}", item.id.toString())}")
            }
        )
    }
}

@Preview(showBackground = true, heightDp = 700)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ItemDetailScreenPreview() {
    MaterialTheme {
        ItemDetailScreenContent(
            item = Item(
                id = 1,
                title = "iPhone 13 Pro Max",
                description = "Excelente estado, incluye cargador original y caja. Usado solo por 6 meses, sin rayones ni golpes. Incluye funda y protector de pantalla aplicado desde el primer día.",
                category = "Electrónicos",
                price = 850.00,
                imageUrl = "https://via.placeholder.com/300x200",
                latitude = 40.4168,
                longitude = -3.7038,
                userId = 1
            ),
            isOwner = true,
            onBackClick = {},
            onEditClick = {},
            onDeleteClick = {},
            onViewOnMapClick = {}
        )
    }
}

@Preview(showBackground = true, heightDp = 700)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ItemDetailScreenNotOwnerPreview() {
    MaterialTheme {
        ItemDetailScreenContent(
            item = Item(
                id = 2,
                title = "Bicicleta de montaña Trek",
                description = "Bicicleta en perfecto estado, ideal para rutas de montaña. Mantenimiento reciente, cambios Shimano, frenos de disco.",
                category = "Deportes",
                price = 450.00,
                imageUrl = "https://via.placeholder.com/300x200",
                latitude = 40.4168,
                longitude = -3.7038,
                userId = 2
            ),
            isOwner = false,
            onBackClick = {},
            onEditClick = {},
            onDeleteClick = {},
            onViewOnMapClick = {}
        )
    }
}

@Preview(showBackground = true, heightDp = 400)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ItemDetailScreenNoImagePreview() {
    MaterialTheme {
        ItemDetailScreenContent(
            item = Item(
                id = 3,
                title = "Libro de programación Kotlin",
                description = "Manual completo para desarrolladores Android con Kotlin, edición 2023.",
                category = "Libros",
                price = 25.00,
                imageUrl = null,
                latitude = 40.4168,
                longitude = -3.7038,
                userId = 3
            ),
            isOwner = false,
            onBackClick = {},
            onEditClick = {},
            onDeleteClick = {},
            onViewOnMapClick = {}
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ItemDetailScreenContent(
    item: Item,
    isOwner: Boolean,
    onBackClick: () -> Unit,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit,
    onViewOnMapClick: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detalle del Item") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                },
                actions = {
                    if (isOwner) {
                        IconButton(onClick = onEditClick) {
                            Icon(Icons.Default.Edit, contentDescription = "Editar")
                        }
                        IconButton(onClick = onDeleteClick) {
                            Icon(Icons.Default.Delete, contentDescription = "Eliminar")
                        }
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Imagen del item
            item.imageUrl?.let { imageUrl ->
                Card(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    AsyncImage(
                        model = imageUrl,
                        contentDescription = "Imagen del item",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp),
                        contentScale = ContentScale.Crop
                    )
                }
            }
            
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = item.title,
                        style = MaterialTheme.typography.headlineSmall
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Text(
                        text = item.description,
                        style = MaterialTheme.typography.bodyLarge
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Categoría: ${item.category}",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Text(
                            text = "€${item.price}",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
            
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Ubicación",
                        style = MaterialTheme.typography.titleMedium
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Text(
                        text = "Latitud: ${item.latitude}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        text = "Longitud: ${item.longitude}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Button(
                        onClick = onViewOnMapClick,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.LocationOn, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Ver en Mapa")
                    }
                }
            }
        }
    }
} 