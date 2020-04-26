package com.mspw.staythefuckathome.data.video

import com.google.gson.annotations.SerializedName

data class CreateVideo(
    @SerializedName("url")
    val url: String,
    @SerializedName("challenge")
    val challengeId: Long,
    @SerializedName("creator")
    val creator: Long
)