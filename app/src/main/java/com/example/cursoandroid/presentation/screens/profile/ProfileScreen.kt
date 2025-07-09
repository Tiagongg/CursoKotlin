package com.example.cursoandroid.presentation.screens.profile

import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.cursoandroid.presentation.components.BottomNavigationBar
import com.example.cursoandroid.presentation.components.NavigationItem
import com.example.cursoandroid.presentation.navigation.Screen
import com.example.cursoandroid.presentation.viewmodel.ProfileState
import com.example.cursoandroid.presentation.viewmodel.ProfileViewModel
import com.example.cursoandroid.data.model.User
import androidx.compose.ui.tooling.preview.Preview
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
fun ProfileScreen(
    navController: NavController,
    profileViewModel: ProfileViewModel,
    currentUser: User?
) {
    val context = LocalContext.current
    
    var name by rememberSaveable { mutableStateOf("") }
    var lastName by rememberSaveable { mutableStateOf("") }
    var age by rememberSaveable { mutableStateOf("") }
    var profileImageUri by rememberSaveable { mutableStateOf<Uri?>(null) }
    var isEditing by rememberSaveable { mutableStateOf(false) }
    var showImageDialog by rememberSaveable { mutableStateOf(false) }
    var tempCameraUri by rememberSaveable { mutableStateOf<Uri?>(null) }

    val profileState by profileViewModel.profileState.collectAsState()
    val userProfile by profileViewModel.currentUserProfile.collectAsState()

    // Permisos de cámara
    val cameraPermissionState = rememberPermissionState(
        android.Manifest.permission.CAMERA
    )

    // Función para crear URI temporal para la cámara
    fun createImageUri(context: Context): Uri {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val imageFileName = "JPEG_${timeStamp}_profile"
        val storageDir = File(context.getExternalFilesDir(null), "Pictures")
        if (!storageDir.exists()) {
            storageDir.mkdirs()
        }
        val imageFile = File(storageDir, "$imageFileName.jpg")
        return FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            imageFile
        )
    }

    // Launcher para tomar foto con cámara
    val cameraLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { success ->
        if (success && tempCameraUri != null) {
            profileImageUri = tempCameraUri
        }
        tempCameraUri = null
    }

    // Launcher para seleccionar imagen de galería
    val galleryLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) {
            profileImageUri = uri
        }
    }

    // Función para manejar la selección de imagen
    fun handleImageSelection(useCamera: Boolean) {
        if (useCamera) {
            if (cameraPermissionState.status.isGranted) {
                tempCameraUri = createImageUri(context)
                tempCameraUri?.let { uri ->
                    cameraLauncher.launch(uri)
                }
            } else {
                cameraPermissionState.launchPermissionRequest()
            }
        } else {
            galleryLauncher.launch("image/*")
        }
        showImageDialog = false
    }

    // Cargar datos del usuario al inicializar
    LaunchedEffect(currentUser?.id) {
        currentUser?.id?.let { userId ->
            profileViewModel.loadUserProfile(userId)
        }
    }

    // Actualizar campos cuando se carga el perfil
    LaunchedEffect(userProfile) {
        userProfile?.let { user ->
            name = user.name
            lastName = user.lastName
            age = if (user.age > 0) user.age.toString() else ""
            // Si hay una imagen de perfil guardada, usar esa URL
            if (user.profileImageUrl != null && profileImageUri == null) {
                try {
                    profileImageUri = Uri.parse(user.profileImageUrl)
                } catch (e: Exception) {
                    // Si no se puede parsear la URI, mantener null
                }
            }
        }
    }

    // Manejar éxito de actualización
    LaunchedEffect(profileState) {
        if (profileState is ProfileState.UpdateSuccess) {
            isEditing = false
            profileViewModel.clearSuccess()
        }
    }

    // Diálogo de selección de imagen
    if (showImageDialog) {
        AlertDialog(
            onDismissRequest = { showImageDialog = false },
            title = { Text("Seleccionar foto de perfil") },
            text = { Text("¿Cómo quieres agregar tu foto de perfil?") },
            confirmButton = {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    TextButton(
                        onClick = { handleImageSelection(true) }
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Cámara")
                    }
                    TextButton(
                        onClick = { handleImageSelection(false) }
                    ) {
                        Icon(Icons.Default.Search, contentDescription = null)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Galería")
                    }
                }
            },
            dismissButton = {
                TextButton(onClick = { showImageDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mi Perfil") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                },
                actions = {
                    if (!isEditing) {
                        IconButton(onClick = { isEditing = true }) {
                            Icon(Icons.Default.Edit, contentDescription = "Editar")
                        }
                    }
                }
            )
        },
        bottomBar = {
            BottomNavigationBar(
                navController = navController,
                currentItem = NavigationItem.PROFILE
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Foto de perfil
            Card(
                modifier = Modifier.size(120.dp),
                shape = CircleShape
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    if (profileImageUri != null) {
                        AsyncImage(
                            model = profileImageUri,
                            contentDescription = "Foto de perfil",
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(CircleShape),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Icon(
                            Icons.Default.Person,
                            contentDescription = "Sin foto",
                            modifier = Modifier.size(60.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    
                    if (isEditing) {
                        Surface(
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(CircleShape),
                            color = MaterialTheme.colorScheme.surface.copy(alpha = 0.7f)
                        ) {
                            IconButton(
                                onClick = { showImageDialog = true },
                                modifier = Modifier.fillMaxSize()
                            ) {
                                Icon(
                                    Icons.Default.Edit,
                                    contentDescription = "Cambiar foto",
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }
                }
            }

            if (isEditing) {
                Text(
                    "Toca la imagen para cambiarla",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Información del usuario (solo lectura)
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Información de la cuenta",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Email: ${userProfile?.email ?: ""}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        text = "ID de usuario: ${userProfile?.id ?: ""}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Campo Nombre
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Nombre") },
                modifier = Modifier.fillMaxWidth(),
                enabled = isEditing,
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Next
                ),
                leadingIcon = {
                    Icon(Icons.Default.Person, contentDescription = null)
                },
                singleLine = true
            )

            // Campo Apellido
            OutlinedTextField(
                value = lastName,
                onValueChange = { lastName = it },
                label = { Text("Apellido") },
                modifier = Modifier.fillMaxWidth(),
                enabled = isEditing,
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Next
                ),
                leadingIcon = {
                    Icon(Icons.Default.Person, contentDescription = null)
                },
                singleLine = true
            )

            // Campo Edad
            OutlinedTextField(
                value = age,
                onValueChange = { newValue ->
                    // Solo permitir números
                    if (newValue.isEmpty() || newValue.all { it.isDigit() }) {
                        age = newValue
                    }
                },
                label = { Text("Edad") },
                modifier = Modifier.fillMaxWidth(),
                enabled = isEditing,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Done
                ),
                leadingIcon = {
                    Icon(Icons.Default.DateRange, contentDescription = null)
                },
                singleLine = true
            )

            // Mostrar errores
            val errorState = profileState
            if (errorState is ProfileState.Error) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.Warning,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onErrorContainer
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = errorState.message,
                            color = MaterialTheme.colorScheme.onErrorContainer,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }

            // Botón de Mis Publicaciones (solo visible cuando no está editando)
            if (!isEditing) {
                OutlinedButton(
                    onClick = { navController.navigate(Screen.MyItems.route) },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.List, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Mis Publicaciones")
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // Botones de acción
            if (isEditing) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = {
                            isEditing = false
                            // Restaurar valores originales
                            userProfile?.let { user ->
                                name = user.name
                                lastName = user.lastName
                                age = if (user.age > 0) user.age.toString() else ""
                            }
                            profileViewModel.clearError()
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.Close, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Cancelar")
                    }

                    Button(
                        onClick = {
                            currentUser?.id?.let { userId ->
                                val ageInt = age.toIntOrNull() ?: 0
                                profileViewModel.updateProfile(
                                    userId = userId,
                                    name = name,
                                    lastName = lastName,
                                    age = ageInt,
                                    profileImageUrl = profileImageUri?.toString()
                                )
                            }
                        },
                        modifier = Modifier.weight(1f),
                        enabled = profileState !is ProfileState.Loading && 
                                 name.isNotBlank() && lastName.isNotBlank()
                    ) {
                        if (profileState is ProfileState.Loading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(16.dp),
                                strokeWidth = 2.dp
                            )
                        } else {
                            Icon(Icons.Default.Check, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Guardar")
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true, heightDp = 800)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreenViewModePreview() {
    MaterialTheme {
        ProfileScreenContent(
            name = "Juan",
            lastName = "Pérez",
            age = "25",
            email = "juan.perez@email.com",
            userId = "123",
            profileImageUri = null,
            isEditing = false,
            profileState = ProfileState.Initial,
            onNameChange = {},
            onLastNameChange = {},
            onAgeChange = {},
            onProfileImageClick = {},
            onEditClick = {},
            onCancelClick = {},
            onSaveClick = {},
            onMyItemsClick = {},
            onBackClick = {}
        )
    }
}

@Preview(showBackground = true, heightDp = 800)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreenEditModePreview() {
    MaterialTheme {
        ProfileScreenContent(
            name = "Juan",
            lastName = "Pérez",
            age = "25",
            email = "juan.perez@email.com",
            userId = "123",
            profileImageUri = null,
            isEditing = true,
            profileState = ProfileState.Initial,
            onNameChange = {},
            onLastNameChange = {},
            onAgeChange = {},
            onProfileImageClick = {},
            onEditClick = {},
            onCancelClick = {},
            onSaveClick = {},
            onMyItemsClick = {},
            onBackClick = {}
        )
    }
}

@Preview(showBackground = true, heightDp = 800)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreenLoadingPreview() {
    MaterialTheme {
        ProfileScreenContent(
            name = "Juan",
            lastName = "Pérez",
            age = "25",
            email = "juan.perez@email.com",
            userId = "123",
            profileImageUri = null,
            isEditing = true,
            profileState = ProfileState.Loading,
            onNameChange = {},
            onLastNameChange = {},
            onAgeChange = {},
            onProfileImageClick = {},
            onEditClick = {},
            onCancelClick = {},
            onSaveClick = {},
            onMyItemsClick = {},
            onBackClick = {}
        )
    }
}

@Preview(showBackground = true, heightDp = 800)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreenErrorPreview() {
    MaterialTheme {
        ProfileScreenContent(
            name = "Juan",
            lastName = "",
            age = "25",
            email = "juan.perez@email.com",
            userId = "123",
            profileImageUri = null,
            isEditing = true,
            profileState = ProfileState.Error("Por favor completa todos los campos obligatorios"),
            onNameChange = {},
            onLastNameChange = {},
            onAgeChange = {},
            onProfileImageClick = {},
            onEditClick = {},
            onCancelClick = {},
            onSaveClick = {},
            onMyItemsClick = {},
            onBackClick = {}
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ProfileScreenContent(
    name: String,
    lastName: String,
    age: String,
    email: String,
    userId: String,
    profileImageUri: Uri?,
    isEditing: Boolean,
    profileState: ProfileState,
    onNameChange: (String) -> Unit,
    onLastNameChange: (String) -> Unit,
    onAgeChange: (String) -> Unit,
    onProfileImageClick: () -> Unit,
    onEditClick: () -> Unit,
    onCancelClick: () -> Unit,
    onSaveClick: () -> Unit,
    onMyItemsClick: () -> Unit,
    onBackClick: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mi Perfil") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                },
                actions = {
                    if (!isEditing) {
                        IconButton(onClick = onEditClick) {
                            Icon(Icons.Default.Edit, contentDescription = "Editar")
                        }
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Imagen de perfil
            Box(
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                Card(
                    modifier = Modifier.size(120.dp),
                    shape = CircleShape,
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    if (profileImageUri != null) {
                        AsyncImage(
                            model = profileImageUri,
                            contentDescription = "Foto de perfil",
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(CircleShape),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.Person,
                                contentDescription = "Sin foto de perfil",
                                modifier = Modifier.size(60.dp),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                if (isEditing) {
                    Card(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .size(36.dp),
                        shape = CircleShape,
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer
                        )
                    ) {
                        IconButton(
                            onClick = onProfileImageClick,
                            modifier = Modifier.fillMaxSize()
                        ) {
                            Icon(
                                Icons.Default.Add,
                                contentDescription = "Cambiar foto",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            }

            if (isEditing) {
                Text(
                    "Toca la imagen para cambiarla",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Información del usuario (solo lectura)
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Información de la cuenta",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Email: $email",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        text = "ID de usuario: $userId",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Campo Nombre
            OutlinedTextField(
                value = name,
                onValueChange = onNameChange,
                label = { Text("Nombre") },
                modifier = Modifier.fillMaxWidth(),
                enabled = isEditing,
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Next
                ),
                leadingIcon = {
                    Icon(Icons.Default.Person, contentDescription = null)
                },
                singleLine = true
            )

            // Campo Apellido
            OutlinedTextField(
                value = lastName,
                onValueChange = onLastNameChange,
                label = { Text("Apellido") },
                modifier = Modifier.fillMaxWidth(),
                enabled = isEditing,
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Next
                ),
                leadingIcon = {
                    Icon(Icons.Default.Person, contentDescription = null)
                },
                singleLine = true
            )

            // Campo Edad
            OutlinedTextField(
                value = age,
                onValueChange = { newValue ->
                    // Solo permitir números
                    if (newValue.isEmpty() || newValue.all { it.isDigit() }) {
                        onAgeChange(newValue)
                    }
                },
                label = { Text("Edad") },
                modifier = Modifier.fillMaxWidth(),
                enabled = isEditing,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Done
                ),
                leadingIcon = {
                    Icon(Icons.Default.DateRange, contentDescription = null)
                },
                singleLine = true
            )

            // Mostrar errores
            if (profileState is ProfileState.Error) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.Warning,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onErrorContainer
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = profileState.message,
                            color = MaterialTheme.colorScheme.onErrorContainer,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }

            // Botón de Mis Publicaciones (solo visible cuando no está editando)
            if (!isEditing) {
                OutlinedButton(
                    onClick = onMyItemsClick,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.List, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Mis Publicaciones")
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // Botones de acción
            if (isEditing) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = onCancelClick,
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.Close, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Cancelar")
                    }

                    Button(
                        onClick = onSaveClick,
                        modifier = Modifier.weight(1f),
                        enabled = profileState !is ProfileState.Loading && 
                                 name.isNotBlank() && lastName.isNotBlank()
                    ) {
                        if (profileState is ProfileState.Loading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(16.dp),
                                strokeWidth = 2.dp
                            )
                        } else {
                            Icon(Icons.Default.Check, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Guardar")
                        }
                    }
                }
            }
        }
    }
} 