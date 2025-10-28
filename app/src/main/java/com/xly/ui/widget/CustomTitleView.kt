package com.xly.ui.widget

import android.animation.ArgbEvaluator
import android.content.Context
import android.view.Gravity
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.ColorTransitionPagerTitleView

class CustomTitleView (context: Context) : ColorTransitionPagerTitleView(context) {

    private var mNormalTextSize = 15f
    private var mSelectedTextSize = 19f

    init {
        // 设置初始状态
        textSize = mNormalTextSize
        setTextColor(mNormalColor)
        gravity = Gravity.CENTER
    }

    override fun onEnter(index: Int, totalCount: Int, enterPercent: Float, leftToRight: Boolean) {
        super.onEnter(index, totalCount, enterPercent, leftToRight)

        // 文字大小动画
        val textSize = mNormalTextSize + (mSelectedTextSize - mNormalTextSize) * enterPercent
        this.textSize = textSize

        // 颜色过渡动画
        val color = ArgbEvaluator().evaluate(enterPercent, mNormalColor, mSelectedColor) as Int
        setTextColor(color)
    }
    override fun onLeave(index: Int, totalCount: Int, leavePercent: Float, leftToRight: Boolean) {
        super.onLeave(index, totalCount, leavePercent, leftToRight)

        // 文字大小动画
        val textSize = mSelectedTextSize - (mSelectedTextSize - mNormalTextSize) * leavePercent
        this.textSize = textSize

        // 颜色过渡动画
        val color = ArgbEvaluator().evaluate(leavePercent, mSelectedColor, mNormalColor) as Int
        setTextColor(color)
    }

    fun setNormalTextSize(size: Float) {
        mNormalTextSize = size
        if (!isSelected) {
            textSize = size
        }
    }

    fun setSelectedTextSize(size: Float) {
        mSelectedTextSize = size
        if (isSelected) {
            textSize = size
        }
    }


    override fun setNormalColor(color: Int) {
        mNormalColor = color
        if (!isSelected) {
            setTextColor(color)
        }
    }

    override fun setSelectedColor(color: Int) {
        mSelectedColor = color
        if (isSelected) {
            setTextColor(color)
        }
    }
}