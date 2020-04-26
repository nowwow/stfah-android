package com.mspw.staythefuckathome.data

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import com.mspw.staythefuckathome.data.user.User
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Sponsorship(
    @SerializedName("id")
    val id: Long,
    @SerializedName("coupon")
    var coupon: String?,
    @SerializedName("sponsor")
    var sponsor: User? = null
) : Parcelable