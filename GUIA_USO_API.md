# Guía de Uso - API Repositories

## 📚 Cómo Usar los Nuevos Repositorios API

Esta guía explica cómo usar los repositorios API creados para la migración del backend.

---

## 🚀 Inicialización

### 1. Configurar Application Class

Agregar en `AndroidManifest.xml`:
```xml
<application
    android:name=".FullSoundApplication"
    ...>
```

Ya está creado el archivo `FullSoundApplication.kt` que inicializa RetrofitClient.

---

## 🔐 Autenticación

### ApiAuthRepository

```kotlin
// En tu Activity o ViewModel
val authRepository = ApiAuthRepository(context)

// LOGIN
lifecycleScope.launch {
    val result = authRepository.login("usuario@email.com", "password123")
    when (result) {
        is Resource.Success -> {
            val authResponse = result.data!!
            val token = authResponse.token
            val usuario = authResponse.usuario
            Log.d("Auth", "Login exitoso: ${usuario.nombreUsuario}")
            // Navegar a la pantalla principal
        }
        is Resource.Error -> {
            Log.e("Auth", "Error: ${result.message}")
            // Mostrar mensaje de error
        }
        is Resource.Loading -> {
            // Mostrar loading
        }
    }
}

// REGISTRO
lifecycleScope.launch {
    val result = authRepository.register(
        username = "usuario123",
        email = "usuario@email.com",
        password = "password123",
        nombre = "Juan",
        apellido = "Pérez"
    )
    when (result) {
        is Resource.Success -> {
            Log.d("Auth", result.data!!.message)
            // Registro exitoso, ahora hacer login
        }
        is Resource.Error -> {
            Log.e("Auth", result.message ?: "Error desconocido")
        }
    }
}

// VERIFICAR SI ESTÁ LOGUEADO
if (authRepository.isLoggedIn()) {
    Log.d("Auth", "Usuario autenticado")
    val userData = authRepository.getCurrentUserData()
    val userId = userData["userId"] as Int
    val username = userData["username"] as String?
    val isAdmin = userData["isAdmin"] as Boolean
}

// LOGOUT
authRepository.logout()
```

---

## 🎵 Beats

### ApiBeatRepository

```kotlin
val beatRepository = ApiBeatRepository(context)

// OBTENER TODOS LOS BEATS
lifecycleScope.launch {
    val result = beatRepository.getAllBeats()
    when (result) {
        is Resource.Success -> {
            val beats = result.data!!
            Log.d("Beats", "${beats.size} beats cargados")
            // Actualizar RecyclerView
            beatAdapter.submitList(beats)
        }
        is Resource.Error -> {
            Log.e("Beats", result.message ?: "Error")
        }
    }
}

// BUSCAR BEATS
lifecycleScope.launch {
    val result = beatRepository.searchBeats("trap")
    when (result) {
        is Resource.Success -> {
            val beats = result.data!!
            // Mostrar resultados de búsqueda
        }
        is Resource.Error -> {
            // Manejar error
        }
    }
}

// OBTENER BEATS DESTACADOS
lifecycleScope.launch {
    val result = beatRepository.getFeaturedBeats(limit = 5)
    when (result) {
        is Resource.Success -> {
            val featuredBeats = result.data!!
            // Mostrar en carousel o sección destacada
        }
        is Resource.Error -> {
            // Manejar error
        }
    }
}

// CREAR BEAT (solo ADMIN)
lifecycleScope.launch {
    val newBeat = BeatRequestDto(
        titulo = "Beat Trap Oscuro",
        artista = "Producer X",
        precio = 15000, // Precio en CLP (15.000 pesos chilenos)
        bpm = 140,
        tonalidad = "Cm",
        duracion = 180,
        genero = "Trap",
        descripcion = "Beat oscuro ideal para trap",
        imagenUrl = "https://example.com/imagen.jpg",
        audioUrl = "https://example.com/audio.mp3"
    )
    
    val result = beatRepository.createBeat(newBeat)
    when (result) {
        is Resource.Success -> {
            val createdBeat = result.data!!
            Log.d("Beats", "Beat creado con ID: ${createdBeat.id}")
        }
        is Resource.Error -> {
            Log.e("Beats", result.message ?: "Error al crear")
        }
    }
}
```

---

## 🛒 Pedidos

