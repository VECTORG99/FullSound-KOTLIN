package com.grupo8.fullsound.ui.beats

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.grupo8.fullsound.model.Beat
import com.grupo8.fullsound.databinding.ItemBeatBinding
import java.io.File

class BeatsAdapter(
    private val onAddToCarrito: (Beat) -> Unit,
    private val onComprar: (Beat) -> Unit
) : ListAdapter<Beat, BeatsAdapter.BeatViewHolder>(BeatDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BeatViewHolder {
        val binding = ItemBeatBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return BeatViewHolder(binding, onAddToCarrito, onComprar)
    }

    override fun onBindViewHolder(holder: BeatViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class BeatViewHolder(
        private val binding: ItemBeatBinding,
        private val onAddToCarrito: (Beat) -> Unit,
        private val onComprar: (Beat) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        private var mediaPlayer: android.media.MediaPlayer? = null
        private var isPlaying = false

        fun bind(beat: Beat) {
            binding.apply {
                // Datos del beat
                txtId.text = beat.id.toString()
                txtTitulo.text = beat.titulo
                txtArtista.text = beat.artista
                txtBpm.text = beat.bpm.toString()

                // Extraer solo el nombre del archivo de audio (proteger si mp3Path está vacío)
                val audioFileName = if (beat.mp3Path.isNullOrBlank()) "" else File(beat.mp3Path).name
                txtAudioNombre.text = audioFileName

                // Asegurar que el ImageView empiece con el placeholder
                imgBeat.setImageResource(com.grupo8.fullsound.R.drawable.image)

                // Cargar imagen desde drawable usando el nombre en beat.imagenPath
                val ctx = binding.root.context
                var imageResId = 0
                var bitmapLoaded = false
                try {
                    if (!beat.imagenPath.isNullOrBlank()) {
                        // Si la ruta corresponde a un archivo existente, cargar desde archivo
                        val possibleFile = java.io.File(beat.imagenPath)
                        if (possibleFile.exists()) {
                            val bmp = android.graphics.BitmapFactory.decodeFile(possibleFile.absolutePath)
                            if (bmp != null) {
                                imgBeat.setImageBitmap(bmp)
                                bitmapLoaded = true
                                android.util.Log.d("BeatsAdapter", "Loaded bitmap from file: ${possibleFile.absolutePath}")
                            } else {
                                // intentar como drawable
                                imageResId = ctx.resources.getIdentifier(beat.imagenPath, "drawable", ctx.packageName)
                                android.util.Log.d("BeatsAdapter", "Tried as drawable: ${beat.imagenPath}, resId=$imageResId")
                            }
                        } else {
                            // intentar como drawable por nombre
                            imageResId = ctx.resources.getIdentifier(beat.imagenPath, "drawable", ctx.packageName)
                            android.util.Log.d("BeatsAdapter", "Tried as drawable (no file): ${beat.imagenPath}, resId=$imageResId")
                        }
                    }
                } catch (e: Exception) {
                    imageResId = 0
                    android.util.Log.w("BeatsAdapter", "Error loading image for beat ${beat.id}: ${e.message}")
                }

                // Si no cargó bitmap desde archivo, aplicar drawable o fallback
                if (!bitmapLoaded) {
                    // Fallback cíclico a img1..img5 si no existe imagen definida o recurso no encontrado
                    if (imageResId == 0) {
                        val idx = ((beat.id % 5) + 5) % 5 + 1 // garantiza 1..5
                        val fallbackName = "img$idx"
                        imageResId = ctx.resources.getIdentifier(fallbackName, "drawable", ctx.packageName)
                        android.util.Log.d("BeatsAdapter", "Fallback drawable: $fallbackName -> resId=$imageResId")
                    }

                    // Aplicar drawable si se encontró, sino placeholder
                    if (imageResId != 0) {
                        imgBeat.setImageResource(imageResId)
                        android.util.Log.d("BeatsAdapter", "Set drawable resId=$imageResId for beat ${beat.id}")
                    } else {
                        imgBeat.setImageResource(com.grupo8.fullsound.R.drawable.image)
                        android.util.Log.d("BeatsAdapter", "Used placeholder for beat ${beat.id}")
                    }
                }

                // Precio: formatear en CLP sin decimales y con redondeo HALF_UP
                try {
                    val nf = java.text.NumberFormat.getCurrencyInstance(java.util.Locale("es", "CL"))
                    nf.maximumFractionDigits = 0
                    nf.minimumFractionDigits = 0
                    nf.roundingMode = java.math.RoundingMode.HALF_UP
                    txtPrecio.text = nf.format(beat.precio)
                } catch (e: Exception) {
                    // Fallback con formato simple y locale explícito
                    txtPrecio.text = String.format(java.util.Locale.US, "$%.0f", Math.round(beat.precio))
                }

                // Configurar botón play/pause
                btnPlay.setOnClickListener {
                    if (isPlaying) {
                        pauseAudio()
                    } else {
                        playAudio(beat)
                    }
                }

                // Si se recicla, nos aseguramos de que el icono esté en estado inicial
                updatePlayIcon()

                // Click listeners para los botones
                btnAddCarrito.setOnClickListener {
                    onAddToCarrito(beat)
                }

                btnComprar.setOnClickListener {
                    onComprar(beat)
                }
            }
        }

        private fun playAudio(beat: Beat) {
            try {
                // Liberar si ya existía
                mediaPlayer?.release()
                mediaPlayer = android.media.MediaPlayer()

                val ctx = binding.root.context

                // Determinar fuente: si mp3Path apunta a un archivo existente, usar decodeFile
                val possibleFile = java.io.File(beat.mp3Path)
                if (possibleFile.exists()) {
                    mediaPlayer?.setDataSource(possibleFile.absolutePath)
                } else {
                    // Intentar cargar desde raw por nombre (sin extensión)
                    val resId = ctx.resources.getIdentifier(beat.mp3Path, "raw", ctx.packageName)
                    if (resId != 0) {
                        val afd = ctx.resources.openRawResourceFd(resId)
                        mediaPlayer?.setDataSource(afd.fileDescriptor, afd.startOffset, afd.length)
                        afd.close()
                    } else {
                        // Fallback: intentar nombres beat1..beat5
                        val fallbackIdx = ((beat.id % 5) + 5) % 5 + 1
                        val fallbackName = "beat$fallbackIdx"
                        val fallbackRes = ctx.resources.getIdentifier(fallbackName, "raw", ctx.packageName)
                        if (fallbackRes != 0) {
                            val afd2 = ctx.resources.openRawResourceFd(fallbackRes)
                            mediaPlayer?.setDataSource(afd2.fileDescriptor, afd2.startOffset, afd2.length)
                            afd2.close()
                        } else {
                            // No hay fuente válida
                            android.util.Log.w("BeatsAdapter", "No audio source for beat ${beat.id}")
                            mediaPlayer?.release()
                            mediaPlayer = null
                            return
                        }
                    }
                }

                mediaPlayer?.prepare()
                mediaPlayer?.start()
                isPlaying = true
                updatePlayIcon()

                mediaPlayer?.setOnCompletionListener {
                    isPlaying = false
                    updatePlayIcon()
                    mediaPlayer?.release()
                    mediaPlayer = null
                }

            } catch (e: Exception) {
                android.util.Log.e("BeatsAdapter", "Error playing audio for beat ${beat.id}: ${e.message}")
                mediaPlayer?.release()
                mediaPlayer = null
                isPlaying = false
                updatePlayIcon()
            }
        }

        private fun pauseAudio() {
            mediaPlayer?.let {
                if (it.isPlaying) {
                    it.pause()
                    isPlaying = false
                    updatePlayIcon()
                }
            }
        }

        private fun updatePlayIcon() {
            val playIcon = if (isPlaying) android.R.drawable.ic_media_pause else android.R.drawable.ic_media_play
            binding.btnPlay.setImageResource(playIcon)
        }

        fun releasePlayer() {
            mediaPlayer?.release()
            mediaPlayer = null
            isPlaying = false
        }
    }

    class BeatDiffCallback : DiffUtil.ItemCallback<Beat>() {
        override fun areItemsTheSame(oldItem: Beat, newItem: Beat): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Beat, newItem: Beat): Boolean {
            return oldItem == newItem
        }
    }

    override fun onViewRecycled(holder: BeatViewHolder) {
        super.onViewRecycled(holder)
        holder.releasePlayer()
    }
}
