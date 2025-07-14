package com.xly.middlelibrary.net

data class LYResponse<T>(
    val code: Int,
    val message: String,
    val data: T?
)