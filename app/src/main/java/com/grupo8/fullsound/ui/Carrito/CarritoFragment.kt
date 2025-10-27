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
import com.grupo8.fullsound.data.repositories.CarritoRepository
import com.grupo8.fullsound.databinding.FragmentCarritoBinding
import com.grupo8.fullsound.ui.beats.CarritoViewModelFactory
import com.grupo8.fullsound.utils.UserSession
import kotlinx.coroutines.launch
import java.util.Locale

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
    }

    private fun setupRecyclerView() {
        carritoAdapter = CarritoAdapter(
            onRemove = { item ->
                confirmRemoveItem(item)
            },
            onQuantityChange = { item, newQuantity ->
                carritoViewModel.updateItemQuantity(item, newQuantity)
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
            confirmClearCarrito()
        }

        binding.btnCheckout.setOnClickListener {
            processCheckout()
        }
    }

    private fun setupBottomNavigation() {
        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_beats -> {
                    findNavController().navigate(R.id.beatsListaFragment)
                    true
                }
                R.id.navigation_carrito -> {
                    // Ya estamos aquí
                    true
                }
                R.id.navigation_logout -> {
                    logout()
                    true
                }
                else -> false
            }
        }

        // Marcar el item actual
        binding.bottomNavigation.selectedItemId = R.id.navigation_carrito
    }

    private fun confirmRemoveItem(item: com.grupo8.fullsound.data.models.CarritoItem) {
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
                    .setMessage("Total a pagar: ${String.format(Locale.US, "$%.2f", total)}\n\n¿Deseas proceder con la compra?")
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
            binding.txtTotal.text = String.format(Locale.US, "$%.2f", total)
        }
    }

    private fun showEmptyState() {
        binding.rvCarrito.visibility = View.GONE
        binding.layoutEmpty.visibility = View.VISIBLE
        binding.cardTotal.visibility = View.GONE
    }

    private fun hideEmptyState() {
        binding.rvCarrito.visibility = View.VISIBLE
        binding.layoutEmpty.visibility = View.GONE
        binding.cardTotal.visibility = View.VISIBLE
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


