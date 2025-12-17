package com.grupo8.fullsound.ui.beats

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.grupo8.fullsound.R
import com.grupo8.fullsound.data.local.AppDatabase
import com.grupo8.fullsound.repository.BeatRepository
import com.grupo8.fullsound.viewmodel.BeatsViewModel
import com.grupo8.fullsound.databinding.FragmentBeatsBinding
import com.grupo8.fullsound.utils.Resource
import com.grupo8.fullsound.utils.UserSession
import com.grupo8.fullsound.utils.AnimationHelper
import com.grupo8.fullsound.data.remote.supabase.repository.SupabaseStorageRepository
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import coil.load
import java.io.File
import java.io.FileOutputStream

class BeatsFragment : Fragment() {

    private var _binding: FragmentBeatsBinding? = null
    private val binding get() = _binding!!

    // Repositorio para subir archivos a Supabase Storage
    private lateinit var storageRepository: SupabaseStorageRepository

    private val viewModel: BeatsViewModel by viewModels {
        val database = AppDatabase.getInstance(requireContext())
        val beatRepository = BeatRepository(database.beatDao())
        BeatsViewModelFactory(beatRepository)
    }

    // Adaptador para la lista de beats (se configurará según el rol)
    private lateinit var beatsAdapter: BeatsAdapter

    // URIs de archivos seleccionados
    private var selectedImageUri: Uri? = null
    private var selectedAudioUri: Uri? = null

    // Beat seleccionado para eliminar
    private var beatToDelete: com.grupo8.fullsound.model.Beat? = null

    // Beat seleccionado para actualizar
    private var beatToUpdate: com.grupo8.fullsound.model.Beat? = null

