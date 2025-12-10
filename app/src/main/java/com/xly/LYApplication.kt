package com.xly

import android.app.Application
import com.tencent.mmkv.MMKV
import com.xly.config.AppKeys
import com.xly.middlelibrary.utils.UmengHelper

class LYApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        
        // 初始化MMKV
        MMKV.initialize(this)
        
        // 初始化友盟SDK（必须先初始化基础SDK）
        UmengHelper.init(
            context = this,
            appKey = AppKeys.MENGA_APP_KEY,
            channel = getChannelName(), // 使用渠道名称
            isDebug = true // 开发环境开启日志
        )
        

    }
    
    /**
     * 获取渠道名称
     * 从manifestPlaceholders中获取UMENG_CHANNEL_VALUE
     */
    private fun getChannelName(): String {
        return try {
            val channel = packageManager.getApplicationInfo(
                packageName,
                android.content.pm.PackageManager.GET_META_DATA
            ).metaData?.getString("UMENG_CHANNEL") ?: "default"
            channel
        } catch (e: Exception) {
            "default"
        }
    }
}