package com.xly.business.login.model

/**
 * 第四步基本资料填写请求实体
 */
data class UserInfoFourthStepRequest(
    /**
     * 生活照列表（图片URL或路径，最多4张）
     */
    var lifePhotos: List<String> = emptyList(),
    
    /**
     * 个人介绍
     */
    var selfIntroduction: String = ""
) {
    /**
     * 将生活照列表转换为逗号分隔的字符串
     */
    fun getLifePhotosString(): String {
        return lifePhotos.joinToString(",")
    }
    
    /**
     * 从逗号分隔的字符串解析生活照列表
     */
    fun setLifePhotosFromString(photosString: String) {
        if (photosString.isNotEmpty()) {
            lifePhotos = photosString.split(",").map { it.trim() }.filter { it.isNotEmpty() }
        } else {
            lifePhotos = emptyList()
        }
    }
    
    /**
     * 验证必填字段
     * 个人介绍为必填，生活照为可选（最多4张）
     */
    fun validate(): Boolean {
        return selfIntroduction.isNotEmpty() && lifePhotos.size <= 4
    }
}

