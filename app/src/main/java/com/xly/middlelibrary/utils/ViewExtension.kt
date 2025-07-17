package com.xly.middlelibrary.utils

import android.annotation.SuppressLint
import android.app.Activity
import android.graphics.Outline
import android.graphics.Rect
import android.graphics.RectF
import android.graphics.drawable.Drawable
import android.os.Build
import android.util.Log
import android.view.GestureDetector
import android.view.GestureDetector.SimpleOnGestureListener
import android.view.MotionEvent
import android.view.TouchDelegate
import android.view.View
import android.view.ViewGroup
import android.view.ViewOutlineProvider
import android.view.ViewParent
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.graphics.drawable.DrawableCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.children
import androidx.core.view.forEach
import com.google.android.material.bottomnavigation.BottomNavigationView


fun View.gone() {
    visibility = View.GONE
}

fun View.invisible() {
    visibility = View.INVISIBLE
}

fun View.visible() {
    visibility = View.VISIBLE
}

fun View.show(show: Boolean): Boolean {
    if (show) visible() else gone()
    return show
}

fun View.visible(visible: Boolean): Boolean {
    if (visible) visible() else invisible()
    return visible
}

/**
 * 把控件从其父布局中移除
 */
fun View.removeFromParent() {
    if (parent is ViewGroup) {
        (parent as ViewGroup).removeView(this)
    }
}

private var lastHashCode = 0

/**
 * 防止快速点击
 * @param delayTime 点击间隔时间
 */
fun View.throttleFirst(delayTime: Long = 500, listener: (view: View) -> Unit) {
    var lastClickTime: Long = 0

    this.setOnClickListener {
        val curClickTime = System.currentTimeMillis()
        val hashCode = it.hashCode()
        // 大于等于设置的时间,或者 不是同一个view, 可以触发事件点击
        if (curClickTime - lastClickTime >= delayTime || hashCode != lastHashCode) {
            listener(it)
        }
        lastHashCode = hashCode
        lastClickTime = curClickTime
    }
}

fun View.click(delayTime: Long = 500, listener: (view: View) -> Unit) {
    var lastClickTime: Long = 0

    this.setOnClickListener {
        val curClickTime = System.currentTimeMillis()
        val hashCode = it.hashCode()
        // 大于等于设置的时间,或者 不是同一个view, 可以触发事件点击
        if (curClickTime - lastClickTime >= delayTime || hashCode != lastHashCode) {
            listener(it)
        }
        lastHashCode = hashCode
        lastClickTime = curClickTime
    }
}


@SuppressLint("ClickableViewAccessibility")
fun View.clickScaleEffect(listener: (view: View) -> Unit) {
    isClickable = true
    setOnTouchListener { v, event ->
        Log.d("===ljp", "action ${event.action}")
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                ViewCompat.animate(v).setDuration(100)
                    .scaleX(0.9f)
                    .scaleY(0.9f)
                    .start()
            }

            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                ViewCompat.animate(v).setDuration(100)
                    .scaleX(1f)
                    .scaleY(1f)
                    .start()
                if (event.action == MotionEvent.ACTION_UP)
                    listener(v)
            }
        }
        false
    }
}

/**
 * 创建一个带圆角的Rectangle背景
 * @param radius 圆角值
 */
fun View.setRoundRect(radius: Float) {
    outlineProvider = object : ViewOutlineProvider() {
        override fun getOutline(view: View?, outline: Outline?) {
            outline?.setRoundRect(0, 0, view?.width ?: 0, view?.height ?: 0, radius)
        }
    }
    clipToOutline = true
}

/**
 * 多个控件有相同的点击事件
 */
fun multiViewOnClickListener(vararg viewArray: View, action: (View) -> Unit) {
    for (view in viewArray) {
        view.throttleFirst {
            action(it)
        }
    }
}

fun ConstraintLayout.setStyleVerticalBias(bias: Float, vararg viewId: Int) {
    val constraintSet = ConstraintSet()
    constraintSet.clone(this)
    viewId.forEach {
        constraintSet.setVerticalBias(it, bias)
    }
    constraintSet.applyTo(this)
}

fun ConstraintLayout.setStyleHorizontalBias(bias: Float, vararg viewId: Int) {
    val constraintSet = ConstraintSet()
    constraintSet.clone(this)
    viewId.forEach {
        constraintSet.setHorizontalBias(it, bias)
    }
    constraintSet.applyTo(this)
}

/**
 * 添加拦截view，防止触摸屏幕
 */

/**
 * 移除拦截view
 */
