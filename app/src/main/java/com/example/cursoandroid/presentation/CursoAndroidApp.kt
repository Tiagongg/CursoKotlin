package com.example.cursoandroid.presentation

import android.app.Application
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.rememberNavController
import com.example.cursoandroid.presentation.navigation.NavGraph
import com.example.cursoandroid.presentation.navigation.Screen
import com.example.cursoandroid.presentation.viewmodel.AuthViewModel
import com.example.cursoandroid.presentation.viewmodel.ItemViewModel
import com.example.cursoandroid.ui.theme.CursoAndroidTheme

// Composable principal de la app. Configura el tema, los ViewModels y la navegación.
@Composable
fun CursoAndroidApp() {
    CursoAndroidTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            // Obtiene el contexto de la aplicación
            val context = LocalContext.current.applicationContext as Application
            // Inicializa los ViewModels de autenticación y de items
            val authViewModel = remember { AuthViewModel(context) }
            val itemViewModel = remember { ItemViewModel(context) }
            // Observa el usuario actual para saber si está autenticado
            val currentUser by authViewModel.currentUser.collectAsState()
            // Controlador de navegación
            val navController = rememberNavController()
            
            // Determina la pantalla inicial según si hay usuario autenticado
            val startDestination = if (currentUser != null) {
                Screen.Home.route
            } else {
                Screen.Login.route
            }
            
            // Configura el grafo de navegación de la app
            NavGraph(
                authViewModel = authViewModel,
                itemViewModel = itemViewModel,
                navController = navController,
                startDestination = startDestination
            )
        }
    }
} 