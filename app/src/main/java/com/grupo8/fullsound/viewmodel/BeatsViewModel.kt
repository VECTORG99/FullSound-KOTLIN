package com.grupo8.fullsound.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.grupo8.fullsound.model.Beat
import com.grupo8.fullsound.repository.BeatRepository
import com.grupo8.fullsound.utils.Resource

class BeatsViewModel(private val beatRepository: BeatRepository) : ViewModel() {

    val beatsResult: LiveData<Resource<List<Beat>>> = beatRepository.beatsResult
    val beatResult: LiveData<Resource<Beat>> = beatRepository.beatResult
    val deleteResult: LiveData<Resource<String>> = beatRepository.deleteResult

    // CREATE
    fun insertBeat(beat: Beat) {
        beatRepository.insertBeat(beat)
    }

    // READ - Los precios ya vienen en CLP desde Supabase
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
}
