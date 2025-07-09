package com.example.cursoandroid.presentation.screens.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.cursoandroid.data.model.Item
import com.example.cursoandroid.presentation.viewmodel.ItemViewModel
import com.example.cursoandroid.presentation.viewmodel.UserViewModel
import androidx.compose.ui.tooling.preview.Preview
import com.example.cursoandroid.data.model.User

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyItemsScreen(
    navController: NavController,
    itemViewModel: ItemViewModel,
    userViewModel: UserViewModel
) {
    val currentUser by userViewModel.currentUser.collectAsState()
    val myItems by itemViewModel.getItemsByUser(currentUser?.id ?: 0L).collectAsState()
    val isLoading by itemViewModel.isLoading.collectAsState()
    
    var showDeleteDialog by remember { mutableStateOf<Item?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mis Publicaciones") },
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
                .padding(paddingValues)
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            } else if (myItems.isEmpty()) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        Icons.Default.Edit,
                        contentDescription = null,
                        modifier = Modifier.size(64.dp),
                        tint = MaterialTheme.colorScheme.outline
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "No tienes publicaciones",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.outline
                    )
                    Text(
                        text = "Crea tu primer item desde la pantalla principal",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.outline
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(myItems) { item ->
                        MyItemCard(
                            item = item,
                            onEditClick = { navController.navigate("edit_item/${item.id}") },
                            onDeleteClick = { showDeleteDialog = item },
                            onItemClick = { navController.navigate("item_detail/${item.id}") }
                        )
                    }
                }
            }
        }
    }
    
    // Diálogo de confirmación para eliminar
    showDeleteDialog?.let { item ->
        AlertDialog(
            onDismissRequest = { showDeleteDialog = null },
            title = { Text("Eliminar publicación") },
            text = { Text("¿Estás seguro de que quieres eliminar \"${item.title}\"?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        itemViewModel.deleteItem(item.id)
                        showDeleteDialog = null
                    }
                ) {
                    Text("Eliminar", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = null }) {
                    Text("Cancelar")
                }
            }
        )
    }
}

@Composable
private fun MyItemCard(
    item: Item,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit,
    onItemClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onItemClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Imagen del item
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(item.imageUrl ?: "https://via.placeholder.com/150")
                    .crossfade(true)
                    .build(),
                contentDescription = "Imagen de ${item.title}",
                modifier = Modifier
                    .size(60.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                contentScale = ContentScale.Crop
            )
            
            Spacer(modifier = Modifier.width(12.dp))
            
            // Información del item
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = item.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = item.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "€${String.format("%.2f", item.price)}",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
            }
            
            // Botones de acción
            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                IconButton(
                    onClick = onEditClick,
                    modifier = Modifier.size(40.dp)
                ) {
                    Icon(
                        Icons.Default.Edit,
                        contentDescription = "Editar",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
                IconButton(
                    onClick = onDeleteClick,
                    modifier = Modifier.size(40.dp)
                ) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = "Eliminar",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MyItemCardPreview() {
    MaterialTheme {
        MyItemCard(
            item = Item(
                id = 1,
                title = "iPhone 13 Pro",
                description = "Excelente estado, incluye cargador y caja original",
                category = "Electrónicos",
                price = 850.00,
                imageUrl = "https://via.placeholder.com/150",
                latitude = 40.4168,
                longitude = -3.7038,
                userId = 1
            ),
            onEditClick = {},
            onDeleteClick = {},
            onItemClick = {}
        )
    }
}

@Preview(showBackground = true, heightDp = 400)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyItemsEmptyStatePreview() {
    MaterialTheme {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Mis Publicaciones") },
                    navigationIcon = {
                        IconButton(onClick = {}) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                        }
                    }
                )
            }
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        Icons.Default.Edit,
                        contentDescription = null,
                        modifier = Modifier.size(64.dp),
                        tint = MaterialTheme.colorScheme.outline
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "No tienes publicaciones",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.outline
                    )
                    Text(
                        text = "Crea tu primer item desde la pantalla principal",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.outline
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true, heightDp = 600)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyItemsWithDataPreview() {
    MaterialTheme {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Mis Publicaciones") },
                    navigationIcon = {
                        IconButton(onClick = {}) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                        }
                    }
                )
            }
        ) { paddingValues ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(listOf(
                    Item(
                        id = 1,
                        title = "iPhone 13 Pro",
                        description = "Excelente estado, incluye cargador y caja original",
                        category = "Electrónicos",
                        price = 850.00,
                        imageUrl = "https://via.placeholder.com/150",
                        latitude = 40.4168,
                        longitude = -3.7038,
                        userId = 1
                    ),
                    Item(
                        id = 2,
                        title = "MacBook Air M1",
                        description = "Poco uso, perfecto para estudiantes",
                        category = "Electrónicos",
                        price = 950.00,
                        imageUrl = "https://via.placeholder.com/150",
                        latitude = 40.4168,
                        longitude = -3.7038,
                        userId = 1
                    ),
                    Item(
                        id = 3,
                        title = "Bicicleta de montaña",
                        description = "Trek modelo 2022, talla M",
                        category = "Deportes",
                        price = 450.00,
                        imageUrl = "https://via.placeholder.com/150",
                        latitude = 40.4168,
                        longitude = -3.7038,
                        userId = 1
                    )
                )) { item ->
                    MyItemCard(
                        item = item,
                        onEditClick = {},
                        onDeleteClick = {},
                        onItemClick = {}
                    )
                }
            }
        }
    }
} 