package com.xly.business.vip.model

/**
 * VIP / SVIP 价格卡片数据模型
 *
 * @param tag 展示标签，例如：连续包年 / 连续包季 / 12个月 等
 * @param originalPrice 原价，例如：¥696，可为空
 * @param currentPrice 当前价格，例如：¥318
 * @param perMonthText 每月价格或说明，例如：26.5元/月 / 限时特惠
 * @param isRecommended 是否主推套餐（高亮边框）
 * @param isContinuous 是否连续续费产品（展示自动续费协议文案）
 */
data class VipRechargeOption(
    val tag: String,
    val originalPrice: String?,
    val currentPrice: String,
    val perMonthText: String,
    val isRecommended: Boolean = false,
    val isContinuous: Boolean = false
)