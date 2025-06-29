# Marketplace de Items con Geolocalización

## 📖 Introducción

Esta aplicación Android es un **marketplace local** que permite a los usuarios comprar, vender e intercambiar productos con geolocalización. Los usuarios pueden registrarse, iniciar sesión, agregar productos con fotos y ubicación específica, y navegar por un catálogo filtrable por categorías.

La aplicación está diseñada como una solución completa de marketplace que combina funcionalidades de comercio electrónico con mapas interactivos, permitiendo a los usuarios visualizar la ubicación de los productos y seleccionar ubicaciones precisas al publicar sus propios items.

### 🎯 Objetivos principales
- Facilitar el comercio local entre usuarios
- Proporcionar una experiencia de usuario intuitiva y moderna
- Integrar funcionalidades de mapa para mejorar la experiencia de compra
- Mantener persistencia local de datos para uso offline

## 🚀 Instructivo paso a paso para levantar la aplicación

### Prerrequisitos
- Android Studio (versión recomendada: Android Studio Hedgehog | 2023.1.1 o superior)
- JDK 17 o superior
- Dispositivo Android o emulador con API level 24 o superior
- Conexión a internet para descargar dependencias

### Paso 1: Clonar el repositorio
```bash
git clone https://github.com/tuusuario/tu-repo.git
cd Adnroid
```

### Paso 2: Configurar Google Maps API Key
1. Ve a [Google Cloud Console](https://console.cloud.google.com/)
2. Crea un nuevo proyecto o selecciona uno existente
3. Habilita la API de Google Maps para Android
4. Crea credenciales (API Key)
5. Agrega la API Key en `app/src/main/res/values/google_maps_api.xml`:
   ```xml
   <string name="google_maps_key" templateMergeStrategy="preserve" translatable="false">TU_API_KEY_AQUI</string>
   ```

### Paso 3: Sincronizar dependencias
1. Abre el proyecto en Android Studio
2. Espera a que se sincronicen las dependencias de Gradle
3. Si hay errores, ejecuta `File > Sync Project with Gradle Files`

### Paso 4: Configurar el dispositivo/emulador
1. Conecta un dispositivo Android o inicia un emulador
2. Asegúrate de que el dispositivo tenga:
   - Google Play Services instalado
   - Permisos de cámara y ubicación habilitados
   - Conexión a internet

### Paso 5: Ejecutar la aplicación
1. Selecciona tu dispositivo/emulador en Android Studio
2. Presiona el botón "Run" (▶️) o usa `Shift + F10`
3. La aplicación se compilará e instalará automáticamente

### Paso 6: Configurar permisos (primera ejecución)
Al ejecutar la app por primera vez, concede los siguientes permisos:
- **Cámara**: Para tomar fotos de productos
- **Almacenamiento**: Para seleccionar imágenes de la galería
- **Ubicación**: Para mostrar y seleccionar ubicaciones en el mapa

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

## 🌍 Información relevante para la ejecución en distintos ambientes

### Ambiente de Desarrollo
- **Sistema Operativo**: Windows 10/11, macOS, Linux
- **IDE**: Android Studio Hedgehog | 2023.1.1
- **Gradle**: Versión 8.4
- **Android Gradle Plugin**: 8.1.4
- **Compile SDK**: 34
- **Target SDK**: 34
- **Minimum SDK**: 24 (Android 7.0)

### Ambiente de Testing
- **Dispositivos físicos**: Android 7.0 o superior
- **Emuladores**: API level 24-34
- **Google Play Services**: Versión 21.0.1 o superior
- **Memoria RAM**: Mínimo 2GB disponible

### Ambiente de Producción
- **Dispositivos soportados**: Android 7.0 (API 24) o superior
- **Orientación**: Portrait y Landscape
- **Densidades de pantalla**: mdpi, hdpi, xhdpi, xxhdpi, xxxhdpi
- **Tamaños de pantalla**: Normal, Large, XLarge

### Configuraciones específicas por ambiente

#### Desarrollo Local
```properties
# gradle.properties
org.gradle.jvmargs=-Xmx2048m -Dfile.encoding=UTF-8
android.useAndroidX=true
android.enableJetifier=true
```

#### Testing
```properties
# build.gradle.kts
android {
    testOptions {
        unitTests {
            isIncludeAndroidResources = true
        }
    }
}
```

#### Producción
```properties
# build.gradle.kts
android {
    buildTypes {
        release {
            isMinifyEnabled = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
}
```

### Variables de entorno y configuración
- **Google Maps API Key**: Requerida para funcionalidad de mapas
- **Debug vs Release**: Configuraciones separadas para desarrollo y producción
- **ProGuard**: Configurado para optimización en release

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

## 📸 Capturas de pantalla

> Agrega aquí tus capturas de pantalla mostrando la app en acción.

## 👨‍💻 Autor

Desarrollado como parte de un curso de Android, cumpliendo consignas de arquitectura, persistencia, navegación y uso de dispositivos.

---

¡Listo para usar y mejorar! Siéntete libre de hacer forks, pull requests o sugerencias. 