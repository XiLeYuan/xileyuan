package com.xly.ui.widget

import android.content.Context
import android.graphics.Color
import net.lucode.hackware.magicindicator.buildins.commonnavigator.indicators.LinePagerIndicator

class CustomLineIndicator (context: Context) : LinePagerIndicator(context) {


    private var mLineColor = Color.parseColor("#FF6B6B")

    init {

        // 直接设置属性，不要重写 setter 方法
        mode = MODE_EXACTLY
        lineHeight = 6f
        lineWidth = 60f
        roundRadius = 4f
        setColors(Color.parseColor("#FF6B6B"))

    }



    fun setLineColor(color: Int) {
        mLineColor = color
        setColors(color)
    }

}