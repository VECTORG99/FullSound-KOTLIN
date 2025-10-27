package com.grupo8.fullsound.ui.carrito

import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.grupo8.fullsound.data.models.CarritoItem
import com.grupo8.fullsound.databinding.ItemCarritoBinding
import java.io.File
import java.util.Locale

class CarritoAdapter(
    private val onRemove: (CarritoItem) -> Unit,
    private val onQuantityChange: (CarritoItem, Int) -> Unit
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
            binding.txtArtistaCarrito.text = item.artista
            binding.txtPrecioUnitario.text = String.format(Locale.US, "$%.2f c/u", item.precio)
            binding.txtCantidad.text = item.cantidad.toString()

            val totalItem = item.precio * item.cantidad
            binding.txtPrecioTotalItem.text = String.format(Locale.US, "$%.2f", totalItem)

            // Cargar imagen
            loadImage(item.imagenPath)

            // Listeners
            binding.btnRemove.setOnClickListener {
                onRemove(item)
            }

            binding.btnDecrease.setOnClickListener {
                val newQuantity = item.cantidad - 1
                onQuantityChange(item, newQuantity)
            }

            binding.btnIncrease.setOnClickListener {
                val newQuantity = item.cantidad + 1
                onQuantityChange(item, newQuantity)
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

