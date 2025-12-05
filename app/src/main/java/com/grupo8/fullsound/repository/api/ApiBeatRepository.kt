package com.grupo8.fullsound.repository.api

import android.content.Context
import android.util.Log
import com.grupo8.fullsound.data.remote.RetrofitClient
import com.grupo8.fullsound.data.remote.dto.BeatRequestDto
import com.grupo8.fullsound.data.remote.dto.BeatResponseDto
import com.grupo8.fullsound.utils.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Repositorio para operaciones con Beats usando el backend Spring Boot
 */
class ApiBeatRepository(private val context: Context) {
    
    private val TAG = "ApiBeatRepository"
    private val beatApiService = RetrofitClient.getBeatApiService(context)
    
    /**
     * Obtener todos los beats activos
     */
    suspend fun getAllBeats(): Resource<List<BeatResponseDto>> {
        return withContext(Dispatchers.IO) {
            try {
                Log.d(TAG, "Obteniendo todos los beats desde backend...")
                val beats = beatApiService.getAllBeats()
                Log.d(TAG, "✅ ${beats.size} beats obtenidos")
                Resource.Success(beats)
            } catch (e: Exception) {
                Log.e(TAG, "❌ Error al obtener beats", e)
                Resource.Error("Error al cargar beats: ${e.message}")
            }
        }
    }
    
    /**
     * Obtener un beat por ID
     */
    suspend fun getBeatById(id: Int): Resource<BeatResponseDto> {
        return withContext(Dispatchers.IO) {
            try {
                Log.d(TAG, "Obteniendo beat con ID: $id")
                val beat = beatApiService.getBeatById(id)
                Log.d(TAG, "✅ Beat obtenido: ${beat.titulo}")
                Resource.Success(beat)
            } catch (e: Exception) {
                Log.e(TAG, "❌ Error al obtener beat por ID", e)
                Resource.Error("Error al cargar beat: ${e.message}")
            }
        }
    }
    
    /**
     * Obtener un beat por slug
     */
    suspend fun getBeatBySlug(slug: String): Resource<BeatResponseDto> {
        return withContext(Dispatchers.IO) {
            try {
                Log.d(TAG, "Obteniendo beat con slug: $slug")
                val beat = beatApiService.getBeatBySlug(slug)
                Log.d(TAG, "✅ Beat obtenido: ${beat.titulo}")
                Resource.Success(beat)
            } catch (e: Exception) {
                Log.e(TAG, "❌ Error al obtener beat por slug", e)
                Resource.Error("Error al cargar beat: ${e.message}")
            }
        }
    }
    
    /**
     * Buscar beats por término
     */
    suspend fun searchBeats(query: String): Resource<List<BeatResponseDto>> {
        return withContext(Dispatchers.IO) {
            try {
                Log.d(TAG, "Buscando beats con query: $query")
                val beats = beatApiService.searchBeats(query)
                Log.d(TAG, "✅ ${beats.size} beats encontrados")
                Resource.Success(beats)
            } catch (e: Exception) {
                Log.e(TAG, "❌ Error al buscar beats", e)
                Resource.Error("Error en búsqueda: ${e.message}")
            }
        }
    }
    
    /**
     * Obtener beats destacados
     */
    suspend fun getFeaturedBeats(limit: Int = 10): Resource<List<BeatResponseDto>> {
        return withContext(Dispatchers.IO) {
            try {
                Log.d(TAG, "Obteniendo beats destacados (limit: $limit)")
                val beats = beatApiService.getFeaturedBeats(limit)
                Log.d(TAG, "✅ ${beats.size} beats destacados obtenidos")
                Resource.Success(beats)
            } catch (e: Exception) {
                Log.e(TAG, "❌ Error al obtener beats destacados", e)
                Resource.Error("Error al cargar beats destacados: ${e.message}")
            }
        }
    }
    
    /**
     * Crear un nuevo beat (requiere rol ADMIN)
     */
    suspend fun createBeat(beat: BeatRequestDto): Resource<BeatResponseDto> {
        return withContext(Dispatchers.IO) {
            try {
                Log.d(TAG, "Creando beat: ${beat.titulo}")
                val createdBeat = beatApiService.createBeat(beat)
                Log.d(TAG, "✅ Beat creado con ID: ${createdBeat.id}")
                Resource.Success(createdBeat)
            } catch (e: Exception) {
                Log.e(TAG, "❌ Error al crear beat", e)
                Resource.Error("Error al crear beat: ${e.message}")
            }
        }
    }
    
    /**
     * Actualizar un beat existente (requiere rol ADMIN)
     */
    suspend fun updateBeat(id: Int, beat: BeatRequestDto): Resource<BeatResponseDto> {
        return withContext(Dispatchers.IO) {
            try {
                Log.d(TAG, "Actualizando beat ID: $id")
                val updatedBeat = beatApiService.updateBeat(id, beat)
                Log.d(TAG, "✅ Beat actualizado: ${updatedBeat.titulo}")
                Resource.Success(updatedBeat)
            } catch (e: Exception) {
                Log.e(TAG, "❌ Error al actualizar beat", e)
                Resource.Error("Error al actualizar beat: ${e.message}")
            }
        }
    }
    
    /**
     * Eliminar un beat (requiere rol ADMIN)
     */
    suspend fun deleteBeat(id: Int): Resource<String> {
        return withContext(Dispatchers.IO) {
            try {
                Log.d(TAG, "Eliminando beat ID: $id")
                val response = beatApiService.deleteBeat(id)
                Log.d(TAG, "✅ ${response.message}")
                Resource.Success(response.message)
            } catch (e: Exception) {
                Log.e(TAG, "❌ Error al eliminar beat", e)
                Resource.Error("Error al eliminar beat: ${e.message}")
            }
        }
    }
}
