package com.jspp.widget

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.ViewCompat
import androidx.customview.widget.ViewDragHelper
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

class FloatingCardBehavior<V : View> : CoordinatorLayout.Behavior<V> {

    private var dragHelper: ViewDragHelper? = null
    private var initialY = 0f
    private var currentY = 0f
    private var parentHeight = 0
    private var childHeight = 0
    private var peekHeight = 220
    private var maxDragDistance = 0
    private var onSlideListener: ((Float) -> Unit)? = null
    private var onStateChangeListener: ((Int) -> Unit)? = null
    private var currentChild: V? = null
    
    // 状态常量
    companion object {
        const val STATE_PEEK = 0      // 露出状态
        const val STATE_DRAGGING = 1  // 拖动中
        const val STATE_EXPANDED = 2  // 完全展开
    }
    
    private var currentState = STATE_PEEK

    constructor() : super()
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    fun setPeekHeight(height: Int) {
        peekHeight = height
    }

    fun setOnSlideListener(listener: (Float) -> Unit) {
        onSlideListener = listener
    }

    fun setOnStateChangeListener(listener: (Int) -> Unit) {
        onStateChangeListener = listener
    }

    override fun onLayoutChild(parent: CoordinatorLayout, child: V, layoutDirection: Int): Boolean {
        currentChild = child
        parentHeight = parent.height
        childHeight = child.height
        maxDragDistance = parentHeight - peekHeight
        
        // 设置初始位置
        child.translationY = maxDragDistance.toFloat()
        
        // 初始化ViewDragHelper
        if (dragHelper == null) {
            dragHelper = ViewDragHelper.create(parent, dragCallback)
        }
        
        return true
    }

    override fun onInterceptTouchEvent(parent: CoordinatorLayout, child: V, ev: MotionEvent): Boolean {
        return dragHelper?.shouldInterceptTouchEvent(ev) ?: false
    }

    override fun onTouchEvent(parent: CoordinatorLayout, child: V, ev: MotionEvent): Boolean {
        dragHelper?.processTouchEvent(ev)
        return true
    }

    override fun onStartNestedScroll(
        coordinatorLayout: CoordinatorLayout,
        child: V,
        directTargetChild: View,
        target: View,
        axes: Int,
        type: Int
    ): Boolean {
        return axes and ViewCompat.SCROLL_AXIS_VERTICAL != 0
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
        // 处理嵌套滚动
        if (dy > 0 && child.translationY > 0) {
            // 向上滚动，卡片跟随
            val newTranslationY = max(0f, child.translationY - dy)
            child.translationY = newTranslationY
            consumed[1] = dy
            updateSlideOffset(child)
        } else if (dy < 0 && child.translationY < maxDragDistance) {
            // 向下滚动，卡片跟随
            val newTranslationY = min(maxDragDistance.toFloat(), child.translationY - dy)
            child.translationY = newTranslationY
            consumed[1] = dy
            updateSlideOffset(child)
        }
    }

    private val dragCallback = object : ViewDragHelper.Callback() {
        override fun tryCaptureView(child: View, pointerId: Int): Boolean {
            return true
        }

        override fun clampViewPositionVertical(child: View, top: Int, dy: Int): Int {
            return max(0, min(maxDragDistance, top))
        }

        override fun onViewDragStateChanged(state: Int) {
            when (state) {
                ViewDragHelper.STATE_IDLE -> {
                    // 拖动结束，根据位置决定最终状态
                    val child = getChild() ?: return
                    val translationY = child.translationY
                    
                    when {
                        translationY < maxDragDistance * 0.3f -> {
                            // 接近顶部，完全展开
                            animateToPosition(child, 0f)
                            setState(STATE_EXPANDED)
                        }
                        translationY > maxDragDistance * 0.7f -> {
                            // 接近底部，收起
                            animateToPosition(child, maxDragDistance.toFloat())
                            setState(STATE_PEEK)
                        }
                        else -> {
                            // 中间位置，保持当前位置
                            setState(STATE_PEEK)
                        }
                    }
                }
                ViewDragHelper.STATE_DRAGGING -> {
                    setState(STATE_DRAGGING)
                }
            }
        }

        override fun onViewPositionChanged(changedView: View, left: Int, top: Int, dx: Int, dy: Int) {
            updateSlideOffset(changedView)
        }
    }

    private fun updateSlideOffset(child: View) {
        val slideOffset = 1f - (child.translationY / maxDragDistance)
        onSlideListener?.invoke(slideOffset)
    }

    private fun animateToPosition(child: View, targetY: Float) {
        child.animate()
            .translationY(targetY)
            .setDuration(300)
            .withEndAction {
                updateSlideOffset(child)
            }
            .start()
    }

    private fun setState(state: Int) {
        if (currentState != state) {
            currentState = state
            onStateChangeListener?.invoke(state)
        }
    }

    private fun getChild(): V? {
        return currentChild
    }

    /**
     * 获取当前滑动偏移量
     */
    fun getCurrentSlideOffset(): Float {
        val child = getChild() ?: return 0f
        return 1f - (child.translationY / maxDragDistance)
    }

    /**
     * 平滑滑动到指定位置
     */
    fun smoothSlideTo(offset: Float) {
        val child = getChild() ?: return
        val targetY = maxDragDistance * (1 - offset)
        animateToPosition(child, targetY)
    }

    /**
     * 展开卡片
     */
    fun expand() {
        val child = getChild() ?: return
        animateToPosition(child, 0f)
        setState(STATE_EXPANDED)
    }

    /**
     * 收起卡片
     */
    fun collapse() {
        val child = getChild() ?: return
        animateToPosition(child, maxDragDistance.toFloat())
        setState(STATE_PEEK)
    }
} 