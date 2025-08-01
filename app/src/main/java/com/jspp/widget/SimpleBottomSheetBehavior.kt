package com.jspp.widget

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.ViewCompat
import com.google.android.material.bottomsheet.BottomSheetBehavior

class SimpleBottomSheetBehavior<V : View> : BottomSheetBehavior<V> {

    constructor() : super()
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    private var onSlideListener: ((Float) -> Unit)? = null
    private var currentChild: V? = null

    fun setOnSlideListener(listener: (Float) -> Unit) {
        onSlideListener = listener
    }

    override fun onLayoutChild(parent: CoordinatorLayout, child: V, layoutDirection: Int): Boolean {
        val handled = super.onLayoutChild(parent, child, layoutDirection)
        
        // 保存child引用
        currentChild = child
        
        // 确保初始位置正确
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
        
        // 计算滑动偏移量
        val slideOffset = getSlideOffset(child, coordinatorLayout)
        onSlideListener?.invoke(slideOffset)
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
     * 获取当前滑动偏移量
     */
    fun getCurrentSlideOffset(): Float {
        val child = currentChild ?: return 0f
        val parent = child.parent as? ViewGroup ?: return 0f
        return getSlideOffset(child, parent)
    }
} 