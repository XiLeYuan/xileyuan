package com.xly.business.find.model

data class Moment(
    val id: String,
    val userAvatar: Int, // drawable res
    val userName: String,
    val content: String,
    val images: List<String>, // 图片url或本地路径
    val time: String
)