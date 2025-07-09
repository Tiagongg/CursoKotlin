package com.example.cursoandroid.presentation.screens.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.cursoandroid.presentation.components.BottomNavigationBar
import com.example.cursoandroid.presentation.components.CategoryFilter
import com.example.cursoandroid.presentation.components.ItemCard
import com.example.cursoandroid.presentation.components.NavigationItem
import com.example.cursoandroid.presentation.navigation.Screen
import com.example.cursoandroid.presentation.viewmodel.AuthViewModel
import com.example.cursoandroid.presentation.viewmodel.ItemViewModel
import androidx.compose.ui.tooling.preview.Preview

@OptIn(ExperimentalMaterial3Api::class)
// Pantalla principal (Home) que muestra la lista de items y permite filtrar, agregar y navegar.
@Composable
fun HomeScreen(
    navController: NavController,
    itemViewModel: ItemViewModel,
    authViewModel: AuthViewModel
) {
    // Observa los estados de los ViewModels
    val selectedCategory by itemViewModel.selectedCategory.collectAsState()
    val searchQuery by itemViewModel.searchQuery.collectAsState()
    val isLoading by itemViewModel.isLoading.collectAsState()
    val error by itemViewModel.error.collectAsState()
    
    val filteredItems by itemViewModel.filteredItems.collectAsState()
    
    Scaffold(
        topBar = {
            // Barra superior con título y acciones
            TopAppBar(
                title = { Text("Marketplace") },
                actions = {
                    // Botón para agregar un nuevo item (movido desde FAB)
                    IconButton(onClick = { navController.navigate(Screen.AddItem.route) }) {
                        Icon(Icons.Default.Add, contentDescription = "Agregar Item")
                    }
                    // Botón para cerrar sesión
                    IconButton(onClick = { authViewModel.logout() }) {
                        Icon(Icons.Default.ExitToApp, contentDescription = "Cerrar Sesión")
                    }
                }
            )
        },
        bottomBar = {
            BottomNavigationBar(
                navController = navController,
                currentItem = NavigationItem.HOME
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Campo de búsqueda
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { itemViewModel.searchItems(it) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                placeholder = { Text("Buscar items...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Buscar") },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { itemViewModel.clearSearch() }) {
                            Icon(Icons.Default.Clear, contentDescription = "Limpiar búsqueda")
                        }
                    }
                },
                singleLine = true
            )
            
            // Filtro de categorías
            CategoryFilter(
                selectedCategory = selectedCategory,
                onCategorySelected = { category ->
                    // Cambia la categoría seleccionada en el ViewModel
                    itemViewModel.filterByCategory(category)
                },
                categories = listOf("Electrónicos", "Deportes", "Libros", "Otros")
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Muestra un indicador de carga si está cargando
            if (isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            // Muestra un mensaje de error si hay error
            } else if (error != null) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = error!!,
                            color = MaterialTheme.colorScheme.error
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(onClick = { /* itemViewModel.loadItems() */ }) {
                            Text("Reintentar")
                        }
                    }
                }
            // Muestra un mensaje si no hay items
            } else if (filteredItems.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (searchQuery.isNotEmpty()) "No se encontraron items" else "No hay items disponibles"
                    )
                }
            // Muestra la lista de items filtrados
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(filteredItems) { item ->
                        // Componente reutilizable para mostrar cada item
                        ItemCard(
                            item = item,
                            onClick = {
                                // Navega al detalle del item seleccionado
                                navController.navigate(Screen.ItemDetail.route.replace("{itemId}", item.id.toString()))
                            }
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true, heightDp = 700)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreenContentPreview() {
    MaterialTheme {
        HomeScreenContent(
            searchQuery = "",
            selectedCategory = null,
            isLoading = false,
            error = null,
            filteredItems = listOf(
                com.example.cursoandroid.data.model.Item(
                    id = 1,
                    title = "iPhone 13 Pro",
                    description = "Excelente estado, incluye cargador",
                    category = "Electrónicos",
                    price = 850.00,
                    imageUrl = "https://via.placeholder.com/150",
                    latitude = 40.4168,
                    longitude = -3.7038,
                    userId = 1
                ),
                com.example.cursoandroid.data.model.Item(
                    id = 2,
                    title = "MacBook Air M1",
                    description = "Perfecto para trabajo y estudios",
                    category = "Electrónicos",
                    price = 950.00,
                    imageUrl = "https://via.placeholder.com/150",
                    latitude = 40.4168,
                    longitude = -3.7038,
                    userId = 2
                )
            ),
            onSearchChange = {},
            onClearSearch = {},
            onCategorySelected = {},
            onAddItemClick = {},
            onLogoutClick = {},
            onItemClick = {}
        )
    }
}

@Preview(showBackground = true, heightDp = 500)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreenLoadingPreview() {
    MaterialTheme {
        HomeScreenContent(
            searchQuery = "",
            selectedCategory = null,
            isLoading = true,
            error = null,
            filteredItems = emptyList(),
            onSearchChange = {},
            onClearSearch = {},
            onCategorySelected = {},
            onAddItemClick = {},
            onLogoutClick = {},
            onItemClick = {}
        )
    }
}

@Preview(showBackground = true, heightDp = 500)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreenErrorPreview() {
    MaterialTheme {
        HomeScreenContent(
            searchQuery = "",
            selectedCategory = null,
            isLoading = false,
            error = "Error al cargar los items",
            filteredItems = emptyList(),
            onSearchChange = {},
            onClearSearch = {},
            onCategorySelected = {},
            onAddItemClick = {},
            onLogoutClick = {},
            onItemClick = {}
        )
    }
}

@Preview(showBackground = true, heightDp = 500)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreenEmptyPreview() {
    MaterialTheme {
        HomeScreenContent(
            searchQuery = "producto inexistente",
            selectedCategory = null,
            isLoading = false,
            error = null,
            filteredItems = emptyList(),
            onSearchChange = {},
            onClearSearch = {},
            onCategorySelected = {},
            onAddItemClick = {},
            onLogoutClick = {},
            onItemClick = {}
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HomeScreenContent(
    searchQuery: String,
    selectedCategory: String?,
    isLoading: Boolean,
    error: String?,
    filteredItems: List<com.example.cursoandroid.data.model.Item>,
    onSearchChange: (String) -> Unit,
    onClearSearch: () -> Unit,
    onCategorySelected: (String?) -> Unit,
    onAddItemClick: () -> Unit,
    onLogoutClick: () -> Unit,
    onItemClick: (com.example.cursoandroid.data.model.Item) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Marketplace") },
                actions = {
                    IconButton(onClick = onAddItemClick) {
                        Icon(Icons.Default.Add, contentDescription = "Agregar Item")
                    }
                    IconButton(onClick = onLogoutClick) {
                        Icon(Icons.Default.ExitToApp, contentDescription = "Cerrar Sesión")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Campo de búsqueda
            OutlinedTextField(
                value = searchQuery,
                onValueChange = onSearchChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                placeholder = { Text("Buscar items...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Buscar") },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = onClearSearch) {
                            Icon(Icons.Default.Clear, contentDescription = "Limpiar búsqueda")
                        }
                    }
                },
                singleLine = true
            )
            
            // Filtro de categorías
            CategoryFilter(
                selectedCategory = selectedCategory,
                onCategorySelected = onCategorySelected,
                categories = listOf("Electrónicos", "Deportes", "Libros", "Otros")
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Muestra un indicador de carga si está cargando
            if (isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            // Muestra un mensaje de error si hay error
            } else if (error != null) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = error,
                            color = MaterialTheme.colorScheme.error
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(onClick = { }) {
                            Text("Reintentar")
                        }
                    }
                }
            // Muestra un mensaje si no hay items
            } else if (filteredItems.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (searchQuery.isNotEmpty()) "No se encontraron items" else "No hay items disponibles"
                    )
                }
            // Muestra la lista de items filtrados
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(filteredItems) { item ->
                        ItemCard(
                            item = item,
                            onClick = { onItemClick(item) }
                        )
                    }
                }
            }
        }
    }
} 