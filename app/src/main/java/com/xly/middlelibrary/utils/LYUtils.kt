package com.xly.middlelibrary.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.renderscript.Allocation
import android.renderscript.Element
import android.renderscript.RenderScript
import android.renderscript.ScriptIntrinsicBlur

object LYUtils {

    fun getRandomColor(): Int {
        val rnd = java.util.Random()
        val red = rnd.nextInt(256)
        val green = rnd.nextInt(256)
        val blue = rnd.nextInt(256)
        return android.graphics.Color.rgb(red, green, blue)
    }
    fun createColorBitmap(color: Int, width: Int, height: Int): Bitmap {
        val bmp = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bmp)
        canvas.drawColor(color)
        return bmp
    }


    fun blurBitmap(context: Context, bitmap: Bitmap, radius: Float = 20f): Bitmap {
        val renderScript = RenderScript.create(context)
        val input = Allocation.createFromBitmap(renderScript, bitmap)
        val output = Allocation.createTyped(renderScript, input.type)
        val script = ScriptIntrinsicBlur.create(renderScript, Element.U8_4(renderScript))
        script.setRadius(radius)
        script.setInput(input)
        script.forEach(output)
        output.copyTo(bitmap)
        renderScript.destroy()
        return bitmap
    }

}