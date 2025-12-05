package com.xly.business.login.model

/**
 * 第三步基本资料填写请求实体
 */
data class UserInfoThirdStepRequest(
    /**
     * 房产情况：有房产、无房产
     */
    var houseStatus: String = "",
    
    /**
     * 车辆情况：有车、无车
     */
    var carStatus: String = "",
    
    /**
     * 婚姻情况：未婚、离异、丧偶
     */
    var maritalStatus: String = "",
    
    /**
     * 子女情况：无子女、有子女，和我一起生活、有子女，不和我一起生活
     */
    var childrenStatus: String = ""
) {
    /**
     * 获取房产情况类型编码
     * 1-有房产，2-无房产
     */
    fun getHouseStatusType(): Int {
        return when (houseStatus) {
            "有房产" -> 1
            "无房产" -> 2
            else -> 0
        }
    }
    
    /**
     * 获取车辆情况类型编码
     * 1-有车，2-无车
     */
    fun getCarStatusType(): Int {
        return when (carStatus) {
            "有车" -> 1
            "无车" -> 2
            else -> 0
        }
    }
    
    /**
     * 获取婚姻情况类型编码
     * 1-未婚，2-离异，3-丧偶
     */
    fun getMaritalStatusType(): Int {
        return when (maritalStatus) {
            "未婚" -> 1
            "离异" -> 2
            "丧偶" -> 3
            else -> 0
        }
    }
    
    /**
     * 获取子女情况类型编码
     * 1-无子女，2-有子女，和我一起生活，3-有子女，不和我一起生活
     */
    fun getChildrenStatusType(): Int {
        return when (childrenStatus) {
            "无子女" -> 1
            "有子女，和我一起生活" -> 2
            "有子女，不和我一起生活" -> 3
            else -> 0
        }
    }
    
    /**
     * 验证必填字段
     * 所有字段均为必填
     */
    fun validate(): Boolean {
        return houseStatus.isNotEmpty() &&
                carStatus.isNotEmpty() &&
                maritalStatus.isNotEmpty() &&
                childrenStatus.isNotEmpty()
    }
}

