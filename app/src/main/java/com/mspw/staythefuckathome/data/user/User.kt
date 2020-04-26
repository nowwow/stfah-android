package com.mspw.staythefuckathome.data.user

import com.mspw.staythefuckathome.data.Image


data class User(
    val id: String,
    val name: String,
    val profile: Image,
    val address:Address
)