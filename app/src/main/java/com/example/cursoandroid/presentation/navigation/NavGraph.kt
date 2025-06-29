package com.example.cursoandroid.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.cursoandroid.presentation.screens.auth.LoginScreen
import com.example.cursoandroid.presentation.screens.auth.RegisterScreen
import com.example.cursoandroid.presentation.screens.home.HomeScreen
import com.example.cursoandroid.presentation.screens.item.AddItemScreen
import com.example.cursoandroid.presentation.screens.item.ItemDetailScreen
import com.example.cursoandroid.presentation.screens.map.MapScreen
import com.example.cursoandroid.presentation.screens.map.SelectLocationMapScreen
import com.example.cursoandroid.presentation.viewmodel.AuthViewModel
import com.example.cursoandroid.presentation.viewmodel.ItemViewModel

@Composable
fun NavGraph(
    navController: NavHostController,
    startDestination: String,
    authViewModel: AuthViewModel,
    itemViewModel: ItemViewModel
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        // Rutas públicas (sin autenticación)
        composable(Screen.Login.route) {
            LoginScreen(
                navController = navController,
                authViewModel = authViewModel
            )
        }
        
        composable(Screen.Register.route) {
            RegisterScreen(
                navController = navController,
                authViewModel = authViewModel
            )
        }
        
        // Rutas privadas (requieren autenticación)
        composable(Screen.Home.route) {
            HomeScreen(
                navController = navController,
                itemViewModel = itemViewModel,
                authViewModel = authViewModel
            )
        }
        
        composable(Screen.AddItem.route) {
            AddItemScreen(
                navController = navController,
                itemViewModel = itemViewModel
            )
        }
        
        composable(
            route = Screen.ItemDetail.route,
            arguments = Screen.ItemDetail.arguments
        ) { backStackEntry ->
            val itemId = backStackEntry.arguments?.getLong("itemId") ?: 0L
            ItemDetailScreen(
                navController = navController,
                itemViewModel = itemViewModel,
                itemId = itemId
            )
        }
        
        composable(
            route = Screen.Map.route,
            arguments = Screen.Map.arguments
        ) { backStackEntry ->
            val itemId = backStackEntry.arguments?.getLong("itemId") ?: -1L
            MapScreen(
                navController = navController,
                itemViewModel = itemViewModel,
                itemId = itemId
            )
        }
        
        composable(
            route = Screen.SelectLocation.route,
            arguments = Screen.SelectLocation.arguments
        ) { backStackEntry ->
            val lat = backStackEntry.arguments?.getString("lat")?.toDoubleOrNull()
            val lng = backStackEntry.arguments?.getString("lng")?.toDoubleOrNull()
            SelectLocationMapScreen(
                navController = navController,
                initialLat = lat,
                initialLng = lng
            )
        }
    }
} 