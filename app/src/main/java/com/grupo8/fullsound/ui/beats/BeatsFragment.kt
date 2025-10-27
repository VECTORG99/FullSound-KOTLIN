package com.grupo8.fullsound.ui.beats

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
import java.io.File
import java.io.FileOutputStream

class BeatsFragment : Fragment() {

    private var _binding: FragmentBeatsBinding? = null
    private val binding get() = _binding!!

    private val viewModel: BeatsViewModel by viewModels {
        val database = AppDatabase.getInstance(requireContext())
        val beatRepository = BeatRepository(database.beatDao())
        BeatsViewModelFactory(beatRepository)
    }

    // Adaptador para la lista de beats
    private val beatsAdapter = BeatsAdapter(
        onAddToCarrito = { beat ->
            // TODO: Implementar agregar al carrito
            showMessage("Agregado al carrito: ${beat.titulo}")
        },
        onComprar = { beat ->
            // TODO: Implementar compra directa
            showMessage("Comprando: ${beat.titulo}")
        }
    )

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

        // Configurar el botón de retroceso para cerrar la app
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                // Finalizar la actividad (cerrar la app)
                requireActivity().finish()
            }
        })

        setupRecyclerView()
        setupObservers()
        setupClickListeners()

        // Configurar la UI según el rol (admin vs usuario común)
        configureUIBasedOnUserRole()

        // Insertar beats de ejemplo si no existen y cargar todos los beats
        viewModel.insertExampleBeats()
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

            // Mostrar el catálogo por defecto
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
            // Cargar beats para administración
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
            // Recargar beats al abrir
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
            // Eliminar el beat
            viewModel.deleteBeat(beat)
            hideFormEliminar()
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
        // Cargar imagen para la vista de actualización si existe
        val imgActualizarView = binding.root.findViewById<android.widget.ImageView>(R.id.img_beat_actualizar)
        val ctx2 = requireContext()
        var imageResId2 = 0
        try {
            if (!beat.imagenPath.isNullOrEmpty()) {
                val possibleFile = java.io.File(beat.imagenPath)
                if (possibleFile.exists()) {
                    val bmp = android.graphics.BitmapFactory.decodeFile(possibleFile.absolutePath)
                    if (bmp != null) {
                        imgActualizarView.setImageBitmap(bmp)
                    } else {
                        imageResId2 = ctx2.resources.getIdentifier(beat.imagenPath, "drawable", ctx2.packageName)
                    }
                } else {
                    imageResId2 = ctx2.resources.getIdentifier(beat.imagenPath, "drawable", ctx2.packageName)
                }
            }
        } catch (_: Exception) {
            imageResId2 = 0
        }
        if (imageResId2 == 0 && imgActualizarView.drawable == null) {
            val idx2 = ((beat.id % 5) + 5) % 5 + 1
            val fallbackName2 = "img$idx2"
            imageResId2 = ctx2.resources.getIdentifier(fallbackName2, "drawable", ctx2.packageName)
        }
        if (imageResId2 != 0 && imgActualizarView.drawable == null) {
            imgActualizarView.setImageResource(imageResId2)
        } else if (imgActualizarView.drawable == null) {
            imgActualizarView.setImageResource(R.drawable.image)
        }

        AnimationHelper.scaleUp(binding.cardBeatActualizarInfo)

        // Mostrar campos de edición con valores actuales
        binding.editTituloActualizar.setText(beat.titulo)
        binding.editArtistaActualizar.setText(beat.artista)
        binding.editBpmActualizar.setText(beat.bpm.toString())
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

        val bpm = bpmText.toIntOrNull()
        if (bpm == null || bpm <= 0) {
            binding.inputBpmActualizar.error = "Ingresa un BPM válido"
            isValid = false
        } else {
            binding.inputBpmActualizar.error = null
        }

        if (!isValid) {
            return
        }

        // Crear beat actualizado
        val beatActualizado = beat.copy(
            titulo = titulo,
            artista = artista,
            bpm = bpm!!
        )

        // Actualizar en la base de datos
        viewModel.updateBeat(beatActualizado)
        hideFormActualizar()
        showMessage("Beat actualizado exitosamente")
    }

    private fun hideFormCrear() {
        binding.formCrearBeat.visibility = View.GONE
        limpiarFormularioCrear()
    }

    private fun handleImageSelection(uri: Uri) {
        try {
            // Validar que la imagen sea cuadrada
            val inputStream = requireContext().contentResolver.openInputStream(uri)
            val options = BitmapFactory.Options().apply {
                inJustDecodeBounds = true
            }
            BitmapFactory.decodeStream(inputStream, null, options)
            inputStream?.close()

            val width = options.outWidth
            val height = options.outHeight

            if (width != height) {
                showMessage("La imagen debe ser cuadrada (mismo ancho y alto)")
                return
            }

            // Guardar la URI seleccionada
            selectedImageUri = uri

            // Obtener el nombre del archivo
            val fileName = getFileName(uri) ?: "imagen_seleccionada.jpg"
            binding.txtImagenSeleccionada.text = fileName

            showMessage("Imagen seleccionada: $fileName")
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

        // Validar campos
        if (titulo.isEmpty()) {
            binding.inputTitulo.error = "El título es requerido"
            return
        }
        if (artista.isEmpty()) {
            binding.inputArtista.error = "El artista es requerido"
            return
        }
        if (bpmText.isEmpty()) {
            binding.inputBpm.error = "El BPM es requerido"
            return
        }

        val bpm = bpmText.toIntOrNull()
        if (bpm == null || bpm <= 0) {
            binding.inputBpm.error = "Ingresa un BPM válido"
            return
        }

        // Validar que se hayan seleccionado imagen y audio
        if (selectedImageUri == null) {
            showMessage("Por favor selecciona una imagen")
            return
        }
        if (selectedAudioUri == null) {
            showMessage("Por favor selecciona un audio")
            return
        }

        try {
            // Copiar archivos al almacenamiento interno de la app
            val imagePath = copyFileToInternalStorage(selectedImageUri!!, "imagen_${System.currentTimeMillis()}.jpg", "images")
            val audioPath = copyFileToInternalStorage(selectedAudioUri!!, "audio_${System.currentTimeMillis()}.mp3", "audio")

            // Crear el beat
            val nuevoBeat = com.grupo8.fullsound.model.Beat(
                titulo = titulo,
                artista = artista,
                bpm = bpm,
                imagenPath = imagePath,
                mp3Path = audioPath
            )

            // Guardar en la base de datos
            viewModel.insertBeat(nuevoBeat)

            // Limpiar formulario y ocultarlo
            hideFormCrear()
            showMessage("Beat guardado exitosamente")
        } catch (e: Exception) {
            showMessage("Error al guardar el beat: ${e.message}")
            e.printStackTrace()
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

        binding.inputTitulo.error = null
        binding.inputArtista.error = null
        binding.inputBpm.error = null

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
        // Observar lista de beats
        viewModel.beatsResult.observe(viewLifecycleOwner) { result ->
            when (result) {
                is Resource.Loading -> {
                    // Mostrar cargando
                }
                is Resource.Success -> {
                    val beats = result.data ?: emptyList()
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
                    beatsAdapter.submitList(beats)

                    // Mostrar/ocultar mensaje de "no hay beats"
                    if (beats.isEmpty()) {
                        binding.recyclerBeats.visibility = View.GONE
                        binding.txtNoBeats.visibility = View.VISIBLE
                    } else {
                        binding.recyclerBeats.visibility = View.VISIBLE
                        binding.txtNoBeats.visibility = View.GONE
                    }
                }
                is Resource.Error -> {
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
