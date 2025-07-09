package com.example.cursoandroid.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.cursoandroid.presentation.screens.auth.LoginScreen
import com.example.cursoandroid.presentation.screens.auth.RegisterScreen
import com.example.cursoandroid.presentation.screens.home.HomeScreen
import com.example.cursoandroid.presentation.screens.item.AddItemScreen
import com.example.cursoandroid.presentation.screens.item.ItemDetailScreen
import com.example.cursoandroid.presentation.screens.map.MapScreen
import com.example.cursoandroid.presentation.screens.map.NearbyMapScreen
import com.example.cursoandroid.presentation.screens.map.SelectLocationMapScreen
import com.example.cursoandroid.presentation.screens.profile.ProfileScreen
import com.example.cursoandroid.presentation.screens.profile.MyItemsScreen
import com.example.cursoandroid.presentation.screens.item.EditItemScreen
import com.example.cursoandroid.presentation.viewmodel.AuthViewModel
import com.example.cursoandroid.presentation.viewmodel.ItemViewModel
import com.example.cursoandroid.presentation.viewmodel.UserViewModel
import com.example.cursoandroid.presentation.viewmodel.ProfileViewModel

// Define el grafo de navegación de la app, es decir, las rutas y que pantalla mostrar en cada una.
@Composable
fun NavGraph(
    navController: NavHostController,
    startDestination: String,
    authViewModel: AuthViewModel,
    itemViewModel: ItemViewModel,
    userViewModel: UserViewModel,
    profileViewModel: ProfileViewModel
) {
    val currentUser by authViewModel.currentUser.collectAsState()
    
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        // Ruta de login (pública)
        composable(Screen.Login.route) {
            LoginScreen(
                navController = navController,
                authViewModel = authViewModel
            )
        }
        
        // Ruta de registro (pública)
        composable(Screen.Register.route) {
            RegisterScreen(
                navController = navController,
                authViewModel = authViewModel
            )
        }
        
        // Ruta principal (privada, requiere autenticación)
        composable(Screen.Home.route) {
            HomeScreen(
                navController = navController,
                itemViewModel = itemViewModel,
                authViewModel = authViewModel
            )
        }
        
        // Ruta para agregar un nuevo item (privada)
        composable(Screen.AddItem.route) {
            AddItemScreen(
                navController = navController,
                itemViewModel = itemViewModel,
                userViewModel = userViewModel
            )
        }
        
        // Ruta para ver el perfil de usuario (privada)
        composable(Screen.Profile.route) {
            ProfileScreen(
                navController = navController,
                profileViewModel = profileViewModel,
                currentUser = currentUser
            )
        }
        
        // Ruta para ver mapa de items cercanos (privada)
        composable(Screen.NearbyMap.route) {
            NearbyMapScreen(
                navController = navController,
                itemViewModel = itemViewModel
            )
        }
        

        
        // Ruta para ver el detalle de un item (privada)
        composable(
            route = Screen.ItemDetail.route,
            arguments = Screen.ItemDetail.arguments
        ) { backStackEntry ->
            // Obtiene el ID del item desde los argumentos de la ruta
            val itemId = backStackEntry.arguments?.getLong("itemId") ?: 0L
            ItemDetailScreen(
                navController = navController,
                itemViewModel = itemViewModel,
                userViewModel = userViewModel,
                itemId = itemId
            )
        }
        
        // Ruta para ver el mapa (privada)
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
        
        // Ruta para seleccionar una ubicación en el mapa (privada)
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
        
        // Ruta para ver mis items (privada)
        composable(Screen.MyItems.route) {
            MyItemsScreen(
                navController = navController,
                itemViewModel = itemViewModel,
                userViewModel = userViewModel
            )
        }
        
        // Ruta para editar un item (privada)
        composable(
            route = Screen.EditItem.route,
            arguments = Screen.EditItem.arguments
        ) { backStackEntry ->
            val itemId = backStackEntry.arguments?.getLong("itemId") ?: 0L
            EditItemScreen(
                navController = navController,
                itemViewModel = itemViewModel,
                userViewModel = userViewModel,
                itemId = itemId
            )
        }
    }
} 