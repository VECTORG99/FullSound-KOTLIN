package com.grupo8.fullsound.ui.beats

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.grupo8.fullsound.data.models.Beat
import com.grupo8.fullsound.data.repositories.BeatRepository
import com.grupo8.fullsound.utils.Resource

class BeatsViewModel(private val beatRepository: BeatRepository) : ViewModel() {

    val beatsResult: LiveData<Resource<List<Beat>>> = beatRepository.beatsResult
    val beatResult: LiveData<Resource<Beat>> = beatRepository.beatResult
    val deleteResult: LiveData<Resource<String>> = beatRepository.deleteResult

    // CREATE ESTOY CANSADO JEFE
    fun insertBeat(beat: Beat) {
        beatRepository.insertBeat(beat)
    }

    // READ
    fun getAllBeats() {
        beatRepository.getAllBeats()
    }

    fun getBeatById(beatId: Int) {
        beatRepository.getBeatById(beatId)
    }

    // UPDATE
    fun updateBeat(beat: Beat) {
        beatRepository.updateBeat(beat)
    }

    // DELETE ESTOY CANSADO
    fun deleteBeat(beat: Beat) {
        beatRepository.deleteBeat(beat)
    }

    fun deleteBeatById(beatId: Int) {
        beatRepository.deleteBeatById(beatId)
    }

    // INSERTAR BEATS ESTOY CANSADO JEFE
    fun insertExampleBeats() {
        beatRepository.insertExampleBeats()
    }
}
