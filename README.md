# 🛍️ Android Marketplace App

Una aplicación completa de marketplace desarrollada en **Kotlin** con **Jetpack Compose**, que permite a los usuarios comprar y vender items con funcionalidades avanzadas de ubicación, gestión de usuarios y navegación intuitiva.

## ✨ Características Principales

### 👤 **Sistema de Usuarios**
- 🔐 **Registro e inicio de sesión** con validación completa
- 👨‍💼 **Gestión de perfiles** con edición de información personal
- 🔒 **Control de permisos** - solo el creador puede editar/eliminar sus items
- 📋 **"Mis Publicaciones"** - pantalla dedicada para gestionar tus items

### 🛒 **Marketplace**
- 📱 **Lista de items** con diseño moderno tipo tarjetas
- 🔍 **Búsqueda en tiempo real** por título/descripción
- 📂 **Filtros por categoría** (Electrónicos, Deportes, Libros, Otros)
- 💰 **Visualización de precios** y detalles completos
- 📸 **Imágenes** desde cámara o galería

### 📍 **Funcionalidades de Ubicación**
- 🗺️ **Mapas integrados** con Google Maps
- 📍 **Doble opción de ubicación**:
  - **"Mi ubicación"**: Usa tu ubicación actual (GPS)
  - **"Elegir en mapa"**: Selección manual precisa
- 🎯 **Botón de centrado** en mapas para ir a tu ubicación
- 📏 **Items cercanos** - muestra items dentro de 100km
- 🔵 **Marcadores diferenciados** - azul para tu ubicación, rojo para items

### 🎨 **Experiencia de Usuario**
- 🌟 **Material Design 3** con interfaz moderna
- 📱 **Navegación por pestañas** (Inicio, Mapa, Perfil)
- 🔄 **Estados de carga** y feedback visual
- 📱 **Responsive design** adaptado a diferentes pantallas
- 🎯 **Previews de Compose** para desarrollo rápido

### 💾 **Persistencia de Datos**
- 🗄️ **Base de datos local** con Room
- 🔄 **Datos sincronizados** entre pantallas
- 💾 **Sin pérdida de datos** al cerrar la app
- 🚀 **Rendimiento optimizado** con Flow y StateFlow

## 🛠️ Tecnologías y Arquitectura

### **Stack Tecnológico**
- **Kotlin 1.9.0** - Lenguaje principal
- **Jetpack Compose 1.5.4** - UI moderna y declarativa
- **Material Design 3** - Sistema de diseño
- **Google Maps Compose** - Integración de mapas
- **Room Database** - Base de datos local
- **CameraX** - Funcionalidad de cámara avanzada
- **Coil** - Carga eficiente de imágenes

### **Arquitectura**
- **MVVM (Model-View-ViewModel)** - Patrón arquitectónico
- **Repository Pattern** - Acceso a datos centralizado
- **Clean Architecture** - Separación de responsabilidades
- **Dependency Injection** - Gestión de dependencias
- **StateFlow & Flow** - Manejo reactivo de estados



## 🚀 Instalación y Configuración

### **Prerrequisitos**
- Android Studio Hedgehog (2023.1.1+)
- JDK 17 o superior
- Google Maps API Key
- Dispositivo/Emulador Android API 24+

## 📱 Funcionalidades Detalladas

### **🏠 Pantalla Principal (Home)**
- Lista scrolleable de todos los items disponibles
- Barra de búsqueda con filtrado en tiempo real
- Filtros por categoría con chips visuales
- Botón de agregar item en la barra superior
- Navegación directa a detalles de item

### **➕ Agregar/Editar Items**
- Formulario completo con validación
- **Imagen**: 
  - 📷 Captura desde cámara
  - 🖼️ Selección desde galería
- **Ubicación**:
  - 📍 "Mi ubicación" (GPS automático)
  - 🗺️ "Elegir en mapa" (selección manual)
- Categorías predefinidas con dropdown
- Validación de campos obligatorios

### **🗺️ Mapas**
- **Vista de items cercanos**: Muestra todos los items en un radio de 100km
- **Marcadores interactivos**: Tap para ver información del item
- **Navegación a detalles**: Desde ventana de información del marcador
- **Botón de ubicación**: Centra el mapa en tu posición actual
- **Animaciones suaves**: Transiciones fluidas de cámara

### **👤 Perfil de Usuario**
- Información personal editable
- Acceso a "Mis Publicaciones"
- Estadísticas de items publicados
- Opción de cerrar sesión

### **📋 Mis Publicaciones**
- Lista personal de items creados
- Acceso directo a edición
- Permisos de eliminación
- Estado vacío con mensaje informativo


**Desarrollado como proyecto educativo** cumpliendo consignas de:
- ✅ Arquitectura MVVM
- ✅ Persistencia con Room
- ✅ Navegación entre pantallas
- ✅ Uso de dispositivos (cámara, GPS)
- ✅ Material Design
- ✅ Jetpack Compose


**¡Gracias por usar Android Marketplace App!** 🚀

