
## 🚀 Instructivo paso a paso para levantar la aplicación

### Prerrequisitos
- Android Studio (versión recomendada: Android Studio Hedgehog | 2023.1.1 o superior)
- JDK 17 o superior
- Dispositivo Android o emulador con API level 24 o superior
- Conexión a internet para descargar dependencias

## 🛠️ Tecnologías utilizadas

### Lenguajes y Frameworks
- **Kotlin 1.9.0**: Lenguaje principal de desarrollo
- **Jetpack Compose 1.5.4**: Framework de UI declarativa
- **Material Design 3**: Sistema de diseño y componentes

### Arquitectura y Patrones
- **MVVM (Model-View-ViewModel)**: Patrón arquitectónico
- **Repository Pattern**: Para acceso a datos
- **Clean Architecture**: Separación de responsabilidades

### Base de Datos y Persistencia
- **Room Database 2.6.1**: Base de datos local SQLite
- **Room Kotlin Extensions**: Para corrutinas y Flow
- **Room Compiler**: Para generación de código

### Navegación
- **Navigation Compose 2.7.7**: Navegación entre pantallas
- **Navigation Testing**: Para testing de navegación

### Mapas y Ubicación
- **Google Maps Compose 4.3.0**: Componentes de mapa para Compose
- **Google Play Services Maps 18.2.0**: Servicios de Google Maps
- **Google Play Services Location 21.0.1**: Servicios de ubicación

### Imágenes y Multimedia
- **Coil 2.5.0**: Carga y cacheo de imágenes
- **CameraX 1.3.2**: Funcionalidad de cámara
- **Accompanist Permissions 0.32.0**: Gestión de permisos

### Utilidades
- **Kotlin Coroutines 1.7.3**: Programación asíncrona
- **Kotlin Flow**: Flujos reactivos
- **Lifecycle Components**: Gestión del ciclo de vida

### Testing
- **JUnit 4**: Testing unitario
- **Espresso**: Testing de UI
- **Mockito**: Mocking para tests

### Variables de entorno y configuración
- **Google Maps API Key**: Requerida para funcionalidad de mapas

### Dependencias externas
- **Google Play Services**: Requerido para mapas y ubicación
- **Internet**: Necesario para cargar mapas y servicios de Google
- **Almacenamiento**: Para guardar imágenes y base de datos local

### Troubleshooting común
1. **Error de API Key**: Verificar que la clave de Google Maps sea válida
2. **Permisos denegados**: Asegurar que todos los permisos estén concedidos
3. **Mapa no carga**: Verificar conexión a internet y Google Play Services
4. **Imágenes no se cargan**: Verificar permisos de almacenamiento

## 📱 Funcionalidades destacadas

- **Registro e inicio de sesión** con validación básica
- **Agregar items** con título, descripción, precio, categoría, imagen y ubicación
- **Lista de items** filtrable por categoría
- **Detalle de item** con imagen, descripción, precio y ubicación
- **Ver ubicación en mapa** de cada item
- **Persistencia local** con Room (los datos no se pierden al cerrar la app)
- **UI moderna** con Material Design 3 y Jetpack Compose

## 🔒 Permisos requeridos

- `CAMERA`: Para tomar fotos
- `READ_EXTERNAL_STORAGE` / `READ_MEDIA_IMAGES`: Para seleccionar imágenes de la galería
- `ACCESS_FINE_LOCATION`: Para seleccionar y mostrar ubicaciones en el mapa
- `INTERNET`: Para Google Maps

Desarrollado como parte de un curso de Android, cumpliendo consignas de arquitectura, persistencia, navegación y uso de dispositivos.

