package com.grupo8.fullsound.ui.carrito

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.grupo8.fullsound.R
import com.grupo8.fullsound.data.local.AppDatabase
import com.grupo8.fullsound.repository.CarritoRepository
import com.grupo8.fullsound.viewmodel.CarritoViewModel
import com.grupo8.fullsound.databinding.FragmentCarritoBinding
import com.grupo8.fullsound.ui.beats.CarritoViewModelFactory
import com.grupo8.fullsound.utils.UserSession
import com.grupo8.fullsound.utils.AnimationHelper
import kotlinx.coroutines.launch
import com.grupo8.fullsound.util.FormatUtils

class CarritoFragment : Fragment() {

    private var _binding: FragmentCarritoBinding? = null
    private val binding get() = _binding!!

    private val carritoViewModel: CarritoViewModel by viewModels {
        val database = AppDatabase.getInstance(requireContext())
        val carritoRepository = CarritoRepository(database.carritoDao())
        CarritoViewModelFactory(carritoRepository)
    }

    private lateinit var carritoAdapter: CarritoAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCarritoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupObservers()
        setupButtons()
        setupBottomNavigation()
        animateEntrance()
    }

    private fun animateEntrance() {
        // Animar elementos principales
        AnimationHelper.fadeIn(binding.rvCarrito, 300)
        AnimationHelper.scaleUp(binding.cardTotal, 250)
    }

    private fun setupRecyclerView() {
        carritoAdapter = CarritoAdapter(
            onRemove = { item ->
                confirmRemoveItem(item)
            }
        )

        binding.rvCarrito.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = carritoAdapter
        }
    }

    private fun setupObservers() {
        carritoViewModel.carritoItems.observe(viewLifecycleOwner) { items ->
            if (items.isEmpty()) {
                showEmptyState()
            } else {
                hideEmptyState()
                carritoAdapter.submitList(items)
                updateTotal()
            }
        }
    }

    private fun setupButtons() {
        binding.btnClearCarrito.setOnClickListener {
            AnimationHelper.animateClick(it)
            confirmClearCarrito()
        }

        binding.btnCheckout.setOnClickListener {
            AnimationHelper.animateClick(it)
            processCheckout()
        }
    }

    private fun setupBottomNavigation() {
        // Botón Logout
        binding.btnLogout.setOnClickListener {
            AnimationHelper.animateClick(it)
            logout()
        }

        // Botón Beats Lista
        binding.btnNavBeatsLista.setOnClickListener {
            AnimationHelper.animateClick(it)
            findNavController().navigate(R.id.action_carritoFragment_to_beatsListaFragment)
        }

        // Botón Carrito (ya estamos aquí)
        binding.btnNavCarrito.setOnClickListener {
            // Ya estamos en el carrito
        }
    }

    private fun confirmRemoveItem(item: com.grupo8.fullsound.model.CarritoItem) {
        AlertDialog.Builder(requireContext())
            .setTitle("Eliminar del carrito")
            .setMessage("¿Deseas eliminar '${item.titulo}' del carrito?")
            .setPositiveButton("Eliminar") { _, _ ->
                carritoViewModel.removeItemFromCarrito(item)
                showMessage("${item.titulo} eliminado del carrito")
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun confirmClearCarrito() {
        AlertDialog.Builder(requireContext())
            .setTitle("Vaciar carrito")
            .setMessage("¿Estás seguro de que deseas vaciar todo el carrito?")
            .setPositiveButton("Vaciar") { _, _ ->
                carritoViewModel.clearCarrito()
                showMessage("Carrito vaciado")
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun processCheckout() {
        lifecycleScope.launch {
            val total = carritoViewModel.getTotalPrice()

            if (total > 0) {
                AlertDialog.Builder(requireContext())
                    .setTitle("Confirmar compra")
                    .setMessage("Total a pagar: ${FormatUtils.formatClp(total)}\n\n¿Deseas proceder con la compra?")
                    .setPositiveButton("Comprar") { _, _ ->
                        // Aquí iría la lógica de pago real
                        showMessage("¡Compra realizada con éxito!")
                        carritoViewModel.clearCarrito()
                    }
                    .setNegativeButton("Cancelar", null)
                    .show()
            } else {
                showMessage("El carrito está vacío")
            }
        }
    }

    private fun updateTotal() {
        lifecycleScope.launch {
            val total = carritoViewModel.getTotalPrice()
            binding.txtTotal.text = FormatUtils.formatClp(total)
        }
    }

    private fun showEmptyState() {
        AnimationHelper.fadeOut(binding.rvCarrito)
        AnimationHelper.fadeOut(binding.cardTotal)
        AnimationHelper.fadeIn(binding.layoutEmpty, 400)
    }

    private fun hideEmptyState() {
        AnimationHelper.fadeOut(binding.layoutEmpty)
        AnimationHelper.fadeIn(binding.rvCarrito, 400)
        AnimationHelper.scaleUp(binding.cardTotal, 250)
    }

    private fun logout() {
        val userSession = UserSession(requireContext())
        userSession.logout()
        findNavController().navigate(R.id.loginFragment)
    }

    private fun showMessage(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
