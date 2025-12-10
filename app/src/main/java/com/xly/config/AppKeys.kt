package com.xly.config

/**
 * 应用内各第三方 AppKey 统一管理
 *
 * 说明：
 * - 在此集中维护所有渠道/环境的 AppKey，方便后期切换和审计
 * - 如有多环境需求，可按需扩展（如 Debug/Release 不同 Key）
 */
object AppKeys {
    // 友盟统计 / APM
    const val MENGA_APP_KEY = "693919fe9a7f376488fbb6c9"

    // TODO: 如有其他平台的 Key，请在此补充，例如：
    // const val WECHAT_APP_ID = ""
    // const val QQ_APP_ID = ""
    // const val MAP_APP_KEY = ""
}

