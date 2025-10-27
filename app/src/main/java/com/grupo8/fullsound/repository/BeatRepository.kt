package com.grupo8.fullsound.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.grupo8.fullsound.data.local.BeatDao
import com.grupo8.fullsound.model.Beat
import com.grupo8.fullsound.utils.Resource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class BeatRepository(private val beatDao: BeatDao) {

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
                beatDao.insertBeat(beat)
                _beatResult.postValue(Resource.Success(beat))
            } catch (e: Exception) {
                _beatResult.postValue(Resource.Error("Error al insertar beat: ${e.message}"))
            }
        }
    }

    // READ
    fun getAllBeats() {
        CoroutineScope(Dispatchers.IO).launch {
            _beatsResult.postValue(Resource.Loading())
            try {
                val beats = beatDao.getAllBeats()
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

