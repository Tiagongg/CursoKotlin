package com.example.cursoandroid.presentation.screens.item

import android.Manifest
import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.navigation.NavController
import androidx.compose.ui.tooling.preview.Preview
import coil.compose.AsyncImage
import com.example.cursoandroid.data.model.Item
import com.example.cursoandroid.presentation.navigation.Screen
import com.example.cursoandroid.presentation.viewmodel.ItemViewModel
import com.example.cursoandroid.presentation.viewmodel.UserViewModel
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.location.LocationServices
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditItemScreen(
    navController: NavController,
    itemViewModel: ItemViewModel,
    userViewModel: UserViewModel,
    itemId: Long
) {
    val context = LocalContext.current
    val currentUser by userViewModel.currentUser.collectAsState()
    
    // Estados del formulario
    var item by remember { mutableStateOf<Item?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var title by rememberSaveable { mutableStateOf("") }
    var description by rememberSaveable { mutableStateOf("") }
    var category by rememberSaveable { mutableStateOf("Electrónicos") }
    var price by rememberSaveable { mutableStateOf("") }
    var location by remember { mutableStateOf<LatLng?>(null) }
    var expanded by rememberSaveable { mutableStateOf(false) }
    var imageUri by rememberSaveable { mutableStateOf<Uri?>(null) }
    var hasImageChanged by remember { mutableStateOf(false) }
    
    val savedStateHandle = navController.currentBackStackEntry?.savedStateHandle

    // Cargar item inicial
    LaunchedEffect(itemId) {
        isLoading = true
        val loadedItem = itemViewModel.getItemById(itemId)
        if (loadedItem != null) {
            // Verificar permisos: solo el propietario puede editar
            if (loadedItem.userId != (currentUser?.id ?: 0L)) {
                navController.navigateUp()
                return@LaunchedEffect
            }
            
            item = loadedItem
            title = loadedItem.title
            description = loadedItem.description
            category = loadedItem.category
            price = loadedItem.price.toString()
            location = if (loadedItem.latitude != 0.0 && loadedItem.longitude != 0.0) {
                LatLng(loadedItem.latitude, loadedItem.longitude)
            } else null
            imageUri = loadedItem.imageUrl?.let { Uri.parse(it) }
        } else {
            navController.navigateUp()
        }
        isLoading = false
    }

    // Recuperar ubicación seleccionada desde el mapa
    LaunchedEffect(savedStateHandle?.get<LatLng>("selected_location")) {
        val selected = savedStateHandle?.get<LatLng>("selected_location")
        if (selected != null) {
            location = selected
            savedStateHandle.remove<LatLng>("selected_location")
        }
    }

    // Galería
    val galleryLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        if (uri != null) {
            imageUri = uri
            hasImageChanged = true
        }
    }

    // Cámara
    fun createImageFile(context: Context): File {
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val storageDir: File? = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(
            "JPEG_${timeStamp}_",
            ".jpg",
            storageDir
        )
    }

    var cameraImageUri by remember { mutableStateOf<Uri?>(null) }
    val cameraLauncher = rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { success ->
        if (success) {
            imageUri = cameraImageUri
            hasImageChanged = true
        }
    }

    fun createImageUri(context: Context): Uri? {
        val contentResolver = context.contentResolver
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, "IMG_${System.currentTimeMillis()}.jpg")
            put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
            put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
        }
        return contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
    }

    val categories = listOf("Electrónicos", "Deportes", "Libros", "Otros")

    // Permiso de cámara
    val cameraPermissionLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
        if (granted) {
            val photoFile = createImageFile(context)
            val uri = FileProvider.getUriForFile(
                context,
                context.packageName + ".fileprovider",
                photoFile
            )
            cameraImageUri = uri
            cameraLauncher.launch(uri)
        }
    }

    // Permiso de ubicación
    val locationPermissionLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
        if (granted) {
            getCurrentLocationForEditItem(context) { currentLocation ->
                location = currentLocation
            }
        }
    }

    if (isLoading) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = androidx.compose.ui.Alignment.Center
        ) {
            CircularProgressIndicator()
        }
        return
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Editar Item") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Título") },
                modifier = Modifier.fillMaxWidth()
            )
            
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Descripción") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3
            )
            
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded },
            ) {
                OutlinedTextField(
                    value = category,
                    onValueChange = { },
                    readOnly = true,
                    label = { Text("Categoría") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor()
                )
                
                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false },
                ) {
                    categories.forEach { option ->
                        DropdownMenuItem(
                            text = { Text(option) },
                            onClick = {
                                category = option
                                expanded = false
                            }
                        )
                    }
                }
            }
            
            OutlinedTextField(
                value = price,
                onValueChange = { price = it },
                label = { Text("Precio") },
                modifier = Modifier.fillMaxWidth()
            )
            
            // Imagen seleccionada
            imageUri?.let {
                AsyncImage(
                    model = it,
                    contentDescription = "Imagen del item",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                )
            }
            
            // Botones de imagen
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = {
                        galleryLauncher.launch("image/*")
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.Add, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Galería")
                }
                OutlinedButton(
                    onClick = {
                        if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == android.content.pm.PackageManager.PERMISSION_GRANTED) {
                            val photoFile = createImageFile(context)
                            val uri = FileProvider.getUriForFile(
                                context,
                                context.packageName + ".fileprovider",
                                photoFile
                            )
                            cameraImageUri = uri
                            cameraLauncher.launch(uri)
                        } else {
                            cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
                        }
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.Add, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Cámara")
                }
            }
            
            // Botones de ubicación
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Botón para usar ubicación actual
                OutlinedButton(
                    onClick = {
                        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == android.content.pm.PackageManager.PERMISSION_GRANTED) {
                            getCurrentLocationForEditItem(context) { currentLocation ->
                                location = currentLocation
                            }
                        } else {
                            locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                        }
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.LocationOn, contentDescription = null)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Mi ubicación")
                }
                
                // Botón para elegir ubicación manualmente
                OutlinedButton(
                    onClick = {
                        navController.navigate(Screen.SelectLocation.route)
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.Place, contentDescription = null)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Elegir en mapa")
                }
            }
            
            location?.let {
                Text("Ubicación: ${String.format("%.6f", it.latitude)}, ${String.format("%.6f", it.longitude)}")
            }
            
            Spacer(modifier = Modifier.weight(1f))
            
            Button(
                onClick = {
                    item?.let { currentItem ->
                        val priceValue = price.toDoubleOrNull() ?: currentItem.price
                        val updatedItem = currentItem.copy(
                            title = title,
                            description = description,
                            category = category,
                            price = priceValue,
                            imageUrl = if (hasImageChanged) imageUri?.toString() else currentItem.imageUrl,
                            latitude = location?.latitude ?: currentItem.latitude,
                            longitude = location?.longitude ?: currentItem.longitude
                        )
                        itemViewModel.updateItem(updatedItem)
                        navController.navigateUp()
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = title.isNotBlank() && description.isNotBlank() && location != null
            ) {
                Icon(Icons.Default.Check, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Guardar Cambios")
            }
        }
    }
}

