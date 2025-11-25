package com.grupo8.fullsound.data.remote.fixer

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object FixerRetrofitClient {
    private const val BASE_URL = "https://data.fixer.io/api/"

    val apiService: FixerApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(FixerApiService::class.java)
    }
}
