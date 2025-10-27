package com.grupo8.fullsound.ui.beats

<<<<<<< Updated upstream
=======
import android.graphics.BitmapFactory
>>>>>>> Stashed changes
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.grupo8.fullsound.data.models.Beat
import com.grupo8.fullsound.databinding.ItemBeatBinding
import java.io.File
<<<<<<< Updated upstream

class BeatsAdapter : ListAdapter<Beat, BeatsAdapter.BeatViewHolder>(BeatDiffCallback()) {
=======
import java.util.Locale

class BeatsAdapter(
    private val onAddToCarrito: (Beat) -> Unit,
    private val onComprar: (Beat) -> Unit
) : ListAdapter<Beat, BeatsAdapter.BeatViewHolder>(BeatDiffCallback()) {
>>>>>>> Stashed changes

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BeatViewHolder {
        val binding = ItemBeatBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return BeatViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BeatViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

<<<<<<< Updated upstream
    class BeatViewHolder(private val binding: ItemBeatBinding) :
        RecyclerView.ViewHolder(binding.root) {

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
=======
    inner class BeatViewHolder(
        private val binding: ItemBeatBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(beat: Beat) {
            binding.txtTitulo.text = beat.titulo
            binding.txtArtista.text = beat.artista
            binding.txtBpm.text = String.format(Locale.US, "%d BPM", beat.bpm)
            binding.txtPrecio.text = String.format(Locale.US, "$%.2f", beat.precio)

            // Cargar imagen si existe
            loadImage(beat.imagenPath)

            // Listeners de botones
            binding.btnAddCarrito.setOnClickListener {
                onAddToCarrito(beat)
            }

            binding.btnComprar.setOnClickListener {
                onComprar(beat)
            }
        }

        private fun loadImage(imagePath: String) {
            try {
                if (imagePath.isNotEmpty()) {
                    val imageFile = File(imagePath)
                    if (imageFile.exists()) {
                        val bitmap = BitmapFactory.decodeFile(imageFile.absolutePath)
                        binding.imgBeat.setImageBitmap(bitmap)
                    } else {
                        // Si es un ID de recurso de LocalBeatsProvider
                        loadImageFromAssets(imagePath)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        private fun loadImageFromAssets(imageId: String) {
            try {
                val context = binding.root.context
                val assetManager = context.assets
                val fileName = "images/${imageId}.jpg"
                val inputStream = assetManager.open(fileName)
                val bitmap = BitmapFactory.decodeStream(inputStream)
                binding.imgBeat.setImageBitmap(bitmap)
                inputStream.close()
            } catch (e: Exception) {
                e.printStackTrace()
>>>>>>> Stashed changes
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
<<<<<<< Updated upstream
=======

>>>>>>> Stashed changes
