package com.xly.business.login.model

/**
 * 第二步基本资料填写请求实体
 */
data class UserInfoSecondStepRequest(
    /**
     * 学历：初中及以下、高中/中专、大专、本科、硕士、博士
     */
    var education: String = "",
    
    /**
     * 学校（选填）
     */
    var school: String = "",
    
    /**
     * 职业
     */
    var occupation: String = "",
    
    /**
     * 收入：3000元以下、3000-5000元、5000-8000元、8000-12000元、
     * 12000-20000元、20000-30000元、30000-50000元、50000元以上
     */
    var incomeLevel: String = ""
) {
    /**
     * 将学历转换为数字等级
     * 1-初中及以下，2-高中/中专，3-大专，4-本科，5-硕士，6-博士
     */
    fun getEducationLevel(): Int {
        return when (education) {
            "初中及以下" -> 1
            "高中/中专" -> 2
            "大专" -> 3
            "本科" -> 4
            "硕士" -> 5
            "博士" -> 6
            else -> 0
        }
    }
    
    /**
     * 验证必填字段
     * 学历、职业、收入为必填，学校为选填
     */
    fun validate(): Boolean {
        return education.isNotEmpty() &&
                occupation.isNotEmpty() &&
                incomeLevel.isNotEmpty()
    }
}

