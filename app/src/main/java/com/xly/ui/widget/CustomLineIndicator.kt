package com.xly.ui.widget

import android.content.Context
import android.graphics.Canvas
import net.lucode.hackware.magicindicator.buildins.commonnavigator.indicators.LinePagerIndicator

class CustomLineIndicator(context: Context) : LinePagerIndicator(context) {

    private var mBottomOffset = 0f  // 距离底部的偏移量（px）

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
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        // 保存当前画布状态
        canvas.save()
        
        // 向上移动画布，使指示器更靠近文字
        // mBottomOffset 是距离底部的偏移量，向上移动就是负值
        val translateY = -mBottomOffset
        canvas.translate(0f, translateY)
        
        // 调用父类绘制方法
        super.onDraw(canvas)
        
        // 恢复画布状态
        canvas.restore()
    }
}