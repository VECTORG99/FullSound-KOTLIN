package com.grupo8.fullsound.ui.beats

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.grupo8.fullsound.repository.BeatRepository
import com.grupo8.fullsound.viewmodel.BeatsViewModel

class BeatsViewModelFactory(private val beatRepository: BeatRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(BeatsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return BeatsViewModel(beatRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

