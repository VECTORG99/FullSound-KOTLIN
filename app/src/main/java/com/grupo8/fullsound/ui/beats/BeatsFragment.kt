package com.grupo8.fullsound.ui.beats

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.grupo8.fullsound.data.local.AppDatabase
import com.grupo8.fullsound.data.repositories.BeatRepository
import com.grupo8.fullsound.databinding.FragmentBeatsBinding
import com.grupo8.fullsound.utils.Resource

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
        setupObservers()

        // Cargar todos los beats al iniciar
        viewModel.getAllBeats()
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
                    binding.beatsTitle.text = "Total de Beats: ${beats?.size ?: 0}"
                }
                is Resource.Error -> {
                    // Mostrar error
                    showMessage(result.message ?: "Error al cargar beats")
                }
                else -> {}
            }
        }

        // Observar resultado de operaciones individuales
        viewModel.beatResult.observe(viewLifecycleOwner) { result ->
            when (result) {
                is Resource.Success -> {
                    showMessage("Operación exitosa")
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
