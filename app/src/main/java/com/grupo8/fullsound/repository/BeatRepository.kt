package com.grupo8.fullsound.repository

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.grupo8.fullsound.data.local.BeatDao
import com.grupo8.fullsound.model.Beat
import com.grupo8.fullsound.utils.Resource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

// Import para el backend Spring Boot
import com.grupo8.fullsound.repository.api.ApiBeatRepository

class BeatRepository(
    private val beatDao: BeatDao,
    private val context: Context,
    private val apiRepo: ApiBeatRepository = ApiBeatRepository(context)
) {

    private val TAG = "BeatRepository"

    private val _beatsResult = MutableLiveData<Resource<List<Beat>>>()
    val beatsResult: LiveData<Resource<List<Beat>>> = _beatsResult

    private val _beatResult = MutableLiveData<Resource<Beat>>()
    val beatResult: LiveData<Resource<Beat>> = _beatResult

    private val _deleteResult = MutableLiveData<Resource<String>>()
    val deleteResult: LiveData<Resource<String>> = _deleteResult


    // CREATE
    fun insertBeat(beat: Beat) {
        CoroutineScope(Dispatchers.IO).launch {
            _beatResult.postValue(Resource.Loading())
            try {
                Log.d(TAG, "Insertando beat en backend Spring Boot...")
                // Intentar insertar en el backend Spring Boot
                val backendBeat = try {
                    // TODO: Implementar método createBeat en ApiBeatRepository
                    // apiRepo.createBeat(beat.toBeatRequestDto())
                    Log.w(TAG, "Método createBeat no implementado aún en ApiBeatRepository")
                    beat
                } catch (backendEx: Exception) {
                    Log.e(TAG, "Error al obtener beat del backend", backendEx)
                    beat
                }

                // Guardar en BD local como caché
                beatDao.insertBeat(backendBeat)
                _beatResult.postValue(Resource.Success(backendBeat))
            } catch (e: Exception) {
                Log.e(TAG, "Error al insertar beat", e)
                _beatResult.postValue(Resource.Error("Error al insertar beat: ${e.message}"))
            }
        }
    }

    // READ - Obtiene todos los beats desde el backend Spring Boot
    fun getAllBeats() {
        CoroutineScope(Dispatchers.IO).launch {
            Log.d(TAG, "=== getAllBeats llamado ===")
            _beatsResult.postValue(Resource.Loading())
            try {
                // PRIORIDAD 1: Obtener desde backend Spring Boot
                Log.d(TAG, "Intentando obtener beats desde backend Spring Boot...")
                try {
                    val response = apiRepo.getAllBeats()

                    when (response) {
                        is Resource.Success -> {
                            val beatDtos = response.data ?: emptyList()
                            Log.d(TAG, "Backend retornó ${beatDtos.size} beats")

                            if (beatDtos.isNotEmpty()) {
                                Log.d(TAG, "✅ Beats encontrados en backend:")

                                // Convertir DTOs a modelo Beat
                                val beats = beatDtos.map { dto ->
                                    Beat(
                                        id = dto.id,
                                        titulo = dto.titulo,
                                        artista = dto.artista ?: "",
                                        precio = dto.precio.toDouble(),
                                        slug = dto.slug ?: "",
                                        bpm = dto.bpm ?: 0,
                                        tonalidad = dto.tonalidad ?: "",
                                        duracion = dto.duracion ?: 0,
                                        genero = dto.genero ?: "",
                                        etiquetas = dto.etiquetas ?: "",
                                        descripcion = dto.descripcion ?: "",
                                        imagenPath = dto.imagenUrl ?: "",
                                        mp3Path = dto.audioUrl ?: "",
                                        estado = dto.estado
                                    )
                                }

                                beats.forEachIndexed { index, beat ->
                                    Log.d(TAG, "  ${index + 1}. ${beat.titulo} - ${beat.artista} - \$${beat.precio} CLP")
                                }

                                // Guardar en BD local como caché
                                Log.d(TAG, "Guardando beats en caché local...")
                                beats.forEach { beatDao.insertBeat(it) }

                                Log.d(TAG, "✅ Retornando ${beats.size} beats desde backend")
                                _beatsResult.postValue(Resource.Success(beats))
                                return@launch
                            } else {
                                Log.w(TAG, "⚠️ Backend retornó lista vacía")
                            }
                        }
                        is Resource.Error -> {
                            Log.e(TAG, "❌ Error del backend: ${response.message}")
                        }
                        else -> {
                            Log.w(TAG, "⚠️ Estado desconocido del backend")
                        }
                    }
                } catch (backendEx: Exception) {
                    Log.e(TAG, "❌ Error al obtener beats del backend: ${backendEx.message}", backendEx)
                    // Si falla el backend, continuar con fallback
                }

                // PRIORIDAD 2: Fallback a BD local (Room)
                Log.d(TAG, "Obteniendo beats desde Room DB (caché local)...")
                val beats = beatDao.getAllBeats()
                Log.d(TAG, "Room retornó ${beats.size} beats")

                if (beats.isNotEmpty()) {
                    Log.d(TAG, "✅ Retornando ${beats.size} beats desde Room DB")
                    _beatsResult.postValue(Resource.Success(beats))
                } else {
                    Log.w(TAG, "⚠️ No hay beats disponibles")
                    _beatsResult.postValue(Resource.Success(emptyList()))
                }
            } catch (e: Exception) {
                Log.e(TAG, "❌ Error general al obtener beats: ${e.message}", e)
                _beatsResult.postValue(Resource.Error("Error al obtener beats: ${e.message}"))
            }
        }
    }

    fun getBeatById(beatId: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            _beatResult.postValue(Resource.Loading())
            try {
                // Intentar obtener desde backend Spring Boot primero
                try {
                    val response = apiRepo.getBeatById(beatId)
                    if (response is Resource.Success && response.data != null) {
                        val dto = response.data
                        val beat = Beat(
                            id = dto.id,
                            titulo = dto.titulo,
                            artista = dto.artista ?: "",
                            precio = dto.precio.toDouble(),
                            slug = dto.slug ?: "",
                            bpm = dto.bpm ?: 0,
                            tonalidad = dto.tonalidad ?: "",
                            duracion = dto.duracion ?: 0,
                            genero = dto.genero ?: "",
                            etiquetas = dto.etiquetas ?: "",
                            descripcion = dto.descripcion ?: "",
                            imagenPath = dto.imagenUrl ?: "",
                            mp3Path = dto.audioUrl ?: "",
                            estado = dto.estado
                        )
                        // Actualizar caché local
                        beatDao.insertBeat(beat)
                        _beatResult.postValue(Resource.Success(beat))
                        return@launch
                    }
                } catch (backendEx: Exception) {
                    Log.e(TAG, "Error al obtener beat del backend", backendEx)
                }

                // Fallback a BD local
                val beat = beatDao.getBeatById(beatId)
                if (beat != null) {
                    _beatResult.postValue(Resource.Success(beat))
                } else {
                    _beatResult.postValue(Resource.Error("Beat no encontrado"))
                }
            } catch (e: Exception) {
                _beatResult.postValue(Resource.Error("Error al obtener beat: ${e.message}"))
            }
        }
    }

    // UPDATE
    fun updateBeat(beat: Beat) {
        CoroutineScope(Dispatchers.IO).launch {
            _beatResult.postValue(Resource.Loading())
            try {
                // Intentar actualizar en backend Spring Boot primero
                try {
                    // TODO: Implementar método updateBeat en ApiBeatRepository
                    Log.w(TAG, "Método updateBeat no implementado aún en ApiBeatRepository")
                } catch (backendEx: Exception) {
                    Log.e(TAG, "Error al actualizar beat en backend", backendEx)
                }

                // Actualizar en BD local
                beatDao.updateBeat(beat)
                _beatResult.postValue(Resource.Success(beat))
            } catch (e: Exception) {
                _beatResult.postValue(Resource.Error("Error al actualizar beat: ${e.message}"))
            }
        }
    }

    // DELETE
    fun deleteBeat(beat: Beat) {
        CoroutineScope(Dispatchers.IO).launch {
            _deleteResult.postValue(Resource.Loading())
            try {
                // Intentar eliminar desde backend Spring Boot primero
                try {
                    // TODO: Implementar método deleteBeat en ApiBeatRepository
                    Log.w(TAG, "Método deleteBeat no implementado aún en ApiBeatRepository")
                } catch (backendEx: Exception) {
                    Log.e(TAG, "Error al eliminar beat del backend", backendEx)
                }

                // Eliminar de BD local
                beatDao.deleteBeat(beat)
                _deleteResult.postValue(Resource.Success("Beat eliminado exitosamente"))
            } catch (e: Exception) {
                _deleteResult.postValue(Resource.Error("Error al eliminar beat: ${e.message}"))
            }
        }
    }

    fun deleteBeatById(beatId: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            _deleteResult.postValue(Resource.Loading())
            try {
                // TODO: Implementar eliminación en backend Spring Boot
                Log.d(TAG, "Eliminando beat del backend...")

                // Eliminar de BD local
                beatDao.deleteBeatById(beatId)
                _deleteResult.postValue(Resource.Success("Beat eliminado exitosamente"))
            } catch (e: Exception) {
                _deleteResult.postValue(Resource.Error("Error al eliminar beat: ${e.message}"))
            }
        }
    }

    fun deleteAllBeats() {
        CoroutineScope(Dispatchers.IO).launch {
            _deleteResult.postValue(Resource.Loading())
            try {
                beatDao.deleteAllBeats()
                _deleteResult.postValue(Resource.Success("Todos los beats eliminados"))
            } catch (e: Exception) {
                _deleteResult.postValue(Resource.Error("Error al eliminar beats: ${e.message}"))
            }
        }
    }
}
