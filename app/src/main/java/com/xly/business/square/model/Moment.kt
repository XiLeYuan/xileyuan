package com.xly.business.square.model

data class Moment(
    val id: String,
    val userAvatar: Int, // drawable res
    val userName: String,
    val content: String,
    val images: List<Int>, // 图片url或本地路径
    val time: String
)