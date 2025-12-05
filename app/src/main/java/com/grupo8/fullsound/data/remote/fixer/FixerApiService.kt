package com.grupo8.fullsound.data.remote.fixer

import retrofit2.http.GET
import retrofit2.http.Query

interface FixerApiService {
    @GET("latest")
    suspend fun getLatestRates(
        @Query("access_key") accessKey: String,
        @Query("symbols") symbols: String
    ): FixerLatestResponse
}

