package com.xly.middlelibrary.utils

import android.content.Context
import android.graphics.Typeface


object LYFontUtil {

    fun getMediumFont(context: Context): Typeface {
        // 使用系统字体，无需打包任何文件
        return Typeface.create("sans-serif-medium", Typeface.NORMAL)
    }

    fun getBoldFont(context: Context): Typeface {
        return Typeface.create("sans-serif", Typeface.BOLD)
    }

    fun getRegularFont(context: Context): Typeface {
        return Typeface.create("sans-serif", Typeface.NORMAL)
    }
}