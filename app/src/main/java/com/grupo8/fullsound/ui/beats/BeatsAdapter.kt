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

        fun bind(beat: Beat) {
            binding.apply {
                // Datos del beat
                txtId.text = beat.id.toString()
                txtTitulo.text = beat.titulo
                txtArtista.text = beat.artista
                txtBpm.text = beat.bpm.toString()

                // Extraer solo el nombre del archivo de audio
                val audioFileName = File(beat.mp3Path).name
                txtAudioNombre.text = audioFileName

                // Asegurar que el ImageView empiece con el placeholder
                imgBeat.setImageResource(com.grupo8.fullsound.R.drawable.image)

                // Cargar imagen desde drawable usando el nombre en beat.imagenPath
                val ctx = binding.root.context
                var imageResId = 0
                var bitmapLoaded = false
                try {
                    if (!beat.imagenPath.isNullOrEmpty()) {
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

                // Click listeners para los botones
                btnAddCarrito.setOnClickListener {
                    onAddToCarrito(beat)
                }

                btnComprar.setOnClickListener {
                    onComprar(beat)
                }
            }
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
}
