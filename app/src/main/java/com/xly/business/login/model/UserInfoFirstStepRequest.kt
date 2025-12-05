package com.xly.business.login.model

/**
 * 第一步基本资料填写请求实体
 */
data class UserInfoFirstStepRequest(
    /**
     * 昵称
     */
    var nickname: String = "",
    
    /**
     * 性别：1-男，2-女
     */
    var gender: String = "",
    
    /**
     * 年龄
     */
    var age: Int = 0,
    
    /**
     * 身高（单位：cm）
     */
    var height: Int = 0,
    
    /**
     * 家乡省份
     */
    var hometownProvince: String = "",
    
    /**
     * 家乡城市
     */
    var hometownCity: String = "",
    
    /**
     * 家乡区县
     */
    var hometownDistrict: String = "",
    
    /**
     * 现居住地省份
     */
    var residenceProvince: String = "",
    
    /**
     * 现居住地城市
     */
    var residenceCity: String = "",
    
    /**
     * 现居住地区县
     */
    var residenceDistrict: String = "",
    
    /**
     * 头像地址（URL）
     */
    var avatarUrl: String = ""
) {
    /**
     * 获取家乡完整地址（省 市 区）
     */
    fun getHometownFullAddress(): String {
        return "$hometownProvince $hometownCity $hometownDistrict"
    }
    
    /**
     * 获取现居住地完整地址（省 市 区）
     */
    fun getResidenceFullAddress(): String {
        return "$residenceProvince $residenceCity $residenceDistrict"
    }
    
    /**
     * 验证必填字段
     */
    fun validate(): Boolean {
        return nickname.isNotEmpty() &&
                gender.isNotEmpty() &&
                age > 0 &&
                height > 0 &&
                hometownProvince.isNotEmpty() &&
                hometownCity.isNotEmpty() &&
                hometownDistrict.isNotEmpty() &&
                residenceProvince.isNotEmpty() &&
                residenceCity.isNotEmpty() &&
                residenceDistrict.isNotEmpty()
    }
    
    /**
     * 验证必填字段（不包含头像，头像为可选）
     */
    fun validateWithoutAvatar(): Boolean {
        return validate()
    }
}

