package com.xly.ui.widget

import android.animation.ArgbEvaluator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.view.Gravity
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.ColorTransitionPagerTitleView

class CustomTitleView(context: Context) : ColorTransitionPagerTitleView(context) {

    private var mNormalTextSize = 15f
    private var mSelectedTextSize = 19f
    private var mNormalColor = android.graphics.Color.parseColor("#666666")
    private var mSelectedColor = android.graphics.Color.parseColor("#FF6B6B")

    private val mTextPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val mTextBounds = Rect()

    // 固定的底部位置（距离底部边缘的距离）
    private val mBottomMargin = 8f

    init {
        // 设置初始状态
        textSize = mNormalTextSize
        setTextColor(mNormalColor)
        gravity = Gravity.CENTER

        // 设置文字对齐方式
        mTextPaint.textAlign = Paint.Align.CENTER
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

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        // 自定义绘制文字，确保底部对齐
        val text = text.toString()
        if (text.isNotEmpty()) {
            mTextPaint.textSize = textSize
            mTextPaint.color = currentTextColor
            mTextPaint.getTextBounds(text, 0, text.length, mTextBounds)

            // 计算文字位置，确保底部对齐
            val x = width / 2f

            // 使用固定的底部位置
            val y = height - mBottomMargin

            canvas.drawText(text, x, y, mTextPaint)
        }
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
        if (!isSelected) {
            setTextColor(color)
        }
    }
}