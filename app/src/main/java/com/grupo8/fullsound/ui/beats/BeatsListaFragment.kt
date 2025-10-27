package com.grupo8.fullsound.ui.beats

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.grupo8.fullsound.R
import com.grupo8.fullsound.data.local.AppDatabase
import com.grupo8.fullsound.data.local.LocalBeatsProvider
import com.grupo8.fullsound.data.repositories.BeatRepository
import com.grupo8.fullsound.data.repositories.CarritoRepository
import com.grupo8.fullsound.databinding.FragmentBeatsListaBinding
import com.grupo8.fullsound.ui.carrito.CarritoViewModel
import com.grupo8.fullsound.utils.Resource
import com.grupo8.fullsound.utils.UserSession
import com.grupo8.fullsound.utils.AnimationHelper
import kotlinx.coroutines.launch

class BeatsListaFragment : Fragment() {

    private var _binding: FragmentBeatsListaBinding? = null
    private val binding get() = _binding!!

    private val beatsViewModel: BeatsViewModel by viewModels {
        val database = AppDatabase.getInstance(requireContext())
        val beatRepository = BeatRepository(database.beatDao())
        BeatsViewModelFactory(beatRepository)
    }

    private val carritoViewModel: CarritoViewModel by viewModels {
        val database = AppDatabase.getInstance(requireContext())
        val carritoRepository = CarritoRepository(database.carritoDao())
        CarritoViewModelFactory(carritoRepository)
    }

    private lateinit var beatsAdapter: BeatsAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBeatsListaBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupToolbar()
        setupRecyclerView()
        setupObservers()
        setupBottomNavigation()
        animateEntrance()

        // Cargar beats (primero intentar de la BD, si no hay, cargar de LocalBeatsProvider)
        loadBeats()
    }

    private fun animateEntrance() {
        AnimationHelper.fadeIn(binding.rvBeats, 300)
    }

    private fun setupToolbar() {
        binding.btnBack.setOnClickListener {
            AnimationHelper.animateClick(it)
            // Navegar de regreso al CRUD de Beats (BeatsFragment)
            findNavController().navigate(R.id.action_beatsListaFragment_to_beatsFragment)
        }
    }

    private fun setupRecyclerView() {
        beatsAdapter = BeatsAdapter(
            onAddToCarrito = { beat ->
                carritoViewModel.addBeatToCarrito(beat)
                showMessage("${beat.titulo} agregado al carrito")
            },
            onComprar = { beat ->
                // Agregar al carrito y navegar
                carritoViewModel.addBeatToCarrito(beat)
                showMessage("Procesando compra de ${beat.titulo}")
                // Aquí podrías navegar al carrito o a una pantalla de checkout
            }
        )

        binding.rvBeats.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = beatsAdapter
        }
    }

    private fun setupObservers() {
        beatsViewModel.beatsResult.observe(viewLifecycleOwner) { result ->
            when (result) {
                is Resource.Loading -> {
                    // Mostrar loading
                }
                is Resource.Success -> {
                    val beats = result.data ?: emptyList()
                    if (beats.isEmpty()) {
                        showEmptyState()
                    } else {
                        hideEmptyState()
                        beatsAdapter.submitList(beats)
                    }
                }
                is Resource.Error -> {
                    showMessage(result.message ?: "Error al cargar beats")
                }
            }
        }
    }

    private fun loadBeats() {
        lifecycleScope.launch {
            beatsViewModel.getAllBeats()

            // Esperar un poco para ver si hay beats en la BD
            kotlinx.coroutines.delay(500)

            // Si no hay beats, cargar los de LocalBeatsProvider
            if (beatsAdapter.itemCount == 0) {
                loadLocalBeats()
            }
        }
    }

    private fun loadLocalBeats() {
        lifecycleScope.launch {
            val localBeats = LocalBeatsProvider.getBeats()

            // Insertar en la BD para persistencia
            localBeats.forEach { beat ->
                beatsViewModel.insertBeat(beat)
            }

            // Recargar
            beatsViewModel.getAllBeats()
        }
    }

    private fun setupBottomNavigation() {
        // Botón Logout
        binding.btnLogout.setOnClickListener {
            AnimationHelper.animateClick(it)
            logout()
        }

        // Botón Beats Lista (ya estamos aquí)
        binding.btnNavBeatsLista.setOnClickListener {
            // Ya estamos en la lista de beats
        }

        // Botón Carrito
        binding.btnNavCarrito.setOnClickListener {
            AnimationHelper.animateClick(it)
            findNavController().navigate(R.id.action_beatsListaFragment_to_carritoFragment)
        }
    }

    private fun logout() {
        val userSession = UserSession(requireContext())
        userSession.logout()
        findNavController().navigate(R.id.loginFragment)
    }

    private fun showEmptyState() {
        binding.rvBeats.visibility = View.GONE
        binding.layoutEmpty.visibility = View.VISIBLE
    }

    private fun hideEmptyState() {
        binding.rvBeats.visibility = View.VISIBLE
        binding.layoutEmpty.visibility = View.GONE
    }

    private fun showMessage(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

class CarritoViewModelFactory(private val carritoRepository: CarritoRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CarritoViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return CarritoViewModel(carritoRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

