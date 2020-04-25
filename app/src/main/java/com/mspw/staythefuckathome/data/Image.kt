package com.mspw.staythefuckathome.data

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Image(
    @SerializedName("url")
    val url: String? = null,
    @SerializedName("width")
    val width: Int? = 0,
    @SerializedName("height")
    val height: Int? = 0
) : Parcelable