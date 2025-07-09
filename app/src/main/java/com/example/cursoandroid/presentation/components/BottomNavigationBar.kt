package com.example.cursoandroid.presentation.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.compose.ui.tooling.preview.Preview
import com.example.cursoandroid.presentation.navigation.Screen

enum class NavigationItem {
    HOME, MAP, PROFILE
}

@Composable
fun BottomNavigationBar(
    navController: NavController,
    currentItem: NavigationItem
) {
    BottomNavigationBarContent(
        currentItem = currentItem,
        onHomeClick = { 
            if (currentItem != NavigationItem.HOME) {
                navController.navigate(Screen.Home.route)
            }
        },
        onMapClick = { 
            if (currentItem != NavigationItem.MAP) {
                navController.navigate(Screen.NearbyMap.route)
            }
        },
        onProfileClick = { 
            if (currentItem != NavigationItem.PROFILE) {
                navController.navigate(Screen.Profile.route)
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
fun BottomNavigationBarHomePreview() {
    MaterialTheme {
        BottomNavigationBarContent(
            currentItem = NavigationItem.HOME,
            onHomeClick = {},
            onMapClick = {},
            onProfileClick = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun BottomNavigationBarMapPreview() {
    MaterialTheme {
        BottomNavigationBarContent(
            currentItem = NavigationItem.MAP,
            onHomeClick = {},
            onMapClick = {},
            onProfileClick = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun BottomNavigationBarProfilePreview() {
    MaterialTheme {
        BottomNavigationBarContent(
            currentItem = NavigationItem.PROFILE,
            onHomeClick = {},
            onMapClick = {},
            onProfileClick = {}
        )
    }
}

@Composable
private fun BottomNavigationBarContent(
    currentItem: NavigationItem,
    onHomeClick: () -> Unit,
    onMapClick: () -> Unit,
    onProfileClick: () -> Unit
) {
    BottomAppBar(
        containerColor = MaterialTheme.colorScheme.primaryContainer,
        contentColor = MaterialTheme.colorScheme.onPrimaryContainer
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Botón de Home/Principal
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.weight(1f)
            ) {
                IconButton(onClick = onHomeClick) {
                    Icon(
                        Icons.Default.Home,
                        contentDescription = if (currentItem == NavigationItem.HOME) {
                            "Inicio, seleccionado"
                        } else {
                            "Ir a Inicio"
                        },
                        tint = if (currentItem == NavigationItem.HOME) 
                            MaterialTheme.colorScheme.primary 
                        else 
                            MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
                Text(
                    text = "Inicio",
                    style = MaterialTheme.typography.labelSmall,
                    color = if (currentItem == NavigationItem.HOME) 
                        MaterialTheme.colorScheme.primary 
                    else 
                        MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
            
            // Botón de Mapa
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.weight(1f)
            ) {
                IconButton(onClick = onMapClick) {
                    Icon(
                        Icons.Default.Place,
                        contentDescription = if (currentItem == NavigationItem.MAP) {
                            "Mapa, seleccionado"
                        } else {
                            "Ir a Mapa"
                        },
                        tint = if (currentItem == NavigationItem.MAP) 
                            MaterialTheme.colorScheme.primary 
                        else 
                            MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
                Text(
                    text = "Mapa",
                    style = MaterialTheme.typography.labelSmall,
                    color = if (currentItem == NavigationItem.MAP) 
                        MaterialTheme.colorScheme.primary 
                    else 
                        MaterialTheme.colorScheme.onPrimaryContainer
                )
            }

            // Botón de Perfil
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.weight(1f)
            ) {
                IconButton(onClick = onProfileClick) {
                    Icon(
                        Icons.Default.Person,
                        contentDescription = if (currentItem == NavigationItem.PROFILE) {
                            "Mi Perfil, seleccionado"
                        } else {
                            "Ir a Mi Perfil"
                        },
                        tint = if (currentItem == NavigationItem.PROFILE) 
                            MaterialTheme.colorScheme.primary 
                        else 
                            MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
                Text(
                    text = "Perfil",
                    style = MaterialTheme.typography.labelSmall,
                    color = if (currentItem == NavigationItem.PROFILE) 
                        MaterialTheme.colorScheme.primary 
                    else 
                        MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }
    }
} 