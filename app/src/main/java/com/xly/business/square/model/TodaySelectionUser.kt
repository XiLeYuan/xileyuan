package com.xly.business.square.model


data class TodaySelectionUser(
    val id: String,
    val name: String,
    val age: Int,
    val location: String,
    val avatar: String,
    val tags: List<String>,
    val selectionReason: String,        // 精选理由
    val selectionDescription: String,   // 精选描述
    val selectionType: SelectionType,   // 精选类型：官方推荐、智能匹配
    val matchScore: Float? = null,      // 匹配度（0-100）
    val hometown: String = "",         // 家乡
    val residence: String = "",         // 居住地
    val featureTags: List<String> = emptyList()  // 精选特色标签：官方推荐、高颜值、高收入等
)

enum class SelectionType {
    OFFICIAL,      // 官方推荐
    AI_MATCHED     // 智能匹配
}