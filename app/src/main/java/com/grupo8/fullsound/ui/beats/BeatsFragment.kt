package com.grupo8.fullsound.ui.beats

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.grupo8.fullsound.R
import com.grupo8.fullsound.data.local.AppDatabase
import com.grupo8.fullsound.data.repositories.BeatRepository
import com.grupo8.fullsound.databinding.FragmentBeatsBinding
import com.grupo8.fullsound.utils.Resource
import com.grupo8.fullsound.utils.UserSession

class BeatsFragment : Fragment() {

    private var _binding: FragmentBeatsBinding? = null
    private val binding get() = _binding!!

    private val viewModel: BeatsViewModel by viewModels {
        val database = AppDatabase.getInstance(requireContext())
        val beatRepository = BeatRepository(database.beatDao())
        BeatsViewModelFactory(beatRepository)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBeatsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Configurar el botón de retroceso para cerrar la app
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                // Finalizar la actividad (cerrar la app)
                requireActivity().finish()
            }
        })

        setupObservers()
        setupClickListeners()

        // Cargar todos los beats al iniciar
        viewModel.getAllBeats()
    }

    private fun setupClickListeners() {
        // Botón de cerrar sesión
        binding.btnLogout.setOnClickListener {
            logout()
        }

        // Botones de navegación
        binding.btnNavBeats.setOnClickListener {
            // Ya estamos en Beats
        }

        binding.btnNavCarrito.setOnClickListener {
            // TODO: Navegar al carrito
            showMessage("Navegando al carrito...")
        }

        // Cards CRUD
        binding.cardCrear.setOnClickListener {
            // TODO: Navegar a crear beat
            showMessage("Crear beat")
        }

        binding.cardLeer.setOnClickListener {
            // Leer beats
            viewModel.getAllBeats()
            showMessage("Cargando beats...")
        }

        binding.cardActualizar.setOnClickListener {
            // TODO: Navegar a actualizar beat
            showMessage("Actualizar beat")
        }

        binding.cardEliminar.setOnClickListener {
            // TODO: Navegar a eliminar beat
            showMessage("Eliminar beat")
        }
    }

    private fun logout() {
        // Limpiar la sesión
        val userSession = UserSession(requireContext())
        userSession.logout()

        // Navegar al login
        findNavController().navigate(R.id.loginFragment)
    }

    private fun setupObservers() {
        // Observar lista de beats
        viewModel.beatsResult.observe(viewLifecycleOwner) { result ->
            when (result) {
                is Resource.Loading -> {
                    // Mostrar cargando
                }
                is Resource.Success -> {
                    val beats = result.data
                    // Actualizar UI con la lista de beats
                    binding.txtTituloBeats.text = "Total de Beats: ${beats?.size ?: 0}"
                }
                is Resource.Error -> {
                    // Mostrar error
                    showMessage(result.message ?: "Error al cargar beats")
                }
            }
        }

        // Observar resultado de operaciones individuales
        viewModel.beatResult.observe(viewLifecycleOwner) { result ->
            when (result) {
                is Resource.Success -> {
                    showMessage("Operación exitosa")
                    viewModel.getAllBeats()
                }
                is Resource.Error -> {
                    showMessage(result.message ?: "Error en operación")
                }
                else -> {}
            }
        }

        // Observar resultado de eliminación
        viewModel.deleteResult.observe(viewLifecycleOwner) { result ->
            when (result) {
                is Resource.Success -> {
                    showMessage(result.data ?: "Eliminado exitosamente")
                    // Recargar la lista
                    viewModel.getAllBeats()
                }
                is Resource.Error -> {
                    showMessage(result.message ?: "Error al eliminar")
                }
                else -> {}
            }
        }
    }

    private fun showMessage(message: String) {
        android.widget.Toast.makeText(requireContext(), message, android.widget.Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

class BeatsViewModelFactory(private val beatRepository: BeatRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(BeatsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return BeatsViewModel(beatRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
