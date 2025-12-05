package com.grupo8.fullsound.ui.carrito

import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.grupo8.fullsound.model.CarritoItem
import com.grupo8.fullsound.databinding.ItemCarritoBinding
import java.io.File
import java.util.Locale
import com.grupo8.fullsound.utils.FormatUtils

class CarritoAdapter(
    private val onRemove: (CarritoItem) -> Unit
) : ListAdapter<CarritoItem, CarritoAdapter.CarritoViewHolder>(CarritoDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CarritoViewHolder {
        val binding = ItemCarritoBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return CarritoViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CarritoViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class CarritoViewHolder(
        private val binding: ItemCarritoBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: CarritoItem) {
            binding.txtTituloCarrito.text = item.titulo
            binding.txtArtistaCarrito.text = item.artista ?: "Artista desconocido"

            // Formatear precios en CLP con separadores de miles y sin decimales
            try {
                binding.txtPrecioTotalItem.text = FormatUtils.formatClp(item.precio * item.cantidad)
            } catch (_: Exception) {
                binding.txtPrecioTotalItem.text = String.format(Locale.US, "$%.2f", item.precio * item.cantidad)
            }

            // Ajustes para evitar que el texto se corte
            binding.txtTituloCarrito.setSingleLine(true)
            binding.txtTituloCarrito.isSelected = true
            binding.txtArtistaCarrito.setSingleLine(true)

            // Cargar imagen
            loadImage(item.imagenPath ?: "")

            // Listener solo para eliminar
            binding.btnRemove.setOnClickListener {
                onRemove(item)
            }
        }

        private fun loadImage(imagePath: String) {
            try {
                if (imagePath.isNotEmpty()) {
                    val imageFile = File(imagePath)
                    if (imageFile.exists()) {
                        val bitmap = BitmapFactory.decodeFile(imageFile.absolutePath)
                        binding.imgBeatCarrito.setImageBitmap(bitmap)
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
                binding.imgBeatCarrito.setImageBitmap(bitmap)
                inputStream.close()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    class CarritoDiffCallback : DiffUtil.ItemCallback<CarritoItem>() {
        override fun areItemsTheSame(oldItem: CarritoItem, newItem: CarritoItem): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: CarritoItem, newItem: CarritoItem): Boolean {
            return oldItem == newItem
        }
    }
}
