package com.mspw.staythefuckathome.data.user

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import com.mspw.staythefuckathome.data.Image
import kotlinx.android.parcel.Parcelize

@Parcelize
data class User(
    @SerializedName("id")
    val id: Long = 0,
    @SerializedName("username")
    var name: String? = null,
    @SerializedName("image")
    var image: String? = null,
    var address: String = ""
) : Parcelable