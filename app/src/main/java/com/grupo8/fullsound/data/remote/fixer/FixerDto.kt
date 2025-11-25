package com.grupo8.fullsound.data.remote.fixer

import com.google.gson.annotations.SerializedName

data class FixerLatestResponse(
    val success: Boolean,
    val timestamp: Long,
    val base: String?,
    val date: String?,
    @SerializedName("rates")
    val rates: Map<String, Double>?
)

