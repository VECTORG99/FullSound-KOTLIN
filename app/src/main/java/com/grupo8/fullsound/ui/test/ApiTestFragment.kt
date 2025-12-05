package com.grupo8.fullsound.ui.test

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.grupo8.fullsound.databinding.FragmentApiTestBinding
import com.grupo8.fullsound.utils.Resource
import com.grupo8.fullsound.viewmodel.ApiBeatsViewModel
import com.grupo8.fullsound.viewmodel.ApiLoginViewModel
import com.grupo8.fullsound.viewmodel.ApiRegisterViewModel

/**
 * Fragment de prueba para verificar la integración con el backend Spring Boot
 */
class ApiTestFragment : Fragment() {

    private var _binding: FragmentApiTestBinding? = null
    private val binding get() = _binding!!

    private val beatsViewModel: ApiBeatsViewModel by viewModels()
    private val loginViewModel: ApiLoginViewModel by viewModels()
    private val registerViewModel: ApiRegisterViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentApiTestBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupObservers()
        setupButtons()
    }

    private fun setupObservers() {
        // Observer para beats
        beatsViewModel.beatsResult.observe(viewLifecycleOwner) { result ->
            when (result) {
                is Resource.Loading -> {
                    binding.tvStatus.text = "Cargando beats..."
                    binding.progressBar.visibility = View.VISIBLE
                }
                is Resource.Success -> {
                    binding.progressBar.visibility = View.GONE
                    val beats = result.data ?: emptyList()
                    binding.tvStatus.text = "✅ ${beats.size} beats cargados\n\n" +
                            beats.take(5).joinToString("\n") { 
                                "• ${it.titulo} - ${it.artista} - CLP $${it.precio}"
                            }
                    Toast.makeText(context, "Beats cargados exitosamente", Toast.LENGTH_SHORT).show()
                }
                is Resource.Error -> {
                    binding.progressBar.visibility = View.GONE
                    binding.tvStatus.text = "❌ Error: ${result.message}"
                    Toast.makeText(context, result.message, Toast.LENGTH_LONG).show()
                }
            }
        }

        // Observer para búsqueda
        beatsViewModel.searchResults.observe(viewLifecycleOwner) { result ->
            when (result) {
                is Resource.Success -> {
                    val beats = result.data ?: emptyList()
                    binding.tvStatus.text = "🔍 ${beats.size} resultados encontrados\n\n" +
                            beats.joinToString("\n") { 
                                "• ${it.titulo} por ${it.artista}"
                            }
                }
                is Resource.Error -> {
                    binding.tvStatus.text = "❌ Error en búsqueda: ${result.message}"
                }
                else -> {}
            }
        }

        // Observer para featured
        beatsViewModel.featuredBeats.observe(viewLifecycleOwner) { result ->
            when (result) {
                is Resource.Success -> {
                    val beats = result.data ?: emptyList()
                    binding.tvStatus.text = "⭐ ${beats.size} beats destacados\n\n" +
                            beats.joinToString("\n") { 
                                "• ${it.titulo} - CLP $${it.precio}"
                            }
                }
                is Resource.Error -> {
                    binding.tvStatus.text = "❌ Error: ${result.message}"
                }
                else -> {}
            }
        }

        // Observer para registro
        registerViewModel.registerResult.observe(viewLifecycleOwner) { result ->
            when (result) {
                is Resource.Loading -> {
                    binding.tvStatus.text = "Registrando usuario..."
                    binding.progressBar.visibility = View.VISIBLE
                }
                is Resource.Success -> {
                    binding.progressBar.visibility = View.GONE
                    binding.tvStatus.text = "✅ ${result.data?.message}"
                    Toast.makeText(context, "Usuario registrado", Toast.LENGTH_SHORT).show()
                }
                is Resource.Error -> {
                    binding.progressBar.visibility = View.GONE
                    binding.tvStatus.text = "❌ Error en registro: ${result.message}"
                    Toast.makeText(context, result.message, Toast.LENGTH_LONG).show()
                }
            }
        }

        // Observer para login
        loginViewModel.loginResult.observe(viewLifecycleOwner) { result ->
            when (result) {
                is Resource.Loading -> {
                    binding.tvStatus.text = "Iniciando sesión..."
                    binding.progressBar.visibility = View.VISIBLE
                }
                is Resource.Success -> {
                    binding.progressBar.visibility = View.GONE
                    val authResponse = result.data!!
                    binding.tvStatus.text = "✅ Login exitoso\n\n" +
                            "Usuario: ${authResponse.usuario.nombreUsuario}\n" +
                            "Email: ${authResponse.usuario.correo}\n" +
                            "Rol: ${authResponse.usuario.rol.nombre}\n" +
                            "Token: ${authResponse.token.take(50)}..."
                    Toast.makeText(context, "Login exitoso", Toast.LENGTH_SHORT).show()
                }
                is Resource.Error -> {
                    binding.progressBar.visibility = View.GONE
                    binding.tvStatus.text = "❌ Error en login: ${result.message}"
                    Toast.makeText(context, result.message, Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun setupButtons() {
        binding.btnGetBeats.setOnClickListener {
            binding.tvStatus.text = "Obteniendo beats del backend..."
            beatsViewModel.getAllBeats()
        }

        binding.btnSearchBeats.setOnClickListener {
            binding.tvStatus.text = "Buscando beats con 'rock'..."
            beatsViewModel.searchBeats("rock")
        }

        binding.btnFeatured.setOnClickListener {
            binding.tvStatus.text = "Obteniendo beats destacados..."
            beatsViewModel.getFeaturedBeats(5)
        }

        binding.btnRegister.setOnClickListener {
            val testEmail = "testuser${System.currentTimeMillis()}@fullsound.com"
            binding.tvStatus.text = "Registrando usuario: $testEmail"
            registerViewModel.register(
                email = testEmail,
                username = "testuser${System.currentTimeMillis()}",
                password = "Password123!",
                nombre = "Test",
                apellido = "User"
            )
        }

        binding.btnLogin.setOnClickListener {
            binding.tvStatus.text = "Intentando login con usuario de prueba..."
            loginViewModel.login(
                email = "test2@fullsound.com",
                password = "Password123!"
            )
        }

        binding.btnCheckSession.setOnClickListener {
            val isLoggedIn = loginViewModel.isLoggedIn()
            if (isLoggedIn) {
                val userData = loginViewModel.getCurrentUserData()
                binding.tvStatus.text = "✅ Sesión activa\n\n" +
                        "User ID: ${userData["userId"]}\n" +
                        "Username: ${userData["username"]}\n" +
                        "Email: ${userData["email"]}\n" +
                        "Role: ${userData["role"]}\n" +
                        "Is Admin: ${userData["isAdmin"]}"
            } else {
                binding.tvStatus.text = "❌ No hay sesión activa"
            }
        }

        binding.btnLogout.setOnClickListener {
            loginViewModel.logout()
            binding.tvStatus.text = "✅ Sesión cerrada"
            Toast.makeText(context, "Logout exitoso", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