    // Launcher para seleccionar imagen
    private val selectImageLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { handleImageSelection(it) }
    }

    // Launcher para seleccionar audio
    private val selectAudioLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { handleAudioSelection(it) }
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

        android.util.Log.d("BeatsFragment", "BEATS FRAGMENT INICIADO")

        // Inicializar repositorio de Supabase Storage
        storageRepository = SupabaseStorageRepository(requireContext())

        // Determinar si el usuario es admin
        val userSession = UserSession(requireContext())
        val isAdmin = userSession.isAdmin()

        android.util.Log.d("BeatsFragment", "Usuario es admin: $isAdmin")

        // Inicializar adapter con showId según el rol
        beatsAdapter = BeatsAdapter(
            onAddToCarrito = { beat ->
                showMessage("Agregado al carrito: ${beat.titulo}")
            },
            onComprar = { beat ->
                showMessage("Comprando: ${beat.titulo}")
            },
            showId = isAdmin
        )

        android.util.Log.d("BeatsFragment", "Adaptador configurado. Cargando beats...")

        // Configurar el botón de retroceso para cerrar la app
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                // Finalizar la actividad (cerrar la app)
                requireActivity().finish()
            }
        })

        setupRecyclerView()
        setupObservers()
        setupGeneroDropdown()
        setupClickListeners()

        // Configurar la UI según el rol (admin vs usuario común)
        configureUIBasedOnUserRole()

        // Los beats se cargarán automáticamente desde Supabase (o caché local)
        // al navegar a BeatsListaFragment
    }

    private fun configureUIBasedOnUserRole() {
        val userSession = UserSession(requireContext())

        // Si no hay sesión, forzar al login
        if (!userSession.isLoggedIn()) {
            findNavController().navigate(R.id.loginFragment)
            return
        }

        val isAdmin = userSession.isAdmin()

        if (!isAdmin) {
            // Usuarios no-admin no deben ver opciones de actualizar/eliminar
            binding.cardActualizar.visibility = View.GONE
            binding.cardEliminar.visibility = View.GONE

            // Ajustar textos para usuarios finales (no-admin)
            binding.txtTituloBeats.text = "Catálogo de beats"
            // El header secundario no tiene id; mantener su texto en XML. Ajustar labels de cards.
            binding.btnCrear.text = "Subir beat"
            binding.btnLeer.text = "Ver catálogo"

            // Mostrar el catálogo por defecto (precios en CLP desde Supabase)
            binding.containerListaBeats.visibility = View.VISIBLE
            viewModel.getAllBeats()

            // Opcional: ocultar el mensaje de total que aplica para admin CRUD si existe
            // (Se mantiene el cardCrear para que puedan subir sus beats)
        } else {
            // Admin: restaurar textos por defecto y mostrar todo
            binding.txtTituloBeats.text = getString(R.string.beats)
            binding.btnCrear.text = getString(R.string.crear_beat)
            binding.btnLeer.text = getString(R.string.leer_beats)

            binding.cardActualizar.visibility = View.VISIBLE
            binding.cardEliminar.visibility = View.VISIBLE
            // Cargar beats para administración (se deja en USD porque admin puede necesitar ver valores originales)
            viewModel.getAllBeats()
        }
    }

    private fun setupRecyclerView() {
        binding.recyclerBeats.apply {
            adapter = beatsAdapter
            layoutManager = androidx.recyclerview.widget.LinearLayoutManager(requireContext())
            setHasFixedSize(false)
        }
    }

    private fun setupGeneroDropdown() {
        // Obtener el array de géneros desde los recursos
        val generos = resources.getStringArray(R.array.generos_musicales)

        // Crear el adaptador para el dropdown
        val adapter = android.widget.ArrayAdapter(
            requireContext(),
            android.R.layout.simple_dropdown_item_1line,
            generos
        )

        // Asignar el adaptador al AutoCompleteTextView de CREAR
        binding.editGenero.setAdapter(adapter)

        // Asignar el adaptador al AutoCompleteTextView de ACTUALIZAR
        binding.editGeneroActualizar.setAdapter(adapter)
    }

    private fun setupClickListeners() {
        // Botón de cerrar sesión
        binding.btnLogout.setOnClickListener {
            logout()
        }

        // Botones de navegación
        binding.btnNavBeatsLista.setOnClickListener {
        }

        binding.btnNavCarrito.setOnClickListener {
            // Navegar al carrito
            findNavController().navigate(R.id.action_beatsFragment_to_carritoFragment)
        }

        // Cards CRUD
        binding.cardCrear.setOnClickListener {
            toggleFormCrear()
        }

        binding.cardLeer.setOnClickListener {
            toggleListaBeats()
        }

        binding.cardActualizar.setOnClickListener {
            toggleFormActualizar()
        }

        binding.cardEliminar.setOnClickListener {
            toggleFormEliminar()
        }

        // Botones del formulario de crear
        binding.btnCancelarCrear.setOnClickListener {
            hideFormCrear()
        }

        binding.btnGuardarBeat.setOnClickListener {
            guardarNuevoBeat()
        }

        // Botones de selección de archivos
        binding.btnSeleccionarImagen.setOnClickListener {
            selectImageLauncher.launch("image/*")
        }

        binding.btnSeleccionarAudio.setOnClickListener {
            selectAudioLauncher.launch("audio/*")
        }

        // Botones del formulario de eliminar
        binding.btnBuscarBeat.setOnClickListener {
            buscarBeatParaEliminar()
        }

        binding.btnCancelarEliminar.setOnClickListener {
            hideFormEliminar()
        }

        binding.btnConfirmarEliminar.setOnClickListener {
            confirmarEliminarBeat()
        }

        // Botones del formulario de actualizar
        binding.btnBuscarActualizar.setOnClickListener {
            buscarBeatParaActualizar()
        }

        binding.btnCancelarActualizar.setOnClickListener {
            hideFormActualizar()
        }

        binding.btnConfirmarActualizar.setOnClickListener {
            confirmarActualizarBeat()
        }
    }

    private fun toggleFormCrear() {
        val isVisible = binding.formCrearBeat.visibility == View.VISIBLE
        AnimationHelper.toggleVisibility(
            binding.formCrearBeat,
            !isVisible,
            AnimationHelper.AnimationType.SLIDE
        )
    }

    private fun toggleListaBeats() {
        val isVisible = binding.containerListaBeats.visibility == View.VISIBLE
        AnimationHelper.toggleVisibility(
            binding.containerListaBeats,
            !isVisible,
            AnimationHelper.AnimationType.FADE
        )
        if (!isVisible) {
            // Recargar beats al abrir (precios en CLP desde Supabase)
            viewModel.getAllBeats()
        }
    }

    private fun toggleFormEliminar() {
        val isVisible = binding.formEliminarBeat.visibility == View.VISIBLE
        AnimationHelper.toggleVisibility(
            binding.formEliminarBeat,
            !isVisible,
            AnimationHelper.AnimationType.SLIDE
        )
    }

    private fun hideFormEliminar() {
        binding.formEliminarBeat.visibility = View.GONE
        limpiarFormularioEliminar()
    }

    private fun limpiarFormularioEliminar() {
        binding.editIdEliminar.text?.clear()
        binding.inputIdEliminar.error = null
        binding.cardBeatInfo.visibility = View.GONE
        beatToDelete = null
    }

    private fun buscarBeatParaEliminar() {
        // Verificar permisos (solo admin)
        val userSession = UserSession(requireContext())
        if (!userSession.isAdmin()) {
            showMessage("No tienes permiso para eliminar beats")
            return
        }

        val idText = binding.editIdEliminar.text.toString().trim()

        if (idText.isEmpty()) {
            binding.inputIdEliminar.error = "Ingresa el ID del beat"
            return
        }

        val beatId = idText.toIntOrNull()
        if (beatId == null || beatId <= 0) {
            binding.inputIdEliminar.error = "Ingresa un ID válido"
            return
        }

        binding.inputIdEliminar.error = null
        // Buscar el beat por ID
        viewModel.getBeatById(beatId)
    }

    private fun mostrarBeatParaEliminar(beat: com.grupo8.fullsound.model.Beat) {
        beatToDelete = beat
        binding.txtTituloEliminar.text = beat.titulo
        binding.txtArtistaEliminar.text = "Artista: ${beat.artista}"
        binding.txtBpmEliminar.text = "BPM: ${beat.bpm}"
        // Cargar imagen desde drawable usando beat.imagenPath con fallback
        val ctx = requireContext()
        var imageResId = 0
        try {
            if (!beat.imagenPath.isNullOrEmpty()) {
                val possibleFile = java.io.File(beat.imagenPath)
                if (possibleFile.exists()) {
                    val bmp = android.graphics.BitmapFactory.decodeFile(possibleFile.absolutePath)
                    if (bmp != null) {
                        binding.imgBeatEliminar.setImageBitmap(bmp)
                    } else {
                        imageResId = ctx.resources.getIdentifier(beat.imagenPath, "drawable", ctx.packageName)
                    }
                } else {
                    imageResId = ctx.resources.getIdentifier(beat.imagenPath, "drawable", ctx.packageName)
                }
            }
        } catch (e: Exception) {
            imageResId = 0
        }
        if (imageResId == 0 && binding.imgBeatEliminar.drawable == null) {
            val idx = ((beat.id % 5) + 5) % 5 + 1
            val fallbackName = "img$idx"
            imageResId = ctx.resources.getIdentifier(fallbackName, "drawable", ctx.packageName)
        }
        if (imageResId != 0 && binding.imgBeatEliminar.drawable == null) {
            binding.imgBeatEliminar.setImageResource(imageResId)
        } else if (binding.imgBeatEliminar.drawable == null) {
            binding.imgBeatEliminar.setImageResource(R.drawable.image)
        }
        // Mostrar con animación
        AnimationHelper.scaleUp(binding.cardBeatInfo)
    }

    private fun confirmarEliminarBeat() {
        // Verificar permisos (solo admin)
        val userSession = UserSession(requireContext())
        if (!userSession.isAdmin()) {
            showMessage("No tienes permiso para eliminar beats")
            return
        }

        beatToDelete?.let { beat ->
            android.util.Log.d("BeatsFragment", "Eliminando beat ID: ${beat.id} - ${beat.titulo}")

            // Eliminar el beat (Supabase + caché local)
            viewModel.deleteBeat(beat)
            hideFormEliminar()
            showMessage("Beat eliminado exitosamente")

            // Recargar lista de beats
            viewModel.getAllBeats()
        } ?: showMessage("No hay beat seleccionado para eliminar")
    }

    // MÉTODOS PARA ACTUALIZAR BEAT
    private fun toggleFormActualizar() {
        val isVisible = binding.formActualizarBeat.visibility == View.VISIBLE
        AnimationHelper.toggleVisibility(
            binding.formActualizarBeat,
            !isVisible,
            AnimationHelper.AnimationType.SLIDE
        )
    }

    private fun hideFormActualizar() {
        binding.formActualizarBeat.visibility = View.GONE
        limpiarFormularioActualizar()
    }

    private fun limpiarFormularioActualizar() {
        binding.editIdActualizar.text?.clear()
        binding.inputIdActualizar.error = null
        binding.cardBeatActualizarInfo.visibility = View.GONE
        binding.layoutCamposActualizar.visibility = View.GONE
        binding.editTituloActualizar.text?.clear()
        binding.editArtistaActualizar.text?.clear()
        binding.editBpmActualizar.text?.clear()
        binding.editGeneroActualizar.text?.clear()
        binding.editPrecioActualizar.text?.clear()
        binding.inputGeneroActualizar.error = null
        binding.inputPrecioActualizar.error = null
        beatToUpdate = null
    }

    private fun buscarBeatParaActualizar() {
        // Verificar permisos (solo admin)
        val userSession = UserSession(requireContext())
        if (!userSession.isAdmin()) {
            showMessage("No tienes permiso para actualizar beats")
            return
        }

        val idText = binding.editIdActualizar.text.toString().trim()

        if (idText.isEmpty()) {
            binding.inputIdActualizar.error = "Ingresa el ID del beat"
            return
        }

        val beatId = idText.toIntOrNull()
        if (beatId == null || beatId <= 0) {
            binding.inputIdActualizar.error = "Ingresa un ID válido"
            return
        }

        binding.inputIdActualizar.error = null
        // Buscar el beat por ID
        viewModel.getBeatById(beatId)
    }

    private fun mostrarBeatParaActualizar(beat: com.grupo8.fullsound.model.Beat) {
        beatToUpdate = beat

        // Mostrar información actual
        binding.txtTituloActualizarActual.text = "Título: ${beat.titulo}"
        binding.txtArtistaActualizarActual.text = "Artista: ${beat.artista}"
        binding.txtBpmActualizarActual.text = "BPM: ${beat.bpm}"
        binding.txtGeneroActualizarActual.text = "Género: ${beat.genero ?: "No especificado"}"
        binding.txtPrecioActualizarActual.text = "Precio: ${com.grupo8.fullsound.utils.FormatUtils.formatClp(beat.precio)}"

        // Cargar imagen desde URL de Supabase usando Coil
        val imageUrl = beat.imagenPath

        if (!imageUrl.isNullOrBlank() && (imageUrl.startsWith("http://") || imageUrl.startsWith("https://"))) {
            // Es una URL de Supabase, cargar con Coil
            android.util.Log.d("BeatsFragment", "Cargando imagen de actualizar desde Supabase: $imageUrl")
            binding.imgBeatActualizar.load(imageUrl) {
                crossfade(true)
                placeholder(R.drawable.image)
                error(R.drawable.image)
                listener(
                    onSuccess = { _, _ ->
                        android.util.Log.d("BeatsFragment", "Imagen cargada para actualizar")
                    },
                    onError = { _, result ->
                        android.util.Log.e("BeatsFragment", "Error cargando imagen: ${result.throwable.message}")
                    }
                )
            }
        } else {
            // Fallback a placeholder
            binding.imgBeatActualizar.setImageResource(R.drawable.image)
        }

        AnimationHelper.scaleUp(binding.cardBeatActualizarInfo)

        // Mostrar campos de edición con valores actuales
        binding.editTituloActualizar.setText(beat.titulo)
        binding.editArtistaActualizar.setText(beat.artista)
        binding.editBpmActualizar.setText(beat.bpm.toString())
        binding.editGeneroActualizar.setText(beat.genero ?: "", false)
        binding.editPrecioActualizar.setText(beat.precio.toInt().toString())
        AnimationHelper.fadeIn(binding.layoutCamposActualizar)
    }

    private fun confirmarActualizarBeat() {
        // Verificar permisos (solo admin)
        val userSession = UserSession(requireContext())
        if (!userSession.isAdmin()) {
            showMessage("No tienes permiso para actualizar beats")
            return
        }

        val beat = beatToUpdate
        if (beat == null) {
            showMessage("No hay beat seleccionado para actualizar")
            return
        }

        // Validar campos
        val titulo = binding.editTituloActualizar.text.toString().trim()
        val artista = binding.editArtistaActualizar.text.toString().trim()
        val bpmText = binding.editBpmActualizar.text.toString().trim()
        val genero = binding.editGeneroActualizar.text.toString().trim()
        val precioText = binding.editPrecioActualizar.text.toString().trim()

        var isValid = true

        if (titulo.isEmpty()) {
            binding.inputTituloActualizar.error = "El título es requerido"
            isValid = false
        } else {
            binding.inputTituloActualizar.error = null
        }

        if (artista.isEmpty()) {
            binding.inputArtistaActualizar.error = "El artista es requerido"
            isValid = false
        } else {
            binding.inputArtistaActualizar.error = null
        }

        val bpm = if (bpmText.isNotEmpty()) {
            bpmText.toIntOrNull()
        } else {
            beat.bpm
        }

        if (bpmText.isNotEmpty() && (bpm == null || bpm <= 0)) {
            binding.inputBpmActualizar.error = "Ingresa un BPM válido"
            isValid = false
        } else {
            binding.inputBpmActualizar.error = null
        }

        // Validar precio
        val precio = if (precioText.isNotEmpty()) {
            precioText.toDoubleOrNull()
        } else {
            beat.precio
        }

        if (precioText.isNotEmpty() && (precio == null || precio < 0)) {
            binding.inputPrecioActualizar.error = "Ingresa un precio válido"
            isValid = false
        } else {
            binding.inputPrecioActualizar.error = null
        }

        if (!isValid) {
            return
        }

        // Crear beat actualizado (mantener los demás campos)
        val beatActualizado = beat.copy(
            titulo = titulo,
            artista = artista,
            bpm = bpm,
            genero = genero.ifEmpty { beat.genero },
            precio = precio ?: beat.precio
        )

        android.util.Log.d("BeatsFragment", "Actualizando beat ID: ${beat.id} - $titulo")

        // Actualizar en la base de datos (Supabase + caché local)
        viewModel.updateBeat(beatActualizado)
        hideFormActualizar()
        showMessage("Beat actualizado exitosamente")

        // Recargar lista de beats
        viewModel.getAllBeats()
    }

    private fun hideFormCrear() {
        binding.formCrearBeat.visibility = View.GONE
        limpiarFormularioCrear()
    }

    private fun handleImageSelection(uri: Uri) {
        try {
            // Cargar la imagen completa
            val inputStream = requireContext().contentResolver.openInputStream(uri)
            val originalBitmap = BitmapFactory.decodeStream(inputStream)
            inputStream?.close()

            if (originalBitmap == null) {
                showMessage("Error al cargar la imagen")
                return
            }

            val width = originalBitmap.width
            val height = originalBitmap.height

            // Si la imagen no es cuadrada, recortarla al centro
            val croppedBitmap = if (width != height) {
                val size = minOf(width, height)
                val x = (width - size) / 2
                val y = (height - size) / 2

                showMessage("Imagen recortada a formato cuadrado (${size}x${size})")
                Bitmap.createBitmap(originalBitmap, x, y, size, size)
            } else {
                showMessage("Imagen cuadrada detectada (${width}x${height})")
                originalBitmap
            }

            // Guardar la imagen recortada en un archivo temporal
            val tempFile = File(requireContext().cacheDir, "temp_image_${System.currentTimeMillis()}.jpg")
            FileOutputStream(tempFile).use { out ->
                croppedBitmap.compress(Bitmap.CompressFormat.JPEG, 90, out)
            }

            // Crear URI desde el archivo temporal
            selectedImageUri = Uri.fromFile(tempFile)

            // Obtener el nombre del archivo original
            val fileName = getFileName(uri) ?: "imagen_seleccionada.jpg"
            binding.txtImagenSeleccionada.text = fileName

            // Liberar memoria
            if (croppedBitmap != originalBitmap) {
                originalBitmap.recycle()
            }

        } catch (e: Exception) {
            showMessage("Error al seleccionar la imagen: ${e.message}")
            e.printStackTrace()
        }
    }

    private fun handleAudioSelection(uri: Uri) {
        try {
            val retriever = MediaMetadataRetriever()
            retriever.setDataSource(requireContext(), uri)

            // Obtener duración en milisegundos
            val durationStr = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
            val durationMs = durationStr?.toLongOrNull() ?: 0L
            val durationMinutes = durationMs / 1000 / 60

            retriever.release()

            // Validar que el audio sea menor a 10 minutos
            if (durationMinutes >= 10) {
                showMessage("El audio debe ser menor a 10 minutos (Duración: $durationMinutes min)")
                return
            }

            // Guardar la URI seleccionada
            selectedAudioUri = uri

            // Obtener el nombre del archivo
            val fileName = getFileName(uri) ?: "audio_seleccionado.mp3"
            binding.txtAudioSeleccionado.text = fileName

            showMessage("Audio seleccionado: $fileName (${durationMinutes}min)")
        } catch (e: Exception) {
            showMessage("Error al seleccionar el audio: ${e.message}")
            e.printStackTrace()
        }
    }

    private fun getFileName(uri: Uri): String? {
        var fileName: String? = null
        requireContext().contentResolver.query(uri, null, null, null, null)?.use { cursor ->
            val nameIndex = cursor.getColumnIndex(android.provider.OpenableColumns.DISPLAY_NAME)
            if (cursor.moveToFirst() && nameIndex >= 0) {
                fileName = cursor.getString(nameIndex)
            }
        }
        return fileName
    }

    private fun guardarNuevoBeat() {
        val titulo = binding.editTitulo.text.toString().trim()
        val artista = binding.editArtista.text.toString().trim()
        val bpmText = binding.editBpm.text.toString().trim()
        val genero = binding.editGenero.text.toString().trim()
        val precioText = binding.editPrecio.text.toString().trim()

        // Validar campos obligatorios
        var isValid = true

        if (titulo.isEmpty()) {
            binding.inputTitulo.error = "El título es requerido"
            isValid = false
        } else {
            binding.inputTitulo.error = null
        }

        if (artista.isEmpty()) {
            binding.inputArtista.error = "El artista es requerido"
            isValid = false
        } else {
            binding.inputArtista.error = null
        }

        val bpm = if (bpmText.isNotEmpty()) {
            bpmText.toIntOrNull()
        } else {
            null
        }

        if (bpmText.isNotEmpty() && (bpm == null || bpm <= 0)) {
            binding.inputBpm.error = "Ingresa un BPM válido"
            isValid = false
        } else {
            binding.inputBpm.error = null
        }

        // Validar precio
        val precio = if (precioText.isNotEmpty()) {
            precioText.toDoubleOrNull()
        } else {
            10000.0 // Precio por defecto
        }

        if (precioText.isNotEmpty() && (precio == null || precio < 0)) {
            binding.inputPrecio.error = "Ingresa un precio válido"
            isValid = false
        } else {
            binding.inputPrecio.error = null
        }

        if (!isValid) {
            return
        }

        // Mostrar indicador de progreso
        binding.btnGuardarBeat.isEnabled = false
        binding.btnGuardarBeat.text = "Subiendo archivos..."

        // Usar coroutine para operaciones asíncronas
        lifecycleScope.launch {
            try {
                // Ejecutar test de diagnóstico primero
                android.util.Log.d("BeatsFragment", "Ejecutando test de Storage...")
                val testResult = storageRepository.testStorageConnection()
                android.util.Log.d("BeatsFragment", "Resultado del test:\n$testResult")

                // Subir imagen a Supabase Storage si se seleccionó
                var imageUrl: String? = null
                if (selectedImageUri != null) {
                    showMessage("Subiendo imagen...")
                    android.util.Log.d("BeatsFragment", "Iniciando subida de imagen...")
                    imageUrl = storageRepository.uploadImage(
                        uri = selectedImageUri!!,
                        fileName = "beat_${titulo.replace(" ", "_")}_${System.currentTimeMillis()}.jpg"
                    )

                    if (imageUrl == null) {
                        android.util.Log.e("BeatsFragment", "Falló la subida de imagen")
                        showMessage("Error al subir imagen. Revisa los logs para más detalles.")
                    } else {
                        android.util.Log.d("BeatsFragment", "Imagen subida: $imageUrl")
                        showMessage("Imagen subida exitosamente")
                    }
                } else {
                    android.util.Log.d("BeatsFragment", "No se seleccionó imagen")
                }

                // Subir audio a Supabase Storage si se seleccionó
                var audioUrl: String? = null
                if (selectedAudioUri != null) {
                    showMessage("Subiendo audio...")
                    android.util.Log.d("BeatsFragment", "Iniciando subida de audio...")
                    audioUrl = storageRepository.uploadAudio(
                        uri = selectedAudioUri!!,
                        fileName = "beat_${titulo.replace(" ", "_")}_${System.currentTimeMillis()}.mp3"
                    )

                    if (audioUrl == null) {
                        android.util.Log.e("BeatsFragment", "Falló la subida de audio")
                        showMessage("Error al subir audio. Revisa los logs para más detalles.")
                    } else {
                        android.util.Log.d("BeatsFragment", "Audio subido: $audioUrl")
                        showMessage("Audio subida exitosamente")
                    }
                } else {
                    android.util.Log.d("BeatsFragment", "No se seleccionó audio")
                }

                // Crear el beat con las URLs de Supabase Storage
                val nuevoBeat = com.grupo8.fullsound.model.Beat(
                    id = 0, // Supabase generará el ID
                    titulo = titulo,
                    artista = artista,
                    bpm = bpm,
                    precio = precio ?: 10000.0,
                    genero = genero.ifEmpty { null },
                    imagenPath = imageUrl, // URL de Supabase Storage
                    mp3Path = audioUrl,    // URL de Supabase Storage
                    estado = "DISPONIBLE"
                )

                android.util.Log.d("BeatsFragment", "Creando beat: $titulo por $artista")
                android.util.Log.d("BeatsFragment", "Precio: ${precio ?: 10000.0} CLP")
                android.util.Log.d("BeatsFragment", "Género: ${genero.ifEmpty { "No especificado" }}")
                android.util.Log.d("BeatsFragment", "Imagen URL: $imageUrl")
                android.util.Log.d("BeatsFragment", "Audio URL: $audioUrl")

                // Guardar en la base de datos (Supabase + caché local)
                viewModel.insertBeat(nuevoBeat)

                // Limpiar formulario y ocultarlo
                hideFormCrear()
                showMessage("Beat creado exitosamente")

                // Recargar lista de beats
                viewModel.getAllBeats()

            } catch (e: Exception) {
                showMessage("Error al guardar el beat: ${e.message}")
                android.util.Log.e("BeatsFragment", "Error al guardar beat", e)
            } finally {
                // Restaurar botón
                binding.btnGuardarBeat.isEnabled = true
                binding.btnGuardarBeat.text = getString(R.string.guardar_beat)
            }
        }
    }

    private fun copyFileToInternalStorage(uri: Uri, fileName: String, subFolder: String): String {
        // Crear directorio si no existe
        val folder = File(requireContext().filesDir, subFolder)
        if (!folder.exists()) {
            folder.mkdirs()
        }

        // Archivo de destino
        val destinationFile = File(folder, fileName)

        // Copiar el archivo
        requireContext().contentResolver.openInputStream(uri)?.use { input ->
            FileOutputStream(destinationFile).use { output ->
                input.copyTo(output)
            }
        }

        // Retornar la ruta relativa
        return destinationFile.absolutePath
    }

    private fun limpiarFormularioCrear() {
        binding.editTitulo.text?.clear()
        binding.editArtista.text?.clear()
        binding.editBpm.text?.clear()
        binding.editGenero.text?.clear()
        binding.editPrecio.setText("10000")

        binding.inputTitulo.error = null
        binding.inputArtista.error = null
        binding.inputBpm.error = null
        binding.inputGenero.error = null
        binding.inputPrecio.error = null

        // Limpiar selección de archivos
        selectedImageUri = null
        selectedAudioUri = null

        binding.txtImagenSeleccionada.text = "No se ha seleccionado ninguna imagen"
        binding.txtAudioSeleccionado.text = "No se ha seleccionado ningún audio"
    }

    private fun logout() {
        // Limpiar la sesión
        val userSession = UserSession(requireContext())
        userSession.logout()

        // Navegar al login
        findNavController().navigate(R.id.loginFragment)
    }

    private fun setupObservers() {
        android.util.Log.d("BeatsFragment", "Configurando observadores de ViewModel...")

        // Observar lista de beats
        viewModel.beatsResult.observe(viewLifecycleOwner) { result ->
            android.util.Log.d("BeatsFragment", "Resultado de beats recibido: ${result.javaClass.simpleName}")

            when (result) {
                is Resource.Loading -> {
                    android.util.Log.d("BeatsFragment", "Cargando beats...")
                    // Mostrar cargando
                }
                is Resource.Success -> {
                    val beats = result.data ?: emptyList()
                    android.util.Log.d("BeatsFragment", "Beats cargados exitosamente: ${beats.size}")

                    if (beats.isNotEmpty()) {
                        android.util.Log.d("BeatsFragment", "Lista de beats:")
                        beats.forEachIndexed { index, beat ->
                            android.util.Log.d("BeatsFragment", "   ${index + 1}. ${beat.titulo} - ${beat.artista} (\$${beat.precio})")
                        }
                    } else {
                        android.util.Log.w("BeatsFragment", "La lista de beats está vacía")
                    }

                    // Actualizar el total de beats en el header
                    val userSession = UserSession(requireContext())
                    val isAdmin = userSession.isAdmin()
                    if (isAdmin) {
                        binding.txtTituloBeats.text = "Beats (${beats.size})"
                    } else {
                        binding.txtTituloBeats.text = "Catálogo de beats (${beats.size})"
                    }
                    // Actualizar el total en la sección de leer
                    binding.txtTotalBeats.text = "Total de Beats: ${beats.size}"

                    // Actualizar el adaptador con los beats
                    android.util.Log.d("BeatsFragment", "Actualizando adaptador con ${beats.size} beats...")
                    beatsAdapter.submitList(beats)

                    // Mostrar/ocultar mensaje de "no hay beats"
                    if (beats.isEmpty()) {
                        android.util.Log.d("BeatsFragment", "Mostrando mensaje 'no hay beats'")
                        binding.recyclerBeats.visibility = View.GONE
                        binding.txtNoBeats.visibility = View.VISIBLE
                    } else {
                        android.util.Log.d("BeatsFragment", "Mostrando RecyclerView con beats")
                        binding.recyclerBeats.visibility = View.VISIBLE
                        binding.txtNoBeats.visibility = View.GONE
                    }
                }
                is Resource.Error -> {
                    android.util.Log.e("BeatsFragment", "❌ Error al cargar beats: ${result.message}")
                    // Mostrar error
                    showMessage(result.message ?: "Error al cargar beats")
                    binding.recyclerBeats.visibility = View.GONE
                    binding.txtNoBeats.visibility = View.VISIBLE
                }
            }
        }

        // Observar resultado de operaciones individuales
        viewModel.beatResult.observe(viewLifecycleOwner) { result ->
            when (result) {
                is Resource.Success -> {
                    result.data?.let { beat ->
                        // Si estamos buscando un beat para eliminar, mostrarlo
                        if (binding.formEliminarBeat.visibility == View.VISIBLE) {
                            mostrarBeatParaEliminar(beat)
                        }
                        // Si estamos buscando un beat para actualizar, mostrarlo
                        else if (binding.formActualizarBeat.visibility == View.VISIBLE) {
                            mostrarBeatParaActualizar(beat)
                        }
                        else {
                            showMessage("Operación exitosa")
                            // Recargar según rol: admin ve USD, usuarios CLP
                            // Recargar beats desde Supabase (precios en CLP)
                            viewModel.getAllBeats()
                        }
                     }
                }
                is Resource.Error -> {
                    showMessage(result.message ?: "Error en operación")
                    // Si estamos buscando un beat para eliminar y no se encuentra
                    if (binding.formEliminarBeat.visibility == View.VISIBLE) {
                        binding.cardBeatInfo.visibility = View.GONE
                        beatToDelete = null
                    }
                    // Si estamos buscando un beat para actualizar y no se encuentra
                    else if (binding.formActualizarBeat.visibility == View.VISIBLE) {
                        binding.cardBeatActualizarInfo.visibility = View.GONE
                        binding.layoutCamposActualizar.visibility = View.GONE
                        beatToUpdate = null
                    }
                }
                else -> {}
            }
        }

        // Observar resultado de eliminación
        viewModel.deleteResult.observe(viewLifecycleOwner) { result ->
            when (result) {
                is Resource.Success -> {
                    showMessage(result.data ?: "Eliminado exitosamente")
                    // Recargar la lista desde Supabase (precios en CLP)
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
