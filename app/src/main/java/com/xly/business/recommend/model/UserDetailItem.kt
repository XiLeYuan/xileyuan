package com.xly.business.recommend.model


data class UserDetailItem(
    val title: String,
    val content: String,
    val type: Int
) {
    companion object {
        const val TYPE_HEADER = 0
        const val TYPE_INFO = 1
        const val TYPE_HOBBY = 2
        const val TYPE_BIO = 3
    }
}