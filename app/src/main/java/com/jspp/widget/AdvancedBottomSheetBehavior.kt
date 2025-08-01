package com.jspp.widget

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.ViewCompat
import androidx.customview.widget.ViewDragHelper
import com.google.android.material.bottomsheet.BottomSheetBehavior
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

class AdvancedBottomSheetBehavior<V : View> : BottomSheetBehavior<V> {

    constructor() : super()
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    private var onSlideListener: ((Float) -> Unit)? = null
    private var onStateChangeListener: ((Int) -> Unit)? = null
    private var currentChild: V? = null
    
    // 自定义状态
    companion object {
        const val STATE_PEEK = 4  // 露出状态
        const val STATE_HALF = 5  // 半展开状态
        const val STATE_FULL = 6  // 全展开状态
    }

    fun setOnSlideListener(listener: (Float) -> Unit) {
        onSlideListener = listener
    }

    fun setOnStateChangeListener(listener: (Int) -> Unit) {
        onStateChangeListener = listener
    }

    override fun onLayoutChild(parent: CoordinatorLayout, child: V, layoutDirection: Int): Boolean {
        val handled = super.onLayoutChild(parent, child, layoutDirection)
        
        // 保存child引用
        currentChild = child
        
        // 设置初始位置
        if (state == STATE_COLLAPSED) {
            child.translationY = parent.height - peekHeight.toFloat()
        }
        
        return handled
    }

    override fun onStartNestedScroll(
        coordinatorLayout: CoordinatorLayout,
        child: V,
        directTargetChild: View,
        target: View,
        axes: Int,
        type: Int
    ): Boolean {
        // 确保可以响应垂直滑动
        return super.onStartNestedScroll(coordinatorLayout, child, directTargetChild, target, axes, type) ||
                (axes and ViewCompat.SCROLL_AXIS_VERTICAL != 0)
    }

    override fun onStopNestedScroll(
        coordinatorLayout: CoordinatorLayout,
        child: V,
        target: View,
        type: Int
    ) {
        super.onStopNestedScroll(coordinatorLayout, child, target, type)
        
        // 根据当前位置决定最终状态
        val slideOffset = getSlideOffset(child, coordinatorLayout)
        val parentHeight = coordinatorLayout.height
        
        when {
            slideOffset < 0.25f -> {
                // 接近顶部，完全展开
                setState(STATE_EXPANDED)
                onStateChangeListener?.invoke(STATE_FULL)
            }
            slideOffset < 0.6f -> {
                // 中间位置，半展开
                setState(STATE_COLLAPSED)
                onStateChangeListener?.invoke(STATE_HALF)
            }
            else -> {
                // 接近底部，收起
                setState(STATE_COLLAPSED)
                onStateChangeListener?.invoke(STATE_PEEK)
            }
        }
    }

    override fun onNestedPreScroll(
        coordinatorLayout: CoordinatorLayout,
        child: V,
        target: View,
        dx: Int,
        dy: Int,
        consumed: IntArray,
        type: Int
    ) {
        super.onNestedPreScroll(coordinatorLayout, child, target, dx, dy, consumed, type)
        
        val slideOffset = getSlideOffset(child, coordinatorLayout)
        onSlideListener?.invoke(slideOffset)
    }

    override fun onNestedScroll(
        coordinatorLayout: CoordinatorLayout,
        child: V,
        target: View,
        dxConsumed: Int,
        dyConsumed: Int,
        dxUnconsumed: Int,
        dyUnconsumed: Int,
        type: Int
    ) {
        super.onNestedScroll(coordinatorLayout, child, target, dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, type)
        
        // 处理未消费的滑动
        if (dyUnconsumed != 0) {
            val slideOffset = getSlideOffset(child, coordinatorLayout)
            onSlideListener?.invoke(slideOffset)
        }
    }

    private fun getSlideOffset(child: V, parent: ViewGroup): Float {
        val parentHeight = parent.height
        val translationY = child.translationY
        
        return when {
            translationY <= 0 -> 1f // 完全展开
            translationY >= parentHeight - peekHeight -> 0f // 完全收起
            else -> {
                val maxTranslation = parentHeight - peekHeight
                (maxTranslation - translationY) / maxTranslation
            }
        }
    }

    /**
     * 设置自定义状态
     */
    fun setCustomState(state: Int, animate: Boolean = true) {
        val child = currentChild ?: return
        
        if (animate) {
            when (state) {
                STATE_FULL -> setState(STATE_EXPANDED)
                STATE_HALF -> setState(STATE_COLLAPSED)
                STATE_PEEK -> setState(STATE_COLLAPSED)
                else -> setState(state)
            }
        } else {
            // 直接设置位置，不触发动画
            val parent = child.parent as? ViewGroup ?: return
            
            when (state) {
                STATE_EXPANDED, STATE_FULL -> {
                    child.translationY = 0f
                }
                STATE_COLLAPSED, STATE_PEEK -> {
                    child.translationY = parent.height - peekHeight.toFloat()
                }
                STATE_HALF -> {
                    child.translationY = (parent.height - peekHeight) * 0.5f
                }
                STATE_HIDDEN -> {
                    child.translationY = parent.height.toFloat()
                }
            }
        }
    }

    /**
     * 获取当前滑动偏移量
     */
    fun getCurrentSlideOffset(): Float {
        val child = currentChild ?: return 0f
        val parent = child.parent as? ViewGroup ?: return 0f
        return getSlideOffset(child, parent)
    }

    /**
     * 设置半展开高度
     */
    fun setHalfExpandedHeight(height: Int) {
        // 可以通过重写ViewDragHelper来实现半展开高度
    }

    /**
     * 平滑滑动到指定位置
     */
    fun smoothSlideTo(offset: Float) {
        val child = currentChild ?: return
        val parent = child.parent as? ViewGroup ?: return
        val parentHeight = parent.height
        val maxTranslation = parentHeight - peekHeight
        val targetTranslation = maxTranslation * (1 - offset)
        
        child.animate()
            .translationY(targetTranslation)
            .setDuration(300)
            .start()
    }
} 