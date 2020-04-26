package com.mspw.staythefuckathome.data.user

import com.google.gson.annotations.SerializedName

data class SignUp(
    @SerializedName("username")
    val userName: String,
    @SerializedName("image")
    val image: String
)