fun removeInterceptView(activity: Activity) {
    val rootView = activity.window.decorView.rootView as? ViewGroup
    if (rootView != null) {
        val view = rootView.children.find { it.tag == "interceptView" }
        rootView.removeView(view)
    }
}

/**
 * 获取指定控件的rectF
 */
fun View.getViewRectF(): RectF {
    val width = measuredWidth
    val height = measuredHeight
    val location = IntArray(2)
    getLocationInWindow(location)
    return RectF(
        location[0].toFloat(),
        location[1].toFloat(),
        (width + location[0]).toFloat(),
        (height + location[1]).toFloat(),
    )
}

/**
 * 扩大控件触摸区域
 * @param size 四周扩大大小，单位:px
 */
fun View.expandTouchArea(size: Int) {
    val parentView = parent as View
    parentView.post {
        val rect = Rect()
        getHitRect(rect)
        rect.top -= size
        rect.bottom += size
        rect.left -= size
        rect.right += size
        parentView.touchDelegate = TouchDelegate(rect, this)
    }
}

/**
 * 放大/缩小view
 */
fun View.scaleSize(scale: Float) {
    this.scaleX = scale
    this.scaleY = scale
}

fun View.setDrawableTint(drawable: Drawable?, colorInt: Int) {
    drawable?.let {
        DrawableCompat.wrap(it).mutate().setTint(colorInt)
    }
}

fun View.safeRequestLayout() {
    if (isSafeToRequestDirectly()) {
        requestLayout()
    } else {
        post { requestLayout() }
    }
}

private fun View.isSafeToRequestDirectly(): Boolean {
    return if (isInLayout) {
        // when isInLayout == true and isLayoutRequested == true,
        // means that this layout pass will layout current view which will
        // make currentView.isLayoutRequested == false, and this will let currentView
        // ignored in process handling requests called during last layout pass.
        isLayoutRequested.not()
    } else {
        var ancestorLayoutRequested = false
        var p: ViewParent? = parent
        while (p != null) {
            if (p.isLayoutRequested) {
                ancestorLayoutRequested = true
                break
            }
            p = p.parent
        }
        ancestorLayoutRequested.not()
    }
}

/**
 * 根据设计图的屏幕宽高和设计图的控件宽高来缩放或放大控件
 */


fun BottomNavigationView.clearLongClickEvent() {
    menu.forEach {
        findViewById<View>(it.itemId).setOnLongClickListener { true }
    }
}

/**
 * 禁止 ViewGroup 下所有 View 的点击事件
 */
@SuppressLint("ClickableViewAccessibility")
fun ViewGroup.disableTouchEvents(enable: Boolean) {
    for (i in 0 until childCount) {
        val child = getChildAt(i)
        child.isEnabled = enable
        if (child is ViewGroup) {
            child.disableTouchEvents(enable)
        }
    }
}

/**
 * View长按抬起监听
 * param: 是否长按抬起
 */
@SuppressLint("ClickableViewAccessibility")
fun View.setOnLongPressCallback(intercept: Boolean, callback: (Boolean) -> Unit) {
    var isLongPress = false
    val gestureDetector = GestureDetector(context, object : SimpleOnGestureListener() {
        override fun onDown(e: MotionEvent) = true
        override fun onLongPress(e: MotionEvent) {
            super.onLongPress(e)
            isLongPress = true
            callback.invoke(false)
            if (intercept) {
                parent.requestDisallowInterceptTouchEvent(true)
            }
        }
    })
    setOnTouchListener { _, event ->
        if (event.action == MotionEvent.ACTION_UP && isLongPress) {
            isLongPress = false
            callback.invoke(true)
            parent.requestDisallowInterceptTouchEvent(false)
        }
        gestureDetector.onTouchEvent(event)
    }
}

fun TextView.measureTextWidth(text: String): Float {
    return paint.measureText(text)
}

/**
 * 适配android15 EdgeToEdge导致状态栏or导航栏挡住内容
 * padding优先使用参数中的值
 */
@JvmOverloads
fun View.adaptEdgeToEdge(
    left: Int? = null,
    top: Int? = null,
    right: Int? = null,
    bottom: Int? = null
) {
    if (Build.VERSION.SDK_INT < 35) return
    ViewCompat.setOnApplyWindowInsetsListener(this) { v, insets ->
        val systemBars =
            insets.getInsets(WindowInsetsCompat.Type.systemBars() or WindowInsetsCompat.Type.displayCutout())
        v.setPadding(
            left ?: systemBars.left,
            top ?: systemBars.top,
            right ?: systemBars.right,
            bottom ?: systemBars.bottom
        )
        insets
    }
}
