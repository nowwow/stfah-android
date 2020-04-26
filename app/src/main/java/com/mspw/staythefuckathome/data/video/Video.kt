package com.mspw.staythefuckathome.data.video

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import com.mspw.staythefuckathome.data.user.User
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Video(
    @SerializedName("id")
    val id: Long = 0,
    var url: String? = null,
    var title: String? = null,
    var user: User? = null,
    var isSelected: Boolean = false
) : Parcelable

