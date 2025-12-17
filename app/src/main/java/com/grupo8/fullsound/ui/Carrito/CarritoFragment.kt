package com.grupo8.fullsound.ui.carrito

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
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
import com.grupo8.fullsound.utils.PermissionHelper
import kotlinx.coroutines.launch
import com.grupo8.fullsound.utils.FormatUtils

class CarritoFragment : Fragment() {

    private var _binding: FragmentCarritoBinding? = null
    private val binding get() = _binding!!

    private val carritoViewModel: CarritoViewModel by viewModels {
        val database = AppDatabase.getInstance(requireContext())
        val carritoRepository = CarritoRepository(database.carritoDao())
        CarritoViewModelFactory(carritoRepository)
    }

    private lateinit var carritoAdapter: CarritoAdapter

    // Launcher para solicitar permisos
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val allGranted = permissions.entries.all { it.value }
        if (allGranted || !PermissionHelper.shouldRequestPermissions()) {
            // Proceder con la descarga
            proceedWithPurchase()
        } else {
            showMessage("Se necesitan permisos de almacenamiento para descargar la licencia")
        }
    }

    // Variable para almacenar el total temporalmente
    private var pendingPurchaseTotal: Double = 0.0

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
            val subtotal = carritoViewModel.getSubtotal()
            val iva = carritoViewModel.getIva()
            val total = carritoViewModel.getTotalConIva()

            if (total > 0) {
                val mensaje = """
                    Subtotal: ${FormatUtils.formatClp(subtotal)}
                    IVA (19%): ${FormatUtils.formatClp(iva)}
                    Total: ${FormatUtils.formatClp(total)}
                    
                    ¿Deseas proceder con la compra?
                    Se descargará tu licencia automáticamente.
                """.trimIndent()

                AlertDialog.Builder(requireContext())
                    .setTitle("Confirmar compra")
                    .setMessage(mensaje)
                    .setPositiveButton("Comprar") { _, _ ->
                        pendingPurchaseTotal = total
                        checkPermissionsAndProceed()
                    }
                    .setNegativeButton("Cancelar", null)
                    .show()
            } else {
                showMessage("El carrito está vacío")
            }
        }
    }

    private fun checkPermissionsAndProceed() {
        if (PermissionHelper.hasStoragePermission(requireContext())) {
            // Ya tenemos permisos, proceder
            proceedWithPurchase()
        } else if (PermissionHelper.shouldRequestPermissions()) {
            // Solicitar permisos
            val permissions = PermissionHelper.getRequiredPermissions()
            if (permissions.isNotEmpty()) {
                requestPermissionLauncher.launch(permissions)
            } else {
                proceedWithPurchase()
            }
        } else {
            // Android 10+, no se necesitan permisos
            proceedWithPurchase()
        }
    }

    private fun proceedWithPurchase() {
        completePurchase(pendingPurchaseTotal)
    }

    private fun completePurchase(total: Double) {
        lifecycleScope.launch {
            try {
                // Obtener items del carrito antes de limpiarlo
                val items = carritoViewModel.carritoItems.value ?: emptyList()

                if (items.isEmpty()) {
                    showMessage("El carrito está vacío")
                    return@launch
                }

                // Obtener información del usuario
                val userSession = UserSession(requireContext())
                val user = com.grupo8.fullsound.model.User(
                    id = userSession.getUserId() ?: "",
                    email = userSession.getUserEmail() ?: "",
                    username = userSession.getUserUsername() ?: "",
                    password = "",
                    name = userSession.getUserName() ?: "Usuario",
                    rut = userSession.getUserRut() ?: "N/A",
                    createdAt = 0L
                )

                // Generar contenido de la licencia
                val licenseContent = com.grupo8.fullsound.utils.LicenseGenerator.generateLicenseContent(
                    items = items,
                    user = user
                )

                // Descargar la licencia
                val licenseSuccess = com.grupo8.fullsound.utils.LicenseDownloader.downloadLicense(
                    context = requireContext(),
                    licenseContent = licenseContent
                )

                // Descargar los beats en MP3
                val database = AppDatabase.getInstance(requireContext())
                val beatDao = database.beatDao()
                var beatsDownloadedCount = 0

                items.forEach { item ->
                    val beat = beatDao.getBeatById(item.beatId)
                    if (beat != null && !beat.mp3Path.isNullOrBlank()) {
                        val downloadSuccess = com.grupo8.fullsound.utils.BeatDownloader.downloadBeat(
                            context = requireContext(),
                            mp3Url = beat.mp3Path,
                            beatTitle = beat.titulo,
                            artistName = beat.artista
                        )
                        if (downloadSuccess) {
                            beatsDownloadedCount++
                        }
                    }
                }

                if (licenseSuccess) {
                    // Limpiar el carrito
                    carritoViewModel.clearCarrito()

                    // Mostrar mensaje de éxito con información de descarga
                    val message = buildString {
                        append("Tu compra se ha completado exitosamente.\n\n")

                        if (licenseSuccess) {
                            append("📄 Licencia descargada en:\n")
                            append("   Descargas/FullSound/\n\n")
                        }

                        if (beatsDownloadedCount > 0) {
                            append("🎵 $beatsDownloadedCount beat(s) descargado(s) en:\n")
                            append("   Música/FullSound/\n\n")
                        }

                        append("Conserva la licencia como prueba de tus derechos de uso.")
                    }

                    AlertDialog.Builder(requireContext())
                        .setTitle("¡Compra exitosa!")
                        .setMessage(message)
                        .setPositiveButton("Aceptar", null)
                        .show()
                } else {
                    showMessage("Compra completada, pero hubo un error al descargar la licencia")
                }
            } catch (e: Exception) {
                e.printStackTrace()
                showMessage("Error al procesar la compra: ${e.message}")
            }
        }
    }

    private fun updateTotal() {
        lifecycleScope.launch {
            val subtotal = carritoViewModel.getSubtotal()
            val iva = carritoViewModel.getIva()
            val total = carritoViewModel.getTotalConIva()

            binding.txtSubtotal.text = FormatUtils.formatClp(subtotal)
            binding.txtIva.text = FormatUtils.formatClp(iva)
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
