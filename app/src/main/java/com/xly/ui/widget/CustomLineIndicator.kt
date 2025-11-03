package com.xly.ui.widget

import android.content.Context
import net.lucode.hackware.magicindicator.buildins.commonnavigator.indicators.LinePagerIndicator

class CustomLineIndicator(context: Context) : LinePagerIndicator(context) {

    private var mBottomOffset = 2f  // 默认 2dp

    init {
        mode = MODE_EXACTLY
        lineHeight = 10f
        lineWidth = 45f
        roundRadius = 5f
        setColors(android.graphics.Color.parseColor("#FF6B6B"))
    }

    /**
     * 设置距离底部的偏移量（dp）
     * 值越小，指示器越靠近文本
     */
    fun setBottomOffset(offsetDp: Float) {
        mBottomOffset = offsetDp * resources.displayMetrics.density
        invalidate()  // 继承自 View，可以直接调用
    }

    // 如果需要自定义绘制，可以重写 onDraw
    // 但通常只需要调整 bottomOffset 即可通过其他方式实现
}