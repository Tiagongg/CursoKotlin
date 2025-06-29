package com.example.cursoandroid.presentation.screens.map

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelectLocationMapScreen(
    navController: NavController,
    initialLat: Double?,
    initialLng: Double?
) {
    val defaultLatLng = LatLng(initialLat ?: 40.4168, initialLng ?: -3.7038)
    var selectedLatLng by remember { mutableStateOf<LatLng?>(null) }
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(defaultLatLng, 14f)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Selecciona ubicación") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        },
        bottomBar = {
            Button(
                onClick = {
                    selectedLatLng?.let {
                        navController.previousBackStackEntry?.savedStateHandle?.set("selected_location", it)
                        navController.popBackStack()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                enabled = selectedLatLng != null
            ) {
                Text("Confirmar ubicación")
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            GoogleMap(
                modifier = Modifier.fillMaxSize(),
                cameraPositionState = cameraPositionState,
                onMapClick = { latLng -> selectedLatLng = latLng }
            ) {
                selectedLatLng?.let {
                    Marker(
                        state = MarkerState(position = it),
                        title = "Ubicación seleccionada"
                    )
                }
            }
        }
    }
} 