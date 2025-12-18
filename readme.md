![Kotlin](https://img.shields.io/badge/kotlin-%237F52FF.svg?style=for-the-badge&logo=kotlin&logoColor=white)
![Android](https://img.shields.io/badge/Android-3DDC84?style=for-the-badge&logo=android&logoColor=white)
![License](https://img.shields.io/badge/license-MIT-blue.svg?style=for-the-badge)

# FullSound

FullSound es una aplicación Android desarrollada en Kotlin que funciona como una tienda de beats musicales. Permite a los usuarios comprar y vender instrumentales, gestionar un carrito de compras y administrar su perfil.

## Arquitectura
- MVVM (Model-View-ViewModel)
- Supabase (Base de datos remota)
- Room Database (Caché local)
- Navigation Component
- Material Design 3
- Retrofit + OkHttp (API Fixer para conversión de monedas)

- Sistema de autenticación de usuarios (Login y Registro).
- Gestión completa de Beats (Crear, Leer, Actualizar, Eliminar).
- Carrito de compras funcional.
- Base de datos en la nube mediante Supabase.
- Interfaz de usuario moderna basada en Material Design 3.
- Navegación fluida utilizando Navigation Component.

## Arquitectura y Tecnologías

El proyecto sigue el patrón de arquitectura MVVM (Model-View-ViewModel) para asegurar una separación clara de responsabilidades y facilitar el mantenimiento.

- **Lenguaje**: Kotlin
- **Arquitectura**: MVVM
- **Base de Datos**: Supabase
- **UI**: XML con ViewBinding y componentes de Jetpack Compose
- **Navegación**: Navigation Component

## Estructura del Proyecto

- **app/src/main/java/com/grupo8/fullsound/ui**: Contiene los fragmentos y actividades de la interfaz de usuario.
- **app/src/main/java/com/grupo8/fullsound/viewmodel**: Contiene la lógica de negocio y gestión de estado.
- **app/src/main/java/com/grupo8/fullsound/data**: Capa de datos, incluyendo la integración con Supabase.
- **app/src/main/java/com/grupo8/fullsound/model**: Definiciones de las entidades de datos.

## Instalación y Compilación

Para compilar y ejecutar el proyecto, utilice el siguiente comando en la terminal:

```bash
# Compilar la aplicación
./gradlew.bat assembleDebug

# Generar APK firmado
./gradlew.bat assembleRelease
```

## Tests

```bash
# Ejecutar todos los tests unitarios
./gradlew.bat test

# Ejecutar tests instrumentados (requiere dispositivo/emulador)
./gradlew.bat connectedAndroidTest

# Ejecutar tests específicos
./gradlew.bat test --tests "com.grupo8.fullsound.model.*"
./gradlew.bat test --tests "com.grupo8.fullsound.viewmodel.*"
```

### Tests Disponibles
- ✅ Tests de Modelos (Beat, User, CarritoItem) - 20 tests
- ✅ Tests de ViewModels (Carrito, Login) - 4 tests  
- ✅ Tests de Utilidades (FormValidator) - 11 tests
- ✅ Tests de API (ExchangeRateProvider) - 2 tests
- ✅ Tests Instrumentados (UI/Recursos) - 8 tests

## Funcionalidades Implementadas
- ✅ Sistema de autenticación con Supabase (Login/Registro)
- ✅ Roles de usuario (User/Admin) - Los correos @admin.cl son admin
- ✅ CRUD completo de Beats conectado a Supabase
- ✅ Catálogo de Beats desde base de datos remota
- ✅ Imágenes y audios desde Supabase Storage (buckets)
- ✅ Conversión de monedas CLP ↔ USD con API de Fixer
- ✅ Carrito de compras con persistencia local
- ✅ Reglas de negocio (No se puede agregar más de un beat duplicado)
- ✅ Persistencia híbrida: Supabase (remoto) + Room (caché local)
- ✅ Navegación con Navigation Component
- ✅ Animaciones personalizadas
- ✅ Material Design 3
- ✅ Arquitectura MVVM
- ✅ Tests unitarios e instrumentados (35+ tests)

## Configuración de Variables de Entorno

El proyecto usa un archivo `local.properties` para las credenciales sensibles:

```properties
# Supabase Configuration
SUPABASE_URL=your_supabase_url
SUPABASE_ANON_KEY=your_supabase_anon_key

# Fixer API (Conversión de monedas)
FIXER_API_KEY=your_fixer_api_key
```

## Estructura de Base de Datos (Supabase)

### Tabla: beat
- `id_beat` (PK, auto-increment)
- `titulo`, `slug`, `artista`
- `precio` (CLP), `bpm`, `tonalidad`, `duracion`
- `genero`, `etiquetas`, `descripcion`
- `imagen_url`, `audio_url`, `audio_demo_url`
- `reproducciones`, `estado`
- `created_at`, `updated_at`

### Tabla: usuario
- `id_usuario` (PK, auto-increment)
- `nombre_usuario`, `correo`, `contraseña`
- `nombre`, `apellido`
- `id_rol` (FK a tipo_usuario)
- `activo`, `created_at`, `updated_at`

### Buckets de Storage
- `beats-images`: Imágenes de beats
- `beats-audio`: Archivos de audio de beats