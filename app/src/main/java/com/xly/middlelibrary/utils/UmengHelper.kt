package com.xly.middlelibrary.utils

import android.content.Context
import com.umeng.analytics.MobclickAgent
import com.umeng.commonsdk.UMConfigure

/**
 * 友盟统计工具类
 * 提供统一的友盟统计接口
 */
object UmengHelper {

    /**
     * 初始化友盟SDK
     * @param context Application上下文
     * @param appKey 友盟AppKey
     * @param channel 渠道名称
     * @param isDebug 是否开启调试模式（开发阶段建议开启）
     */
    fun init(
        context: Context,
        appKey: String,
        channel: String = "default",
        isDebug: Boolean = false
    ) {
        // 初始化友盟SDK
        UMConfigure.init(
            context,
            appKey,
            channel,
            UMConfigure.DEVICE_TYPE_PHONE,
            null
        )

        // 设置日志开关（仅在开发阶段开启）
        UMConfigure.setLogEnabled(isDebug)

        // 设置场景类型（可选）
        MobclickAgent.setScenarioType(context, MobclickAgent.EScenarioType.E_UM_NORMAL)

        // 设置是否对日志信息进行加密（可选）
        MobclickAgent.enableEncrypt(false)
    }

    /**
     * 页面统计 - 页面开始
     * @param context 上下文
     * @param pageName 页面名称
     */
    fun onPageStart(context: Context, pageName: String) {
        MobclickAgent.onPageStart(pageName)
    }

    /**
     * 页面统计 - 页面结束
     * @param context 上下文
     * @param pageName 页面名称
     */
    fun onPageEnd(context: Context, pageName: String) {
        MobclickAgent.onPageEnd(pageName)
    }

    /**
     * 事件统计
     * @param context 上下文
     * @param eventId 事件ID
     */
    fun onEvent(context: Context, eventId: String) {
        MobclickAgent.onEvent(context, eventId)
    }

    /**
     * 事件统计（带参数）
     * @param context 上下文
     * @param eventId 事件ID
     * @param map 事件参数Map
     */
    fun onEvent(context: Context, eventId: String, map: Map<String, String>) {
        MobclickAgent.onEvent(context, eventId, map)
    }

    /**
     * 自定义事件统计（带数值）
     * @param context 上下文
     * @param eventId 事件ID
     * @param map 事件参数Map
     * @param value 事件数值（Int类型）
     */
    fun onEventValue(context: Context, eventId: String, map: Map<String, String>, value: Int) {
        MobclickAgent.onEventValue(context, eventId, map, value)
    }
    
    /**
     * 自定义事件统计（带数值，Long类型，自动转换为Int）
     * @param context 上下文
     * @param eventId 事件ID
     * @param map 事件参数Map
     * @param value 事件数值（Long类型，会转换为Int）
     */
    fun onEventValue(context: Context, eventId: String, map: Map<String, String>, value: Long) {
        // 将Long转换为Int，注意可能会溢出
        MobclickAgent.onEventValue(context, eventId, map, value.toInt())
    }

    /**
     * 账号统计 - 登录
     * @param userId 用户ID
     */
    fun onProfileSignIn(userId: String) {
        MobclickAgent.onProfileSignIn(userId)
    }

    /**
     * 账号统计 - 登录（带Provider）
     * @param userId 用户ID
     * @param provider 账号来源（如：weixin、qq等）
     */
    fun onProfileSignIn(userId: String, provider: String) {
        MobclickAgent.onProfileSignIn(provider, userId)
    }

    /**
     * 账号统计 - 登出
     */
    fun onProfileSignOff() {
        MobclickAgent.onProfileSignOff()
    }

    /**
     * 设置用户属性
     * 注意：友盟SDK新版本已移除setUserGender和setUserAge方法
     * 可以通过事件统计的方式记录用户属性信息
     * @param context 上下文
     * @param userId 用户ID
     * @param sex 性别（M：男，F：女）
     * @param age 年龄
     */
    fun setUserInfo(context: Context, userId: String, sex: String? = null, age: Int? = null) {
        // 使用事件统计的方式记录用户属性
        val userInfoMap = mutableMapOf<String, String>()
        userInfoMap["user_id"] = userId
        if (sex != null) {
            userInfoMap["gender"] = sex
        }
        if (age != null) {
            userInfoMap["age"] = age.toString()
        }
        // 记录用户属性事件
        MobclickAgent.onEvent(context, "user_info_update", userInfoMap)
    }

    /**
     * 设置用户标签
     * @param context 上下文
     * @param tag 标签
     */
    fun setUserTag(context: Context, tag: String) {
        MobclickAgent.onEvent(context, "user_tag", mapOf("tag" to tag))
    }

    /**
     * 错误统计
     * @param context 上下文
     * @param error 错误信息
     */
    fun reportError(context: Context, error: String) {
        MobclickAgent.reportError(context, error)
    }

    /**
     * 错误统计（带异常）
     * @param context 上下文
     * @param throwable 异常对象
     */
    fun reportError(context: Context, throwable: Throwable) {
        MobclickAgent.reportError(context, throwable)
    }
}

