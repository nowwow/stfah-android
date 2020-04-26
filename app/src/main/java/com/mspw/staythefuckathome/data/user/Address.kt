package com.mspw.staythefuckathome.data.user

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Address(
    val address: String,
    val lat: Double,
    val lng: Double
) : Parcelable