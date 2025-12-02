package com.grupo8.fullsound.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.grupo8.fullsound.data.local.BeatDao
import com.grupo8.fullsound.model.Beat
import com.grupo8.fullsound.utils.Resource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import android.util.Log

// Import para Supabase
import com.grupo8.fullsound.data.remote.supabase.repository.SupabaseBeatRepository

class BeatRepository(
    private val beatDao: BeatDao,
    private val supabaseRepo: SupabaseBeatRepository = SupabaseBeatRepository()
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
                // Intentar insertar en Supabase primero
                val supabaseBeat = try {
                    supabaseRepo.insertBeat(beat)
                } catch (supabaseEx: Exception) {
                    Log.e(TAG, "Error al insertar en Supabase", supabaseEx)
                    null
                }

                // Insertar en BD local (con el ID de Supabase si se obtuvo)
                val finalBeat = supabaseBeat ?: beat
                beatDao.insertBeat(finalBeat)
                _beatResult.postValue(Resource.Success(finalBeat))
            } catch (e: Exception) {
                _beatResult.postValue(Resource.Error("Error al insertar beat: ${e.message}"))
            }
        }
    }

    // READ - Obtiene todos los beats desde Supabase (sin API key, precios ya están en CLP)
    fun getAllBeats() {
        CoroutineScope(Dispatchers.IO).launch {
            Log.d(TAG, "=== getAllBeats llamado ===")
            _beatsResult.postValue(Resource.Loading())
            try {
                // PRIORIDAD 1: Obtener desde Supabase
                Log.d(TAG, "Intentando obtener beats desde Supabase...")
                try {
                    val supabaseBeats = supabaseRepo.getAllBeats()
                    Log.d(TAG, "Supabase retornó ${supabaseBeats.size} beats")

                    if (supabaseBeats.isNotEmpty()) {
                        Log.d(TAG, "✅ Beats encontrados en Supabase:")
                        supabaseBeats.forEachIndexed { index, beat ->
                            Log.d(TAG, "  ${index + 1}. ${beat.titulo} - ${beat.artista} - \$${beat.precio} CLP")
                        }

                        // Guardar en BD local como caché
                        Log.d(TAG, "Guardando beats en caché local...")
                        supabaseBeats.forEach { beatDao.insertBeat(it) }

                        Log.d(TAG, "✅ Retornando ${supabaseBeats.size} beats desde Supabase")
                        _beatsResult.postValue(Resource.Success(supabaseBeats))
                        return@launch
                    } else {
                        Log.w(TAG, "⚠️ Supabase retornó lista vacía")
                    }
                } catch (supabaseEx: Exception) {
                    Log.e(TAG, "❌ Error al obtener beats de Supabase: ${supabaseEx.message}", supabaseEx)
                    // Si falla Supabase, continuar con fallback
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
                // Intentar obtener desde Supabase primero
                try {
                    val supabaseBeat = supabaseRepo.getBeatById(beatId)
                    if (supabaseBeat != null) {
                        // Actualizar caché local
                        beatDao.insertBeat(supabaseBeat)
                        _beatResult.postValue(Resource.Success(supabaseBeat))
                        return@launch
                    }
                } catch (supabaseEx: Exception) {
                    supabaseEx.printStackTrace()
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
                // Intentar actualizar en Supabase primero
                try {
                    supabaseRepo.updateBeat(beat)
                } catch (supabaseEx: Exception) {
                    supabaseEx.printStackTrace()
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
                // Intentar eliminar de Supabase primero
                try {
                    supabaseRepo.deleteBeat(beat.id)
                } catch (supabaseEx: Exception) {
                    supabaseEx.printStackTrace()
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
                // Intentar eliminar de Supabase primero
                try {
                    supabaseRepo.deleteBeat(beatId)
                } catch (supabaseEx: Exception) {
                    supabaseEx.printStackTrace()
                }

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
