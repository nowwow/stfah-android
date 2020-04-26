package com.mspw.staythefuckathome.data.user

import android.os.Parcelable
import com.mspw.staythefuckathome.data.Image
import kotlinx.android.parcel.Parcelize

@Parcelize
data class User(
    val id: String,
    val name: String,
    val profile: Image,
    val address: Address
) : Parcelable