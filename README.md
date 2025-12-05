# Proyecto FullSound
Este proyecto es una tienda de Beats donde puedes comprar y vender beats.

## Rutas principales del proyecto

| Componente         | Ruta principal                                                                 |
|--------------------|-------------------------------------------------------------------------------|
| MainActivity       | app/src/main/java/com/grupo8/fullsound/MainActivity.kt                        |
| Login              | app/src/main/java/com/grupo8/fullsound/ui/auth/login/LoginFragment.kt         |
| Registro           | app/src/main/java/com/grupo8/fullsound/ui/auth/register/RegisterFragment.kt   |
| Beats (CRUD)       | app/src/main/java/com/grupo8/fullsound/ui/beats/BeatsFragment.kt              |
| Lista de Beats     | app/src/main/java/com/grupo8/fullsound/ui/beats/BeatsListaFragment.kt         |
| Carrito            | app/src/main/java/com/grupo8/fullsound/ui/Carrito/CarritoFragment.kt          |
| ViewModels         | app/src/main/java/com/grupo8/fullsound/viewmodel/                             |
| Repositorios       | app/src/main/java/com/grupo8/fullsound/repository/                            |
| Modelos            | app/src/main/java/com/grupo8/fullsound/model/                                 |
| Base de datos      | app/src/main/java/com/grupo8/fullsound/data/local/                            |
| Layouts XML        | app/src/main/res/layout/                                                      |
| Navegación         | app/src/main/res/navigation/nav_graph.xml                                     |
| Documentación      | Documentacion/                                                                |

## Arquitectura
- MVVM (Model-View-ViewModel)
- Supabase (Base de datos remota)
- Room Database (Caché local)
- Navigation Component
- Material Design 3
- Retrofit + OkHttp (API Fixer para conversión de monedas)

## Compilación

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

# Documentación

- En carpeta [Documentacion](/Documentacion)

