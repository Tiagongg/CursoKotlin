package com.example.cursoandroid.presentation.screens.map

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.cursoandroid.data.model.Item
import com.example.cursoandroid.presentation.viewmodel.ItemViewModel
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.maps.android.compose.*
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import kotlin.math.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
fun MapScreen(
    navController: NavController,
    itemViewModel: ItemViewModel,
    itemId: Long
) {
    val context = LocalContext.current
    val items by itemViewModel.items.collectAsState()
    val targetItem = items.find { it.id == itemId }
    
    // Estados para ubicación del usuario - Simplificado
    var userLocation by remember { mutableStateOf<LatLng?>(null) }
    
    // Permiso de ubicación
    val locationPermissionState = rememberPermissionState(
        Manifest.permission.ACCESS_FINE_LOCATION
    )
    
    // Launcher para pedir permisos
    val requestPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            // Solo obtener ubicación inicial para vista de items cercanos
            if (itemId == -1L) {
                val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
                fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                    location?.let {
                        userLocation = LatLng(it.latitude, it.longitude)
                    }
                }
            }
        }
    }
    
    // Filtrar items cercanos (dentro de 100km) si tenemos ubicación del usuario
    val nearbyItems = remember(items, userLocation) {
        userLocation?.let { location ->
            items.filter { item ->
                if (item.latitude != 0.0 && item.longitude != 0.0) {
                    val distance = calculateDistance(
                        location.latitude,
                        location.longitude,
                        item.latitude,
                        item.longitude
                    )
                    distance <= 100.0 // Máximo 100km
                } else {
                    false
                }
            }
        } ?: items.filter { it.latitude != 0.0 && it.longitude != 0.0 }
    }

    val defaultLocation = LatLng(40.4168, -3.7038) // Madrid por defecto
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(
            targetItem?.let { LatLng(it.latitude, it.longitude) } 
                ?: userLocation 
                ?: defaultLocation,
            if (targetItem != null) 15f else 10f
        )
    }
    
    // Actualizar posición de cámara cuando obtengamos la ubicación del usuario
    LaunchedEffect(userLocation) {
        userLocation?.let { location ->
            if (targetItem == null) { // Solo para vista de items cercanos
                cameraPositionState.animate(
                    CameraUpdateFactory.newLatLngZoom(location, 12f),
                    1000 // Duración de animación en ms
                )
            }
        }
    }

    // Obtener ubicación automáticamente para vista de items cercanos
    LaunchedEffect(locationPermissionState.status, itemId) {
        if (itemId == -1L) { // Solo para vista de items cercanos
            if (locationPermissionState.status.isGranted) {
                val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
                fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                    location?.let {
                        userLocation = LatLng(it.latitude, it.longitude)
                    }
                }
            } else {
                requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        if (targetItem != null) "Ubicación del Item" 
                        else "Items Cercanos"
                    ) 
                },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                },
                actions = {
                    if (targetItem == null) {
                        IconButton(
                            onClick = {
                                if (locationPermissionState.status.isGranted) {
                                    // Simplemente centrar el mapa en la ubicación actual
                                    centerMapOnUserLocation(context, cameraPositionState)
                                } else {
                                    requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                                }
                            }
                        ) {
                            Icon(
                                Icons.Default.LocationOn, 
                                contentDescription = "Centrar en mi ubicación",
                                tint = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            )
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
                uiSettings = MapUiSettings(
                    myLocationButtonEnabled = false,
                    zoomControlsEnabled = true
                )
            ) {
                // Marcador del usuario siempre visible si tenemos su ubicación
                userLocation?.let { location ->
                    Marker(
                        state = MarkerState(position = location),
                        title = "Mi ubicación actual",
                        snippet = "Tu ubicación actual",
                        icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)
                    )
                }
                
                // Si estamos viendo un item específico, mostrar ese item
                if (targetItem != null) {
                    Marker(
                        state = MarkerState(position = LatLng(targetItem.latitude, targetItem.longitude)),
                        title = targetItem.title,
                        snippet = "${targetItem.description} - €${targetItem.price}",
                        onInfoWindowClick = {
                            navController.navigate("item_detail/${targetItem.id}")
                        }
                    )
                } else {
                    // Mostrar todos los items cercanos
                    nearbyItems.forEach { item ->
                        Marker(
                            state = MarkerState(
                                position = LatLng(item.latitude, item.longitude)
                            ),
                            title = item.title,
                            snippet = "${item.description} - €${item.price}",
                            icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED),
                            onInfoWindowClick = {
                                navController.navigate("item_detail/${item.id}")
                            }
                        )
                    }
                }
            }
        }
    }
}

private fun centerMapOnUserLocation(
    context: android.content.Context,
    cameraPositionState: CameraPositionState
) {
    val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
    
    try {
        if (androidx.core.content.ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == android.content.pm.PackageManager.PERMISSION_GRANTED
        ) {
            fusedLocationClient.lastLocation
                .addOnSuccessListener { location ->
                    if (location != null) {
                        val userLatLng = LatLng(location.latitude, location.longitude)
                        // Animar la cámara hacia la ubicación del usuario
                        CoroutineScope(Dispatchers.Main).launch {
                            cameraPositionState.animate(
                                CameraUpdateFactory.newLatLngZoom(userLatLng, 15f),
                                1000 // 1 segundo de animación
                            )
                        }
                    }
                }
        }
    } catch (e: SecurityException) {
        // Manejo silencioso del error
    }
}

private fun calculateDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
    val earthRadius = 6371.0 // Radio de la Tierra en kilómetros
    
    val dLat = Math.toRadians(lat2 - lat1)
    val dLon = Math.toRadians(lon2 - lon1)
    
    val a = sin(dLat / 2) * sin(dLat / 2) +
            cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) *
            sin(dLon / 2) * sin(dLon / 2)
    
    val c = 2 * atan2(sqrt(a), sqrt(1 - a))
    
    return earthRadius * c
} 