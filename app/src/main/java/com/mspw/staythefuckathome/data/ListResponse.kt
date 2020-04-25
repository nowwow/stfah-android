package com.mspw.staythefuckathome.data

data class ListResponse<T>(
    val next: String,
    val prev: String,
    val results: List<T>
)