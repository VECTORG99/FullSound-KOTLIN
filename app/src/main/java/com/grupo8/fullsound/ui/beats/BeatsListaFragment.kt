package com.grupo8.fullsound.ui.beats

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.grupo8.fullsound.R
import com.grupo8.fullsound.data.local.AppDatabase
import com.grupo8.fullsound.databinding.FragmentBeatsListaBinding
import com.grupo8.fullsound.repository.BeatRepository
import com.grupo8.fullsound.repository.CarritoRepository
import com.grupo8.fullsound.utils.AnimationHelper
import com.grupo8.fullsound.utils.Resource
import com.grupo8.fullsound.utils.UserSession
import com.grupo8.fullsound.viewmodel.BeatsViewModel
import com.grupo8.fullsound.viewmodel.CarritoViewModel

class BeatsListaFragment : Fragment() {

    private var _binding: FragmentBeatsListaBinding? = null
    private val binding get() = _binding!!

    private val beatsViewModel: BeatsViewModel by viewModels {
        val database = AppDatabase.getInstance(requireContext())
        val beatRepository = BeatRepository(database.beatDao(), requireContext())
        BeatsViewModelFactory(beatRepository)
    }

    private val carritoViewModel: CarritoViewModel by viewModels {
        val database = AppDatabase.getInstance(requireContext())
        val carritoRepository = CarritoRepository(database.carritoDao())
        CarritoViewModelFactory(carritoRepository)
    }

    private lateinit var beatsAdapter: BeatsAdapter
    private var allBeats: List<com.grupo8.fullsound.model.Beat> = emptyList()
    private var currentGeneroFilter: String? = null

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
        setupGenreFilter()
        setupObservers()
        setupBottomNavigation()
        animateEntrance()

        loadBeats()
    }

    private fun animateEntrance() {
        AnimationHelper.fadeIn(binding.rvBeats, 300)
    }

    private fun setupToolbar() {
        binding.btnBack.setOnClickListener {
            AnimationHelper.animateClick(it)
            findNavController().navigate(R.id.action_beatsListaFragment_to_beatsFragment)
        }
    }

    private fun setupRecyclerView() {
        beatsAdapter = BeatsAdapter(
            onAddToCarrito = { beat ->
                carritoViewModel.addBeatToCarrito(beat)
            },
            onComprar = { beat ->
                carritoViewModel.addBeatToCarrito(beat)
            }
        )

        binding.rvBeats.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = beatsAdapter
        }
    }

    private fun setupObservers() {
        carritoViewModel.addToCarritoResult.observe(viewLifecycleOwner) { result ->
            when (result) {
                is com.grupo8.fullsound.viewmodel.AddToCarritoResult.Success -> {
                    showMessage("${result.beatTitle} agregado al carrito")
                }
                is com.grupo8.fullsound.viewmodel.AddToCarritoResult.AlreadyExists -> {
                    showMessage("${result.beatTitle} ya está en el carrito. Solo puedes comprar 1 unidad por beat.")
                }
            }
        }

        beatsViewModel.beatsResult.observe(viewLifecycleOwner) { result ->
            when (result) {
                is Resource.Loading -> {
                    // Mostrar loading
                }
                is Resource.Success -> {
                    allBeats = result.data ?: emptyList()
                    applyGenreFilter()
                }
                is Resource.Error -> {
                    showMessage(result.message ?: "Error al cargar beats")
                }
            }
        }
    }

    private fun setupGenreFilter() {
        binding.btnFiltroGenero.setOnClickListener {
            showGenreFilterDialog()
        }
    }

    private fun showGenreFilterDialog() {
        // Obtener lista de géneros únicos
        val genres = allBeats.mapNotNull { it.genero }.distinct().sorted().toMutableList()
        genres.add(0, "Todos los géneros")

        val genresArray = genres.toTypedArray()

        androidx.appcompat.app.AlertDialog.Builder(requireContext())
            .setTitle(getString(R.string.filtrar_por_genero))
            .setItems(genresArray) { _, which ->
                val selectedGenre = genres[which]
                currentGeneroFilter = if (selectedGenre == "Todos los géneros") null else selectedGenre
                binding.btnFiltroGenero.text = selectedGenre
                applyGenreFilter()
            }
            .show()
    }

    private fun applyGenreFilter() {
        val filteredBeats = if (currentGeneroFilter == null) {
            allBeats
        } else {
            allBeats.filter { it.genero == currentGeneroFilter }
        }

        if (filteredBeats.isEmpty()) {
            showEmptyState()
        } else {
            hideEmptyState()
            beatsAdapter.submitList(filteredBeats)
        }

        Log.d("BeatsListaFragment", "Filtro aplicado: $currentGeneroFilter - ${filteredBeats.size} beats")
    }

    private fun loadBeats() {
        Log.d("BeatsListaFragment", "=== Iniciando carga de beats desde backend API ===")
        beatsViewModel.getAllBeats()
    }

    private fun setupBottomNavigation() {
        binding.btnLogout.setOnClickListener {
            AnimationHelper.animateClick(it)
            logout()
        }

        binding.btnNavBeatsLista.setOnClickListener {
            // Ya estamos en la lista de beats
        }

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

