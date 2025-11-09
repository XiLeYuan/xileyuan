package com.xly.base

import android.app.Activity
import android.content.Context
import android.content.Intent

/**
 * Activity栈管理工具类
 * 用于管理Activity栈，清除之前的Activity并跳转到新页面
 */
object ActivityStackManager {
    
    /**
     * 清除所有之前的Activity并跳转到目标Activity
     * 使用FLAG_ACTIVITY_NEW_TASK | FLAG_ACTIVITY_CLEAR_TASK清除整个任务栈
     * 
     * @param context 上下文
     * @param targetActivity 目标Activity的Class
     * @param block Intent配置块，用于设置Intent的额外参数
     */
    fun <T : Activity> startActivityAndClearStack(
        context: Context,
        targetActivity: Class<T>,
        block: (Intent.() -> Unit)? = null
    ) {
        val intent = Intent(context, targetActivity).apply {
            // 清除整个任务栈并创建新任务
            // FLAG_ACTIVITY_NEW_TASK: 在新任务中启动Activity
            // FLAG_ACTIVITY_CLEAR_TASK: 清除任务栈中所有Activity
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            // 执行配置块
            block?.invoke(this)
        }
        context.startActivity(intent)
        
        // 如果context是Activity，finish掉当前Activity
        // 注意：由于使用了CLEAR_TASK，当前Activity也会被清除，但为了保险起见还是调用finish
        (context as? Activity)?.finish()
    }
    
    /**
     * 清除所有之前的Activity并跳转到目标Activity（带参数）
     * @param context 上下文
     * @param targetActivity 目标Activity的Class
     * @param vararg pairs Intent的key-value对
     */
    fun <T : Activity> startActivityAndClearStack(
        context: Context,
        targetActivity: Class<T>,
        vararg pairs: Pair<String, Any?>
    ) {
        val intent = Intent(context, targetActivity).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            pairs.forEach { (key, value) ->
                when (value) {
                    is String -> putExtra(key, value)
                    is Int -> putExtra(key, value)
                    is Boolean -> putExtra(key, value)
                    is Long -> putExtra(key, value)
                    is Float -> putExtra(key, value)
                    is Double -> putExtra(key, value)
                    is Byte -> putExtra(key, value)
                    is Char -> putExtra(key, value)
                    is Short -> putExtra(key, value)
                    else -> value?.let { putExtra(key, it as android.os.Parcelable) }
                }
            }
        }
        context.startActivity(intent)
        (context as? Activity)?.finish()
    }
}
