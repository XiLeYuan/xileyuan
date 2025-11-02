package com.xly.business.square.model


data class Matchmaker(
    val id: String,
    val name: String,
    val avatar: String,
    val rating: Float,              // 评分 0-5
    val userCount: Int,             // 用户数量
    val location: String,            // 服务区域
    val description: String,       // 简介
    val tags: List<String>,         // 标签
    val successRate: Float,         // 成功率 0-100
    val isVerified: Boolean = false, // 是否认证
    val isVIP: Boolean = false,      // 是否VIP
    val yearsOfExperience: Int = 0   // 从业年限
)