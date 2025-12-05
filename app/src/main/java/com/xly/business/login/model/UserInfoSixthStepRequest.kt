package com.xly.business.login.model

/**
 * 第六步基本资料填写请求实体（身份验证）
 */
data class UserInfoSixthStepRequest(
    /**
     * 真实姓名
     */
    var realName: String = "",
    
    /**
     * 身份证号
     */
    var idCardNumber: String = ""
) {
    companion object {
        /**
         * 18位身份证号正则表达式
         * 前17位为数字，最后一位为数字或X（大小写均可）
         */
        private val ID_CARD_18_REGEX = Regex("^[1-9]\\d{5}(18|19|20)\\d{2}(0[1-9]|1[0-2])(0[1-9]|[12]\\d|3[01])\\d{3}[0-9Xx]$")
        
        /**
         * 15位身份证号正则表达式（旧版）
         * 15位数字
         */
        private val ID_CARD_15_REGEX = Regex("^[1-9]\\d{5}\\d{2}(0[1-9]|1[0-2])(0[1-9]|[12]\\d|3[01])\\d{3}$")
    }
    
    /**
     * 验证必填字段
     * 姓名和身份证号均为必填
     */
    fun validate(): Boolean {
        return realName.isNotEmpty() && idCardNumber.isNotEmpty()
    }
    
    /**
     * 验证身份证号格式（使用正则表达式）
     * 支持18位新身份证和15位旧身份证
     */
    fun validateIdCardFormat(): Boolean {
        if (idCardNumber.isEmpty()) {
            return false
        }
        
        // 去除空格
        val trimmedIdCard = idCardNumber.trim()
        
        // 18位身份证号验证
        if (trimmedIdCard.length == 18) {
            return ID_CARD_18_REGEX.matches(trimmedIdCard)
        }
        
        // 15位身份证号验证（旧版）
        if (trimmedIdCard.length == 15) {
            return ID_CARD_15_REGEX.matches(trimmedIdCard)
        }
        
        return false
    }
    
    /**
     * 完整验证（格式+必填）
     */
    fun validateAll(): Boolean {
        return validate() && validateIdCardFormat()
    }
}

