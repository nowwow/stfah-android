package com.mspw.staythefuckathome.data.user

import com.google.gson.annotations.SerializedName

data class UpdateAddressUser(
    @SerializedName("address")
    val address:String
)