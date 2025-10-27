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
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.grupo8.fullsound.R
import com.grupo8.fullsound.data.local.AppDatabase
import com.grupo8.fullsound.data.repositories.BeatRepository
import com.grupo8.fullsound.databinding.FragmentBeatsBinding
import com.grupo8.fullsound.utils.Resource
import com.grupo8.fullsound.utils.UserSession
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
    private val beatsAdapter = BeatsAdapter()

    // URIs de archivos seleccionados
    private var selectedImageUri: Uri? = null
    private var selectedAudioUri: Uri? = null

    // Beat seleccionado para eliminar
    private var beatToDelete: com.grupo8.fullsound.data.models.Beat? = null

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

        // Cargar todos los beats al iniciar
        viewModel.getAllBeats()
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
        binding.btnNavBeats.setOnClickListener {
            // Ya estamos en Beats
        }

        binding.btnNavCarrito.setOnClickListener {
            // TODO: Navegar al carrito
            showMessage("Navegando al carrito...")
        }

        // Cards CRUD
        binding.cardCrear.setOnClickListener {
            toggleFormCrear()
        }

        binding.cardLeer.setOnClickListener {
            toggleListaBeats()
        }

        binding.cardActualizar.setOnClickListener {
            // TODO: Navegar a actualizar beat
            showMessage("Actualizar beat")
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
    }

    private fun toggleFormCrear() {
        if (binding.formCrearBeat.visibility == View.GONE) {
            binding.formCrearBeat.visibility = View.VISIBLE
        } else {
            binding.formCrearBeat.visibility = View.GONE
        }
    }

    private fun toggleListaBeats() {
        if (binding.containerListaBeats.visibility == View.GONE) {
            binding.containerListaBeats.visibility = View.VISIBLE
            // Recargar beats al abrir
            viewModel.getAllBeats()
        } else {
            binding.containerListaBeats.visibility = View.GONE
        }
    }

    private fun toggleFormEliminar() {
        if (binding.formEliminarBeat.visibility == View.GONE) {
            binding.formEliminarBeat.visibility = View.VISIBLE
        } else {
            binding.formEliminarBeat.visibility = View.GONE
        }
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

    private fun mostrarBeatParaEliminar(beat: com.grupo8.fullsound.data.models.Beat) {
        beatToDelete = beat
        binding.cardBeatInfo.visibility = View.VISIBLE
        binding.txtTituloEliminar.text = beat.titulo
        binding.txtArtistaEliminar.text = "Artista: ${beat.artista}"
        binding.txtBpmEliminar.text = "BPM: ${beat.bpm}"
        // Usar siempre el placeholder
        binding.imgBeatEliminar.setImageResource(R.drawable.image)
    }

    private fun confirmarEliminarBeat() {
        beatToDelete?.let { beat ->
            // Eliminar el beat
            viewModel.deleteBeat(beat)
            hideFormEliminar()
        } ?: showMessage("No hay beat seleccionado para eliminar")
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
            val nuevoBeat = com.grupo8.fullsound.data.models.Beat(
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
                    binding.txtTituloBeats.text = "Beats (${beats.size})"
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
                        } else {
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