### ApiPedidoRepository

```kotlin
val pedidoRepository = ApiPedidoRepository(context)

// CREAR PEDIDO
lifecycleScope.launch {
    val items = listOf(
        PedidoItemRequestDto(
            idBeat = 1,
            cantidad = 1,
            precioUnitario = 15000 // CLP $15.000
        ),
        PedidoItemRequestDto(
            idBeat = 2,
            cantidad = 1,
            precioUnitario = 20000 // CLP $20.000
        )
    )
    
    val pedidoRequest = PedidoRequestDto(
        items = items,
        total = 35000, // CLP $35.000 (suma de todos los items)
        metodoPago = "STRIPE"
    )
    
    val result = pedidoRepository.createPedido(pedidoRequest)
    when (result) {
        is Resource.Success -> {
            val pedido = result.data!!
            Log.d("Pedido", "Pedido creado: ${pedido.numeroPedido}")
            // Proceder al pago
        }
        is Resource.Error -> {
            Log.e("Pedido", result.message ?: "Error")
        }
    }
}

// MIS PEDIDOS
lifecycleScope.launch {
    val result = pedidoRepository.getMisPedidos()
    when (result) {
        is Resource.Success -> {
            val pedidos = result.data!!
            Log.d("Pedidos", "${pedidos.size} pedidos encontrados")
            // Mostrar lista de pedidos
            pedidos.forEach { pedido ->
                Log.d("Pedidos", "Pedido ${pedido.numeroPedido}: ${pedido.estado}")
            }
        }
        is Resource.Error -> {
            Log.e("Pedidos", result.message ?: "Error")
        }
    }
}

// ACTUALIZAR ESTADO (solo ADMIN)
lifecycleScope.launch {
    val result = pedidoRepository.updateEstadoPedido(
        id = 1,
        nuevoEstado = "COMPLETADO"
    )
    when (result) {
        is Resource.Success -> {
            val pedido = result.data!!
            Log.d("Pedidos", "Estado actualizado: ${pedido.estado}")
        }
        is Resource.Error -> {
            Log.e("Pedidos", result.message ?: "Error")
        }
    }
}
```

---

## 👤 Usuarios

### ApiUsuarioRepository

```kotlin
val usuarioRepository = ApiUsuarioRepository(context)

// OBTENER USUARIO ACTUAL
lifecycleScope.launch {
    val result = usuarioRepository.getCurrentUser()
    when (result) {
        is Resource.Success -> {
            val usuario = result.data!!
            Log.d("Usuario", "Nombre: ${usuario.nombreUsuario}")
            Log.d("Usuario", "Email: ${usuario.correo}")
            Log.d("Usuario", "Rol: ${usuario.rol.nombre}")
            // Actualizar perfil
        }
        is Resource.Error -> {
            Log.e("Usuario", result.message ?: "Error")
        }
    }
}

// CAMBIAR CONTRASEÑA
lifecycleScope.launch {
    val result = usuarioRepository.changePassword(
        currentPassword = "oldPassword123",
        newPassword = "newPassword456"
    )
    when (result) {
        is Resource.Success -> {
            Log.d("Usuario", result.data!!.message)
            // Mostrar mensaje de éxito
        }
        is Resource.Error -> {
            Log.e("Usuario", result.message ?: "Error")
            // Mostrar error
        }
    }
}

// OBTENER TODOS LOS USUARIOS (solo ADMIN)
lifecycleScope.launch {
    val result = usuarioRepository.getAllUsers()
    when (result) {
        is Resource.Success -> {
            val usuarios = result.data!!
            Log.d("Usuarios", "${usuarios.size} usuarios en el sistema")
            // Mostrar lista de usuarios
        }
        is Resource.Error -> {
            Log.e("Usuarios", result.message ?: "Error")
        }
    }
}
```

---

## 🎨 Ejemplo en ViewModel

```kotlin
class BeatViewModel(application: Application) : AndroidViewModel(application) {
    
    private val beatRepository = ApiBeatRepository(application.applicationContext)
    
    private val _beats = MutableLiveData<Resource<List<BeatResponseDto>>>()
    val beats: LiveData<Resource<List<BeatResponseDto>>> = _beats
    
    fun loadBeats() {
        viewModelScope.launch {
            _beats.value = Resource.Loading()
            val result = beatRepository.getAllBeats()
            _beats.value = result
        }
    }
    
    fun searchBeats(query: String) {
        viewModelScope.launch {
            _beats.value = Resource.Loading()
            val result = beatRepository.searchBeats(query)
            _beats.value = result
        }
    }
}
```

