package com.mspw.staythefuckathome.data.video

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import com.mspw.staythefuckathome.data.Image
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Video(
    @SerializedName("id")
    val id: Long = 0,
    val thumbnail: Image? = null,
    var url: String? = null,
    var tag: String? = null,
    var isSelected: Boolean = false
) : Parcelable

