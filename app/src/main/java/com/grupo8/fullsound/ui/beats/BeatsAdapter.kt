package com.grupo8.fullsound.ui.beats

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.grupo8.fullsound.data.models.Beat
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

                // Usar siempre el placeholder
                imgBeat.setImageResource(com.grupo8.fullsound.R.drawable.image)

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
