package com.example.cursoandroid.presentation.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun CategoryFilter(
    selectedCategory: String?,
    onCategorySelected: (String?) -> Unit,
    categories: List<String>
) {
    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Opción "Todas"
        item {
            FilterChip(
                selected = selectedCategory == null,
                onClick = { onCategorySelected(null) },
                label = { Text("Todas") }
            )
        }
        
        // Categorías disponibles
        items(categories) { category ->
            FilterChip(
                selected = selectedCategory == category,
                onClick = { onCategorySelected(category) },
                label = { Text(category) }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CategoryFilterAllSelectedPreview() {
    MaterialTheme {
        CategoryFilter(
            selectedCategory = null,
            onCategorySelected = {},
            categories = listOf("Electrónicos", "Deportes", "Libros", "Otros")
        )
    }
}

@Preview(showBackground = true)
@Composable
fun CategoryFilterElectronicsSelectedPreview() {
    MaterialTheme {
        CategoryFilter(
            selectedCategory = "Electrónicos",
            onCategorySelected = {},
            categories = listOf("Electrónicos", "Deportes", "Libros", "Otros")
        )
    }
}

@Preview(showBackground = true)
@Composable
fun CategoryFilterManyCategoriesPreview() {
    MaterialTheme {
        CategoryFilter(
            selectedCategory = "Hogar",
            onCategorySelected = {},
            categories = listOf("Electrónicos", "Deportes", "Libros", "Hogar", "Vehículos", "Moda", "Muebles", "Jardín")
        )
    }
} 