/**
 * Función para obtener la ubicación actual del usuario en EditItemScreen
 */
private fun getCurrentLocationForEditItem(context: android.content.Context, onLocationReceived: (LatLng) -> Unit) {
    if (androidx.core.content.ContextCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION) == android.content.pm.PackageManager.PERMISSION_GRANTED) {
        val fusedLocationClient = com.google.android.gms.location.LocationServices.getFusedLocationProviderClient(context)
        fusedLocationClient.lastLocation.addOnSuccessListener { currentLocation ->
            currentLocation?.let {
                onLocationReceived(LatLng(it.latitude, it.longitude))
            }
        }
    }
}

@Preview(showBackground = true, heightDp = 800)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditItemScreenPreview() {
    MaterialTheme {
        EditItemScreenContent(
            title = "iPhone 13 Pro",
            description = "Excelente estado, incluye cargador original",
            category = "Electrónicos",
            price = "850.00",
            imageUri = null,
            location = null,
            onTitleChange = {},
            onDescriptionChange = {},
            onCategoryChange = {},
            onPriceChange = {},
            onGalleryClick = {},
            onCameraClick = {},
            onCurrentLocationClick = {},
            onMapLocationClick = {},
            onSaveClick = {},
            onBackClick = {},
            categories = listOf("Electrónicos", "Deportes", "Libros", "Otros"),
            expanded = false,
            onExpandedChange = {},
            enabled = true
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EditItemScreenContent(
    title: String,
    description: String,
    category: String,
    price: String,
    imageUri: android.net.Uri?,
    location: com.google.android.gms.maps.model.LatLng?,
    onTitleChange: (String) -> Unit,
    onDescriptionChange: (String) -> Unit,
    onCategoryChange: (String) -> Unit,
    onPriceChange: (String) -> Unit,
    onGalleryClick: () -> Unit,
    onCameraClick: () -> Unit,
    onCurrentLocationClick: () -> Unit,
    onMapLocationClick: () -> Unit,
    onSaveClick: () -> Unit,
    onBackClick: () -> Unit,
    categories: List<String>,
    expanded: Boolean,
    onExpandedChange: (Boolean) -> Unit,
    enabled: Boolean
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Editar Item") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = title,
                onValueChange = onTitleChange,
                label = { Text("Título") },
                modifier = Modifier.fillMaxWidth()
            )
            
            OutlinedTextField(
                value = description,
                onValueChange = onDescriptionChange,
                label = { Text("Descripción") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3
            )
            
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = onExpandedChange,
            ) {
                OutlinedTextField(
                    value = category,
                    onValueChange = { },
                    readOnly = true,
                    label = { Text("Categoría") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor()
                )
                
                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { onExpandedChange(false) },
                ) {
                    categories.forEach { option ->
                        DropdownMenuItem(
                            text = { Text(option) },
                            onClick = {
                                onCategoryChange(option)
                                onExpandedChange(false)
                            }
                        )
                    }
                }
            }
            
            OutlinedTextField(
                value = price,
                onValueChange = onPriceChange,
                label = { Text("Precio") },
                modifier = Modifier.fillMaxWidth()
            )
            
            // Imagen seleccionada
            imageUri?.let {
                AsyncImage(
                    model = it,
                    contentDescription = "Imagen del item",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                )
            }
            
            // Botones de imagen
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = onGalleryClick,
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.Add, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Galería")
                }
                OutlinedButton(
                    onClick = onCameraClick,
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.Add, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Cámara")
                }
            }
            
            // Botones de ubicación
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Botón para usar ubicación actual
                OutlinedButton(
                    onClick = onCurrentLocationClick,
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.LocationOn, contentDescription = null)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Mi ubicación")
                }
                
                // Botón para elegir ubicación manualmente
                OutlinedButton(
                    onClick = onMapLocationClick,
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.Place, contentDescription = null)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Elegir en mapa")
                }
            }
            
            location?.let {
                Text("Ubicación: ${String.format("%.6f", it.latitude)}, ${String.format("%.6f", it.longitude)}")
            }
            
            Spacer(modifier = Modifier.weight(1f))
            
            Button(
                onClick = onSaveClick,
                modifier = Modifier.fillMaxWidth(),
                enabled = enabled
            ) {
                Icon(Icons.Default.Check, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Guardar Cambios")
            }
        }
    }
} 