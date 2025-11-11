package com.xly.business.square.model

data class Moment(
    val id: String,
    val userAvatar: Int, // drawable res
    val userName: String,
    val content: String,
    val images: List<Int>, // 图片url或本地路径
    val time: String,
    val isVertical: Boolean = false, // 单张图片时，是否为竖图（高大于宽）
    val videoUrl: String? = null, // 视频URL（如果有视频，images为空或只包含视频封面）
    val videoThumbnail: Int? = null, // 视频缩略图资源ID
    val videoDuration: Long = 0L // 视频时长（秒）
)