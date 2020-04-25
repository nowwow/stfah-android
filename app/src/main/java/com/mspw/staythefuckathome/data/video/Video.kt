package com.mspw.staythefuckathome.data.video

import com.google.gson.annotations.SerializedName
import com.mspw.staythefuckathome.data.Image

data class Video(
    @SerializedName("id")
    val id: Long = 0,
    val thumbnail: Image? = null
)

