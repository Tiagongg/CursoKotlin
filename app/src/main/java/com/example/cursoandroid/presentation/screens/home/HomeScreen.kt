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
import com.example.cursoandroid.presentation.components.CategoryFilter
import com.example.cursoandroid.presentation.components.ItemCard
import com.example.cursoandroid.presentation.navigation.Screen
import com.example.cursoandroid.presentation.viewmodel.AuthViewModel
import com.example.cursoandroid.presentation.viewmodel.ItemViewModel

@OptIn(ExperimentalMaterial3Api::class)
// Pantalla principal (Home) que muestra la lista de items y permite filtrar, agregar y navegar.
@Composable
fun HomeScreen(
    navController: NavController,
    itemViewModel: ItemViewModel,
    authViewModel: AuthViewModel
) {
    // Observa los estados de los ViewModels
    val items by itemViewModel.items.collectAsState()
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
                    // Botón para cerrar sesión
                    IconButton(onClick = { authViewModel.logout() }) {
                        Icon(Icons.Default.ExitToApp, contentDescription = "Cerrar Sesión")
                    }
                }
            )
        },
        floatingActionButton = {
            // Botón flotante para agregar un nuevo item
            FloatingActionButton(
                onClick = { navController.navigate(Screen.AddItem.route) }
            ) {
                Icon(Icons.Default.Add, contentDescription = "Agregar Item")
            }
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