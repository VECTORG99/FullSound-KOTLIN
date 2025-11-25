package com.grupo8.fullsound.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.grupo8.fullsound.data.local.BeatDao
import com.grupo8.fullsound.model.Beat
import com.grupo8.fullsound.utils.Resource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

// Añadimos imports para Retrofit
import com.grupo8.fullsound.data.remote.RetrofitClient
import com.grupo8.fullsound.data.remote.dto.BeatDto

// Import para fixer
import com.grupo8.fullsound.data.remote.fixer.ExchangeRateProvider

class BeatRepository(private val beatDao: BeatDao) {

    private val _beatsResult = MutableLiveData<Resource<List<Beat>>>()
    val beatsResult: LiveData<Resource<List<Beat>>> = _beatsResult

    private val _beatResult = MutableLiveData<Resource<Beat>>()
    val beatResult: LiveData<Resource<Beat>> = _beatResult

    private val _deleteResult = MutableLiveData<Resource<String>>()
    val deleteResult: LiveData<Resource<String>> = _deleteResult

    // Mapeo desde BeatDto -> Beat local
    private fun dtoToBeat(dto: BeatDto): Beat {
        // Si no hay id remoto, generar uno estable a partir de title+artist para evitar duplicados
        val computedId = if (dto.id > 0) dto.id else {
            val key = (dto.title ?: "") + "|" + (dto.artist ?: "")
            val h = key.hashCode()
            val abs = if (h == Int.MIN_VALUE) Int.MAX_VALUE else Math.abs(h)
            // Evitar 0
            if (abs == 0) 1 else abs
        }

        return Beat(
            id = computedId,
            titulo = dto.title ?: "Sin título",
            artista = dto.artist ?: "Desconocido",
            bpm = dto.bpm ?: 0,
            imagenPath = dto.image ?: "img1",
            mp3Path = dto.mp3 ?: "",
            precio = dto.price ?: 9.99
        )
    }

    // Convierte precio USD -> CLP usando fixer (si falla devuelve precio USD)
    private suspend fun convertUsdToClpIfPossible(usdPrice: Double, apiKey: String, context: android.content.Context): Double {
        return try {
            val rate = ExchangeRateProvider.getUsdToClpRate(context, apiKey)
            if (rate != null) {
                // redondear a 2 decimales
                String.format("%.2f", usdPrice * rate).replace(',', '.').toDouble()
            } else {
                usdPrice
            }
        } catch (e: Exception) {
            e.printStackTrace()
            usdPrice
        }
    }

    // CREATE
    fun insertBeat(beat: Beat) {
        CoroutineScope(Dispatchers.IO).launch {
            _beatResult.postValue(Resource.Loading())
            try {
                beatDao.insertBeat(beat)
                _beatResult.postValue(Resource.Success(beat))
            } catch (e: Exception) {
                _beatResult.postValue(Resource.Error("Error al insertar beat: ${e.message}"))
            }
        }
    }

    // READ
    // getAllBeats ahora tiene una variante que recibe opcionalmente apiKeyFixer para convertir precios a CLP
    fun getAllBeats(apiKeyFixer: String? = null, context: android.content.Context? = null) {
        CoroutineScope(Dispatchers.IO).launch {
            _beatsResult.postValue(Resource.Loading())
            try {
                // Intentar primero obtener desde API remota
                try {
                    val remoteDtos = RetrofitClient.apiService.getBeats()
                    var remoteBeats = remoteDtos.map { dtoToBeat(it) }

                    // Si se pasa apiKey y price viene en USD, intentamos convertir
                    if (apiKeyFixer != null && context != null) {
                        // Convertir cada precio de USD -> CLP (suponiendo que dto.price está en USD)
                        val convertedList = mutableListOf<Beat>()
                        for (b in remoteBeats) {
                            val cp = try {
                                convertUsdToClpIfPossible(b.precio, apiKeyFixer, context)
                            } catch (_: Exception) {
                                b.precio
                            }
                            convertedList.add(b.copy(precio = cp))
                        }
                        remoteBeats = convertedList
                    }

                    // Guardar/actualizar en BD local
                    remoteBeats.forEach { beatDao.insertBeat(it) }
                    // Leer desde BD para obtener los IDs reales generados por Room
                    var beatsFromDb = beatDao.getAllBeats()
                    // Si se pasó apiKeyFixer, convertir precios en la lista leída de la BD
                    if (apiKeyFixer != null && context != null && beatsFromDb.isNotEmpty()) {
                        val converted = mutableListOf<Beat>()
                        for (b in beatsFromDb) {
                            val cp = try {
                                convertUsdToClpIfPossible(b.precio, apiKeyFixer, context)
                            } catch (_: Exception) { b.precio }
                            converted.add(b.copy(precio = cp))
                        }
                        beatsFromDb = converted
                    }
                    _beatsResult.postValue(Resource.Success(beatsFromDb))
                     return@launch
                 } catch (apiEx: Exception) {
                    // Si falla la API, continuamos con la BD local
                    apiEx.printStackTrace()
                }

                var beats = beatDao.getAllBeats()

                // Si se solicita conversion y hay beats locales (precio en USD), convertir
                if (apiKeyFixer != null && context != null && beats.isNotEmpty()) {
                    val convertedLocal = mutableListOf<Beat>()
                    for (b in beats) {
                        val cp = try {
                            convertUsdToClpIfPossible(b.precio, apiKeyFixer, context)
                        } catch (_: Exception) {
                            b.precio
                        }
                        convertedLocal.add(b.copy(precio = cp))
                    }
                    beats = convertedLocal
                }

                _beatsResult.postValue(Resource.Success(beats))
            } catch (e: Exception) {
                _beatsResult.postValue(Resource.Error("Error al obtener beats: ${e.message}"))
            }
        }
    }

    fun getBeatById(beatId: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            _beatResult.postValue(Resource.Loading())
            try {
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

    // INSERTAR BEATS DE EJEMPLO
    fun insertExampleBeats() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                // Verificar si ya hay beats en la base de datos
                val existingBeats = beatDao.getAllBeats()
                if (existingBeats.isEmpty()) {
                    // Insertar beats de ejemplo
                    val exampleBeats = com.grupo8.fullsound.data.local.LocalBeatsProvider.getBeats()
                    exampleBeats.forEach { beat ->
                        beatDao.insertBeat(beat)
                    }
                }
                // Cargar beats
                val beats = beatDao.getAllBeats()
                _beatsResult.postValue(Resource.Success(beats))
            } catch (e: Exception) {
                e.printStackTrace()
                _beatsResult.postValue(Resource.Error("Error al cargar beats: ${e.message}"))
            }
        }
    }
}
