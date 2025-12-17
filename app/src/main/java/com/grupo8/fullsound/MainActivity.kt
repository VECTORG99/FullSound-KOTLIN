package com.grupo8.fullsound

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import com.grupo8.fullsound.utils.UserSession
import com.grupo8.fullsound.data.remote.supabase.repository.SupabaseUserRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {
    private val TAG = "MainActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController

        // DIAGNÓSTICO: Probar conexión con Supabase al iniciar
        testSupabaseConnection()

        // Verificar si hay una sesión activa
        val userSession = UserSession(this)
        if (userSession.isLoggedIn()) {
            // Si hay sesión activa, navegar directamente a Beats
            navController.navigate(R.id.beatsFragment)
        }
    }

    private fun testSupabaseConnection() {
        CoroutineScope(Dispatchers.Main).launch {
            Log.d(TAG, "========== DIAGNÓSTICO DE SUPABASE ==========")
            Log.d(TAG, "Configuración:")
            Log.d(TAG, "   URL: ${BuildConfig.SUPABASE_URL}")
            Log.d(TAG, "   Key: ${BuildConfig.SUPABASE_ANON_KEY.take(20)}...")

            try {
                val repository = SupabaseUserRepository()

                // Test 1: Listar todos los usuarios
                Log.d(TAG, "\nTest 1: Obtener todos los usuarios...")
                val users = withContext(Dispatchers.IO) {
                    repository.getAllUsers()
                }

                if (users.isEmpty()) {
                    Log.w(TAG, "No se encontraron usuarios en Supabase")
                    Log.w(TAG, "   Verifica que:")
                    Log.w(TAG, "   1. La tabla 'usuario' existe en Supabase")
                    Log.w(TAG, "   2. Hay datos en la tabla")
                    Log.w(TAG, "   3. Las políticas RLS permiten lectura")
                } else {
                    Log.d(TAG, "Se encontraron ${users.size} usuarios:")
                    users.forEachIndexed { index, user ->
                        Log.d(TAG, "   ${index + 1}. ${user.username} (${user.email})")
                        Log.d(TAG, "      - ID: ${user.id}")
                        Log.d(TAG, "      - Rol: ${user.role}")
                        Log.d(TAG, "      - RUT: ${user.rut}")
                    }
                }

                // Test 2: Probar login con un usuario existente (si hay usuarios)
                if (users.isNotEmpty()) {
                    val testUser = users.first()
                    Log.d(TAG, "\nTest 2: Probar autenticación con usuario: ${testUser.username}")
                    Log.d(TAG, "   Intentando login con email: ${testUser.email}")

                    // Nota: No podemos probar con la contraseña real sin conocerla
                    Log.d(TAG, "   Para probar login completo, usa las credenciales reales en la pantalla de login")
                }

                Log.d(TAG, "\nDiagnóstico completado")
                Log.d(TAG, "========================================\n")

            } catch (e: Exception) {
                Log.e(TAG, "Error en diagnóstico de Supabase:", e)
                Log.e(TAG, "   Tipo: ${e.javaClass.simpleName}")
                Log.e(TAG, "   Mensaje: ${e.message}")
                Log.e(TAG, "   Verifica:")
                Log.e(TAG, "   1. La URL de Supabase es correcta")
                Log.e(TAG, "   2. La ANON_KEY es válida")
                Log.e(TAG, "   3. El dispositivo tiene conexión a Internet")
                Log.e(TAG, "========================================\n")
            }
        }
    }
}
