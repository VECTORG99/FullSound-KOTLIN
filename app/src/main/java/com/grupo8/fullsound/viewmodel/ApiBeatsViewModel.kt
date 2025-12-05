package com.grupo8.fullsound.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.grupo8.fullsound.data.remote.dto.BeatResponseDto
import com.grupo8.fullsound.data.remote.dto.BeatRequestDto
import com.grupo8.fullsound.repository.api.ApiBeatRepository
import com.grupo8.fullsound.utils.Resource
import kotlinx.coroutines.launch

/**
 * ViewModel para Beats usando API REST del backend Spring Boot
 */
class ApiBeatsViewModel(application: Application) : AndroidViewModel(application) {

    private val beatRepository = ApiBeatRepository(application.applicationContext)

    private val _beatsResult = MutableLiveData<Resource<List<BeatResponseDto>>>()
    val beatsResult: LiveData<Resource<List<BeatResponseDto>>> = _beatsResult

    private val _beatResult = MutableLiveData<Resource<BeatResponseDto>>()
    val beatResult: LiveData<Resource<BeatResponseDto>> = _beatResult

    private val _deleteResult = MutableLiveData<Resource<String>>()
    val deleteResult: LiveData<Resource<String>> = _deleteResult

    private val _searchResults = MutableLiveData<Resource<List<BeatResponseDto>>>()
    val searchResults: LiveData<Resource<List<BeatResponseDto>>> = _searchResults

    private val _featuredBeats = MutableLiveData<Resource<List<BeatResponseDto>>>()
    val featuredBeats: LiveData<Resource<List<BeatResponseDto>>> = _featuredBeats

    /**
     * Obtener todos los beats
     */
    fun getAllBeats() {
        viewModelScope.launch {
            _beatsResult.value = Resource.Loading()
            val result = beatRepository.getAllBeats()
            _beatsResult.value = result
        }
    }

    /**
     * Obtener un beat por ID
     */
    fun getBeatById(beatId: Int) {
        viewModelScope.launch {
            _beatResult.value = Resource.Loading()
            val result = beatRepository.getBeatById(beatId)
            _beatResult.value = result
        }
    }

    /**
     * Obtener un beat por slug
     */
    fun getBeatBySlug(slug: String) {
        viewModelScope.launch {
            _beatResult.value = Resource.Loading()
            val result = beatRepository.getBeatBySlug(slug)
            _beatResult.value = result
        }
    }

    /**
     * Buscar beats por término
     */
    fun searchBeats(query: String) {
        viewModelScope.launch {
            _searchResults.value = Resource.Loading()
            val result = beatRepository.searchBeats(query)
            _searchResults.value = result
        }
    }

    /**
     * Obtener beats destacados
     */
    fun getFeaturedBeats(limit: Int = 10) {
        viewModelScope.launch {
            _featuredBeats.value = Resource.Loading()
            val result = beatRepository.getFeaturedBeats(limit)
            _featuredBeats.value = result
        }
    }

    /**
     * Crear un nuevo beat (requiere ADMIN)
     */
    fun createBeat(beat: BeatRequestDto) {
        viewModelScope.launch {
            _beatResult.value = Resource.Loading()
            val result = beatRepository.createBeat(beat)
            _beatResult.value = result
        }
    }

    /**
     * Actualizar un beat (requiere ADMIN)
     */
    fun updateBeat(id: Int, beat: BeatRequestDto) {
        viewModelScope.launch {
            _beatResult.value = Resource.Loading()
            val result = beatRepository.updateBeat(id, beat)
            _beatResult.value = result
        }
    }

    /**
     * Eliminar un beat (requiere ADMIN)
     */
    fun deleteBeat(beatId: Int) {
        viewModelScope.launch {
            _deleteResult.value = Resource.Loading()
            val result = beatRepository.deleteBeat(beatId)
            _deleteResult.value = result
        }
    }
}
