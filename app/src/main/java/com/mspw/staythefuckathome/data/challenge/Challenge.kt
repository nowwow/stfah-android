package com.mspw.staythefuckathome.data.challenge

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import com.mspw.staythefuckathome.data.Image
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Challenge(
    @SerializedName("id")
    val id: Long = 0,
    @SerializedName("image")
    var image: String? = null,
    @SerializedName("title")
    var title: String? = null,
    @SerializedName("yt_video_id")
    var videoId: String? = null,
    @SerializedName("start_seconds")
    var startSeconds: Int = 0,
    @SerializedName("reward")
    var reward: String? = null
) : Parcelable