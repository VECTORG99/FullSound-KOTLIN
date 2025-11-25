package com.grupo8.fullsound.data.remote

import com.grupo8.fullsound.data.remote.dto.BeatDto
import retrofit2.http.GET

interface ApiService {
    @GET("beats") // Ajustar según la API real; por ahora asumimos endpoint /beats
    suspend fun getBeats(): List<BeatDto>
}