---

## 🎨 Ejemplo en Fragment

```kotlin
class BeatListFragment : Fragment() {
    
    private lateinit var beatViewModel: BeatViewModel
    private lateinit var beatAdapter: BeatAdapter
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        beatViewModel = ViewModelProvider(this)[BeatViewModel::class.java]
        
        // Observar cambios
        beatViewModel.beats.observe(viewLifecycleOwner) { result ->
            when (result) {
                is Resource.Loading -> {
                    showLoading(true)
                }
                is Resource.Success -> {
                    showLoading(false)
                    val beats = result.data!!
                    beatAdapter.submitList(beats)
                }
                is Resource.Error -> {
                    showLoading(false)
                    showError(result.message ?: "Error desconocido")
                }
            }
        }
        
        // Cargar beats
        beatViewModel.loadBeats()
    }
}
```

---

## ⚠️ Notas Importantes

### Precios
Los precios en el backend están en **CLP (pesos chilenos)** como Integer:
- Backend almacena: `15000` = CLP $15.000
- Valor real: `CLP 15000`
- Para mostrar: Formatear directamente

```kotlin
val precio = beatDto.precio // Ya está en pesos chilenos
val precioFormateado = String.format("CLP $%,d", precio)
// Resultado: "CLP $15,000"
```

### Estados de Pedidos
Estados válidos:
- `PENDIENTE` - Pedido creado, esperando pago
- `CONFIRMADO` - Pago confirmado
- `CANCELADO` - Pedido cancelado
- `COMPLETADO` - Pedido entregado

### Roles
- `USER` - Usuario normal
- `ADMIN` - Administrador

### Manejo de Errores
Siempre usar try-catch y verificar Resource:
```kotlin
when (result) {
    is Resource.Success -> { /* Éxito */ }
    is Resource.Error -> { /* Manejar error */ }
    is Resource.Loading -> { /* Mostrar loading */ }
}
```

### Backend URL
- **Emulador Android**: `http://10.0.2.2:8080/api/`
- **Dispositivo físico**: `http://TU_IP_LOCAL:8080/api/`
- **Producción**: `https://tu-dominio.com/api/`

Configurar en `local.properties`:
```properties
BACKEND_BASE_URL=http://10.0.2.2:8080/api/
```

---

## 🔧 Verificar Backend

```kotlin
// Verificar que el backend esté disponible
val authRepository = ApiAuthRepository(context)
lifecycleScope.launch {
    val result = authRepository.healthCheck()
    when (result) {
        is Resource.Success -> {
            Log.d("Backend", "✅ Backend disponible")
        }
        is Resource.Error -> {
            Log.e("Backend", "❌ Backend no disponible: ${result.message}")
        }
    }
}
```

---

## 📝 Próximos Pasos

1. **Actualizar ViewModels existentes** para usar los nuevos repositorios
2. **Adaptar Fragments** para trabajar con DTOs del backend
3. **Probar flujos** de autenticación y compra
4. **Implementar manejo de errores** robusto
5. **Testing** de integración

---

## 🆘 Troubleshooting

### Error: "Unable to resolve host"
- Verificar que el backend esté corriendo
- Verificar URL en `local.properties`
- Usar `http://10.0.2.2:8080/api/` para emulador

### Error: "401 Unauthorized"
- Token JWT expirado o inválido
- Hacer logout y login nuevamente
- Verificar que el token se esté enviando correctamente

### Error: "403 Forbidden"
- Usuario no tiene permisos (necesita rol ADMIN)
- Verificar rol del usuario

### Error: "Connection timeout"
- Aumentar timeout en RetrofitClient
- Verificar conexión a internet
- Verificar firewall/antivirus

---

## 📞 Contacto

Para dudas o problemas, revisar:
- `MIGRACION_PROGRESO.md` - Estado de la migración
- `PLAN_MIGRACION_BACKEND_KOTLIN.md` - Plan completo
- Logs con tag "ApiBeatRepository", "ApiAuthRepository", etc.
