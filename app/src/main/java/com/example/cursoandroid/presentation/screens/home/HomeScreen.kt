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
@Composable
fun HomeScreen(
    navController: NavController,
    itemViewModel: ItemViewModel,
    authViewModel: AuthViewModel
) {
    val items by itemViewModel.items.collectAsState()
    val selectedCategory by itemViewModel.selectedCategory.collectAsState()
    val isLoading by itemViewModel.isLoading.collectAsState()
    val error by itemViewModel.error.collectAsState()
    
    val filteredItems by itemViewModel.filteredItems.collectAsState()
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mi App") },
                actions = {
                    IconButton(onClick = { navController.navigate(Screen.Map.route) }) {
                        Icon(Icons.Default.LocationOn, contentDescription = "Mapa")
                    }
                    IconButton(onClick = { authViewModel.logout() }) {
                        Icon(Icons.Default.ExitToApp, contentDescription = "Cerrar Sesión")
                    }
                }
            )
        },
        floatingActionButton = {
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
            // Filtro de categorías
            CategoryFilter(
                selectedCategory = selectedCategory,
                onCategorySelected = { category ->
                    itemViewModel.filterByCategory(category)
                },
                categories = listOf("Electrónicos", "Deportes", "Libros", "Otros")
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Lista de items
            if (isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
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
            } else if (filteredItems.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No hay items disponibles")
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(filteredItems) { item ->
                        ItemCard(
                            item = item,
                            onClick = {
                                navController.navigate(Screen.ItemDetail.route.replace("{itemId}", item.id.toString()))
                            }
                        )
                    }
                }
            }
        }
    }
} 