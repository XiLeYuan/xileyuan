package com.xly.business.login.model

/**
 * 第五步基本资料填写请求实体（择偶要求）
 */
data class UserInfoFifthStepRequest(
    /**
     * 择偶描述（希望他）
     */
    var idealPartner: String = "",
    
    /**
     * 期望年龄最小值
     */
    var preferredAgeMin: Int = 0,
    
    /**
     * 期望年龄最大值
     */
    var preferredAgeMax: Int = 0,
    
    /**
     * 期望身高最小值（单位：cm）
     */
    var preferredHeightMin: Int = 0,
    
    /**
     * 期望身高最大值（单位：cm）
     */
    var preferredHeightMax: Int = 0
) {
    /**
     * 验证必填字段
     * 择偶描述为必填，年龄和身高范围为可选
     */
    fun validate(): Boolean {
        return idealPartner.isNotEmpty()
    }
    
    /**
     * 验证年龄范围是否有效
     */
    fun validateAgeRange(): Boolean {
        if (preferredAgeMin == 0 && preferredAgeMax == 0) {
            return true // 年龄范围可选
        }
        return preferredAgeMin > 0 && preferredAgeMax > 0 && preferredAgeMin <= preferredAgeMax
    }
    
    /**
     * 验证身高范围是否有效
     */
    fun validateHeightRange(): Boolean {
        if (preferredHeightMin == 0 && preferredHeightMax == 0) {
            return true // 身高范围可选
        }
        return preferredHeightMin > 0 && preferredHeightMax > 0 && preferredHeightMin <= preferredHeightMax
    }
}

