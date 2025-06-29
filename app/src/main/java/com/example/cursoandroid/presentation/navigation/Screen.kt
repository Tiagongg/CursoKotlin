package com.example.cursoandroid.presentation.navigation

import androidx.navigation.NavType
import androidx.navigation.navArgument

sealed class Screen(val route: String) {
    // Rutas públicas
    object Login : Screen("login")
    object Register : Screen("register")
    
    // Rutas privadas
    object Home : Screen("home")
    object AddItem : Screen("add_item")
    object ItemDetail : Screen("item_detail/{itemId}") {
        val arguments = listOf(
            navArgument("itemId") { type = NavType.LongType }
        )
    }
    object Map : Screen("map/{itemId}") {
        val arguments = listOf(
            navArgument("itemId") { type = NavType.LongType; defaultValue = -1L }
        )
    }
    object SelectLocation : Screen("select_location") {
        val arguments = emptyList<androidx.navigation.NamedNavArgument>()
    }
} 