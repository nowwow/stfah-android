package com.mspw.staythefuckathome.data.video

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import com.mspw.staythefuckathome.data.user.User
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Video(
    @SerializedName("id")
    val id: Long = 0,
    @SerializedName("url")
    var url: String? = null,
    @SerializedName("title")
    var title: String? = null,
    @SerializedName("creator")
    var user: User? = null,
    var isSelected: Boolean = false
) : Parcelable

