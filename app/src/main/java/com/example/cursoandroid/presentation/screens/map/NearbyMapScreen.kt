package com.example.cursoandroid.presentation.screens.map

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.example.cursoandroid.presentation.components.BottomNavigationBar
import com.example.cursoandroid.presentation.components.NavigationItem
import com.example.cursoandroid.presentation.viewmodel.ItemViewModel

@Composable
fun NearbyMapScreen(
    navController: NavController,
    itemViewModel: ItemViewModel
) {
    Scaffold(
        bottomBar = {
            BottomNavigationBar(
                navController = navController,
                currentItem = NavigationItem.MAP
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Usar el MapScreen existente con itemId = -1 para mostrar todos los items cercanos
            MapScreen(
                navController = navController,
                itemViewModel = itemViewModel,
                itemId = -1L
            )
        }
    }
} 