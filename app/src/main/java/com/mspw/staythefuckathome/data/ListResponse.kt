package com.mspw.staythefuckathome.data

import com.google.gson.annotations.SerializedName

data class ListResponse<T>(
    @SerializedName("next")
    val next: String,
    @SerializedName("previous")
    val prev: String,
    @SerializedName("results")
    val results: List<T>
)