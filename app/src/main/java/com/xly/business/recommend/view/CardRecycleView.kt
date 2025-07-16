package com.xly.business.recommend.view

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.ViewConfiguration
import androidx.recyclerview.widget.RecyclerView

class CardRecycleView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : RecyclerView(context, attrs) {

    private var startX = 0f
    private var startY = 0f
    private val touchSlop = ViewConfiguration.get(context).scaledTouchSlop
    private var isVerticalScroll = false

    override fun onInterceptTouchEvent(e: MotionEvent): Boolean {
        when (e.action) {
            MotionEvent.ACTION_DOWN -> {
                startX = e.x
                startY = e.y
                isVerticalScroll = false
                parent.requestDisallowInterceptTouchEvent(false)
            }
            MotionEvent.ACTION_MOVE -> {
                val dx = Math.abs(e.x - startX)
                val dy = Math.abs(e.y - startY)
                if (dy > dx && dy > touchSlop) {
                    // 垂直滑动，自己拦截
                    isVerticalScroll = true
                    parent.requestDisallowInterceptTouchEvent(true)
                    return true
                } else if (dx > dy && dx > touchSlop) {
                    // 水平滑动，交给父控件
                    isVerticalScroll = false
                    parent.requestDisallowInterceptTouchEvent(false)
                    return false
                }
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                isVerticalScroll = false
                parent.requestDisallowInterceptTouchEvent(false)
            }
        }
        return super.onInterceptTouchEvent(e)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(e: MotionEvent): Boolean {
        if (isVerticalScroll) {
            parent.requestDisallowInterceptTouchEvent(true)
        }
        return super.onTouchEvent(e)
    }
}