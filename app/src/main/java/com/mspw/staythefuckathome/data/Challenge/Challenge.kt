package com.mspw.staythefuckathome.data.Challenge

import android.os.Parcelable
import com.mspw.staythefuckathome.data.Image
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Challenge(
    val url: String,
    val thumbnail: Image,
    val id: String
): Parcelable