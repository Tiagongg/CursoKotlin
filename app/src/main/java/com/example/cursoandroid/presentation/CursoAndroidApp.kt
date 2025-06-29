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

@Composable
fun CursoAndroidApp() {
    CursoAndroidTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            val context = LocalContext.current.applicationContext as Application
            val authViewModel = remember { AuthViewModel(context) }
            val itemViewModel = remember { ItemViewModel(context) }
            val currentUser by authViewModel.currentUser.collectAsState()
            val navController = rememberNavController()
            
            // Determinar la pantalla inicial basada en el estado de autenticación
            val startDestination = if (currentUser != null) {
                Screen.Home.route
            } else {
                Screen.Login.route
            }
            
            NavGraph(
                authViewModel = authViewModel,
                itemViewModel = itemViewModel,
                navController = navController,
                startDestination = startDestination
            )
        }
    }
} 