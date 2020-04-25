package com.mspw.staythefuckathome.data

import com.google.gson.annotations.SerializedName

data class Image(
    @SerializedName("url")
    val url: String? = null,
    @SerializedName("width")
    val width: Int? = 0,
    @SerializedName("height")
    val height: Int? = 0
)