package com.mspw.staythefuckathome.data.challenge

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import com.mspw.staythefuckathome.data.Image
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Challenge(
    @SerializedName("id")
    val id: String,
    @SerializedName("url")
    var url: String? = null,
    @SerializedName("thumbnail")
    var thumbnail: Image? = null,
    @SerializedName("thumbnail")
    var title: String? = null,
    @SerializedName("reward")
    var reward: String? = null
): Parcelable