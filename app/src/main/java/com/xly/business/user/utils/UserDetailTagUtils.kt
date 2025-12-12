package com.xly.business.user.utils

import android.widget.TextView
import androidx.core.content.ContextCompat
import com.google.android.flexbox.FlexboxLayout
import com.xly.R

/**
 * 用户详情页标签工具类
 */
object UserDetailTagUtils {
    
    /**
     * 设置基本信息标签
     * @param flexboxLayout 标签容器
     * @param tags 标签文本列表
     */
    fun setupBasicInfoTags(flexboxLayout: FlexboxLayout, tags: List<String>) {
        flexboxLayout.removeAllViews()
        
        val density = flexboxLayout.resources.displayMetrics.density
        
        tags.forEach { tagText ->
            val tagView = TextView(flexboxLayout.context).apply {
                text = tagText
                textSize = 14f
                setTextColor(ContextCompat.getColor(context, R.color.text_primary_dark))
                background = ContextCompat.getDrawable(context, R.drawable.bg_profile_info_tag)
                setPadding(
                    (12 * density).toInt(),
                    (8 * density).toInt(),
                    (12 * density).toInt(),
                    (8 * density).toInt()
                )
                
                // 使用 FlexboxLayout.LayoutParams 支持自动换行
                val layoutParams = FlexboxLayout.LayoutParams(
                    FlexboxLayout.LayoutParams.WRAP_CONTENT,
                    FlexboxLayout.LayoutParams.WRAP_CONTENT
                ).apply {
                    setMargins(
                        0,
                        0,
                        (8 * density).toInt(),
                        (8 * density).toInt()
                    )
                }
                this.layoutParams = layoutParams
            }
            flexboxLayout.addView(tagView)
        }
    }
    
    /**
     * 创建示例基本信息标签数据
     */
    fun getSampleBasicInfoTags(): List<String> {
        return listOf(
            "软件工程师",
            "本科",
            "165cm",
            "28岁",
            "北京",
            "已购车",
            "已购房"
        )
    }
}

