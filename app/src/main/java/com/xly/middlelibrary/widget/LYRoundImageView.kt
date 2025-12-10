package com.xly.middlelibrary.widget



import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapShader
import android.graphics.Canvas
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.Shader
import android.graphics.drawable.BitmapDrawable
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageView

class LYRoundImageView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : AppCompatImageView(context, attrs, defStyleAttr) {

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private var radius = 0f

    override fun onDraw(canvas: Canvas) {
        val drawable = drawable ?: return
        val bitmap = drawableToBitmap(drawable) ?: return

        val viewWidth = width
        val viewHeight = height
        radius = (minOf(viewWidth, viewHeight) / 2).toFloat()

        // 计算缩放
        val scale = radius * 2 / minOf(bitmap.width, bitmap.height)
        val matrix = Matrix()
        matrix.setScale(scale, scale)

        val shader = BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP)
        shader.setLocalMatrix(matrix)
        paint.shader = shader

        canvas.drawCircle(viewWidth / 2f, viewHeight / 2f, radius, paint)
    }

    private fun drawableToBitmap(drawable: android.graphics.drawable.Drawable): Bitmap? {
        if (drawable is BitmapDrawable) {
            return drawable.bitmap
        }
        val w = drawable.intrinsicWidth.takeIf { it > 0 } ?: width
        val h = drawable.intrinsicHeight.takeIf { it > 0 } ?: height
        if (w <= 0 || h <= 0) return null
        val bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, w, h)
        drawable.draw(canvas)
        return bitmap
    }
}
