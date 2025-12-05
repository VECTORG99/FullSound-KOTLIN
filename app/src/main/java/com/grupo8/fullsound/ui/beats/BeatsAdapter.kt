package com.grupo8.fullsound.ui.beats

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.grupo8.fullsound.model.Beat
import com.grupo8.fullsound.databinding.ItemBeatBinding
import com.grupo8.fullsound.BuildConfig
import com.grupo8.fullsound.data.remote.fixer.ExchangeRateProvider
import coil.load
import coil.request.ErrorResult
import coil.request.SuccessResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.NumberFormat
import java.util.Locale

class BeatsAdapter(
    private val onAddToCarrito: (Beat) -> Unit,
    private val onComprar: (Beat) -> Unit,
    private val showId: Boolean = false
) : ListAdapter<Beat, BeatsAdapter.BeatViewHolder>(BeatDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BeatViewHolder {
        val binding = ItemBeatBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return BeatViewHolder(binding, onAddToCarrito, onComprar, showId)
    }

    override fun onBindViewHolder(holder: BeatViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class BeatViewHolder(
        private val binding: ItemBeatBinding,
        private val onAddToCarrito: (Beat) -> Unit,
        private val onComprar: (Beat) -> Unit,
        private val showId: Boolean
    ) : RecyclerView.ViewHolder(binding.root) {

        private var mediaPlayer: android.media.MediaPlayer? = null
        private var isPlaying = false

        fun bind(beat: Beat) {
            binding.apply {
                // Datos del beat
                txtTitulo.text = beat.titulo
                txtArtista.text = beat.artista ?: "Desconocido"
                txtGenero.text = beat.genero ?: "N/A"
                txtBpm.text = beat.bpm?.toString() ?: "N/A"

                // Mostrar u ocultar ID según el parámetro
                txtIdVisible.visibility = if (showId) android.view.View.VISIBLE else android.view.View.GONE
                txtIdVisible.text = "ID: ${beat.id}"
                txtId.text = beat.id.toString()

                // ID y nombre de audio ocultos (ya están con visibility="gone" en el XML)

                // DIAGNÓSTICO: Log detallado del beat
                android.util.Log.d("BeatsAdapter", "🔍 Beat ${beat.id}: ${beat.titulo}")
                android.util.Log.d("BeatsAdapter", "   imagenPath: ${beat.imagenPath}")
                android.util.Log.d("BeatsAdapter", "   mp3Path: ${beat.mp3Path}")
                android.util.Log.d("BeatsAdapter", "   audioDemoPath: ${beat.audioDemoPath}")

                // Cargar imagen desde URL de Supabase usando Coil
                val imageUrl = beat.imagenPath

                if (!imageUrl.isNullOrBlank() && (imageUrl.startsWith("http://") || imageUrl.startsWith("https://"))) {
                    // Es una URL de Supabase, cargar con Coil
                    android.util.Log.d("BeatsAdapter", "📸 Cargando imagen desde Supabase: $imageUrl")
                    imgBeat.load(imageUrl) {
                        crossfade(true)
                        placeholder(com.grupo8.fullsound.R.drawable.image)
                        error(com.grupo8.fullsound.R.drawable.image)
                        listener(
                            onSuccess = { _, _ ->
                                android.util.Log.d("BeatsAdapter", "✅ Imagen cargada desde Supabase para beat ${beat.id}")
                            },
                            onError = { _, result ->
                                android.util.Log.e("BeatsAdapter", "❌ Error cargando imagen: ${result.throwable.message}")
                            }
                        )
                    }
                } else {
                    // Fallback a placeholder local
                    android.util.Log.d("BeatsAdapter", "⚠️ No hay URL válida, usando placeholder para beat ${beat.id}")
                    imgBeat.setImageResource(com.grupo8.fullsound.R.drawable.image)
                }

                // Precios: Mostrar CLP (precio base) y USD (convertido)
                displayPrices(beat)

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

                val audioUrl = beat.mp3Path ?: beat.audioDemoPath

                if (!audioUrl.isNullOrBlank()) {
                    // Si es una URL de Supabase (HTTP/HTTPS), usarla directamente
                    if (audioUrl.startsWith("http://") || audioUrl.startsWith("https://")) {
                        android.util.Log.d("BeatsAdapter", "🎵 Reproduciendo audio desde Supabase: $audioUrl")
                        mediaPlayer?.setDataSource(audioUrl)
                    } else {
                        // Intentar como archivo local
                        val possibleFile = java.io.File(audioUrl)
                        if (possibleFile.exists()) {
                            android.util.Log.d("BeatsAdapter", "🎵 Reproduciendo audio desde archivo local")
                            mediaPlayer?.setDataSource(possibleFile.absolutePath)
                        } else {
                            // Intentar desde recursos raw
                            val ctx = binding.root.context
                            val resId = ctx.resources.getIdentifier(audioUrl, "raw", ctx.packageName)
                            if (resId != 0) {
                                android.util.Log.d("BeatsAdapter", "🎵 Reproduciendo audio desde raw resources")
                                val afd = ctx.resources.openRawResourceFd(resId)
                                mediaPlayer?.setDataSource(afd.fileDescriptor, afd.startOffset, afd.length)
                                afd.close()
                            } else {
                                // No hay fuente válida
                                android.util.Log.w("BeatsAdapter", "⚠️ No se encontró fuente de audio para beat ${beat.id}")
                                mediaPlayer?.release()
                                mediaPlayer = null
                                return
                            }
                        }
                    }
                } else {
                    // No hay URL de audio
                    android.util.Log.w("BeatsAdapter", "⚠️ No hay URL de audio para beat ${beat.id}")
                    mediaPlayer?.release()
                    mediaPlayer = null
                    return
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

        /**
         * Muestra los precios en CLP (principal) y USD (convertido)
         */
        private fun displayPrices(beat: Beat) {
            val context = binding.root.context

            // Formatear precio en CLP (sin decimales)
            val clpFormatted = try {
                val nf = NumberFormat.getNumberInstance(Locale("es", "CL"))
                nf.maximumFractionDigits = 0
                nf.minimumFractionDigits = 0
                "$${nf.format(beat.precio)} CLP"
            } catch (e: Exception) {
                "$${beat.precio.toInt()} CLP"
            }

            // Mostrar precio CLP inmediatamente
            binding.txtPrecioClp.text = clpFormatted
            binding.txtPrecioUsd.text = "Cargando..."

            // Convertir a USD en segundo plano
            CoroutineScope(Dispatchers.Main).launch {
                try {
                    val apiKey = try {
                        BuildConfig.FIXER_API_KEY
                    } catch (e: Exception) {
                        android.util.Log.w("BeatsAdapter", "⚠️ FIXER_API_KEY no configurada, usando valor por defecto")
                        "YOUR_FIXER_API_KEY_HERE"
                    }

                    val usdAmount = withContext(Dispatchers.IO) {
                        ExchangeRateProvider.convertClpToUsd(beat.precio, context, apiKey)
                    }

                    if (usdAmount != null) {
                        // Formatear precio en USD (con 2 decimales)
                        val usdFormatted = String.format(Locale.US, "$%.2f USD", usdAmount)
                        binding.txtPrecioUsd.text = usdFormatted
                        android.util.Log.d("BeatsAdapter", "💵 ${beat.titulo}: $clpFormatted = $usdFormatted")
                    } else {
                        binding.txtPrecioUsd.text = "USD no disponible"
                        android.util.Log.w("BeatsAdapter", "⚠️ No se pudo convertir precio para beat ${beat.id}")
                    }
                } catch (e: Exception) {
                    binding.txtPrecioUsd.text = "Error USD"
                    android.util.Log.e("BeatsAdapter", "❌ Error convirtiendo precio: ${e.message}")
                }
            }
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
