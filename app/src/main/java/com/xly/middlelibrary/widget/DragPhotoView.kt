package com.xly.middlelibrary.widget


import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.widget.FrameLayout
import com.github.chrisbanes.photoview.PhotoView
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

class DragPhotoView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    private val photoView = PhotoView(context)
    private val paint = Paint().apply {
        color = Color.BLACK
        alpha = 255
    }

    private var downY = 0f
    private var downX = 0f
    private var currentY = 0f
    private var currentX = 0f
    private var isDragging = false
    private var onDragListener: OnDragListener? = null

    init {
        addView(photoView, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
        setBackgroundColor(Color.TRANSPARENT)
    }

    // 添加获取 PhotoView 的方法
    fun getPhotoView(): PhotoView {
        return photoView
    }

    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        when (ev.action) {
            MotionEvent.ACTION_DOWN -> {
                downY = ev.y
                downX = ev.x
                currentY = downY
                currentX = downX
                isDragging = false
            }
            MotionEvent.ACTION_MOVE -> {
                val deltaY = ev.y - downY
                val deltaX = ev.x - downX

                // 判断是否为下拉手势
                if (abs(deltaY) > abs(deltaX) && deltaY > 50) {
                    isDragging = true
                    return true // 拦截事件
                }
            }
        }
        return super.onInterceptTouchEvent(ev)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_MOVE -> {
                if (isDragging) {
                    currentY = event.y
                    currentX = event.x

                    val deltaY = currentY - downY
                    val progress = min(1f, deltaY / 500f)
                    updateDragProgress(progress)
                    return true
                }
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                if (isDragging) {
                    val deltaY = currentY - downY
                    if (deltaY > 200) {
                        onDragListener?.onDragClose()
                    } else {
                        resetPosition()
                    }
                    isDragging = false
                    return true
                }
            }
        }
        return super.onTouchEvent(event)
    }

    private fun updateDragProgress(progress: Float) {
        val alpha = (255 * (1 - progress)).toInt()
        paint.alpha = max(0, alpha)
        Log.i("alpha","" + alpha)
        val scale = 1f - progress * 0.3f
        val translateY = (currentY - downY) * 0.5f
        val translateX = (currentX - downX) * 0.5f

        photoView.scaleX = scale
        photoView.scaleY = scale
        photoView.translationX = translateX
        photoView.translationY = translateY

        invalidate()
    }

    private fun resetPosition() {
        photoView.animate()
            .scaleX(1f)
            .scaleY(1f)
            .translationX(0f)
            .translationY(0f)
            .setDuration(200)
            .start()

        paint.alpha = 255
        invalidate()
    }

    override fun dispatchDraw(canvas: Canvas) {
//        canvas.drawColor(paint.color)
        canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), paint)
        super.dispatchDraw(canvas)

    }

    fun setOnDragListener(listener: OnDragListener) {
        onDragListener = listener
    }

    interface OnDragListener {
        fun onDragClose()
    }
}