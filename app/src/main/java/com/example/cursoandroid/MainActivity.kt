package com.example.cursoandroid

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.example.cursoandroid.presentation.CursoAndroidApp
import com.example.cursoandroid.ui.theme.CursoAndroidTheme

// Actividad principal de la aplicación. Es el punto de entrada de la app.
class MainActivity : ComponentActivity() {
    // Método que se llama al crear la actividad
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Establece el contenido de la pantalla usando Jetpack Compose
        setContent {
            // Aplica el tema personalizado de la app
            CursoAndroidTheme {
                // Surface define el fondo de la pantalla
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // Llama al componente principal de la app
                    CursoAndroidApp()
                }
            }
        }
    }
}