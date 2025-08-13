package com.xly.middlelibrary.net

data class LYResponse<T>(
    var success: Boolean = false,
    var message: String? = null,
    val data: T? = null,
    var timestamp: Long = 0L,
    var rawJson: String? = null
) {
    // 扩展属性
    val isSuccessful: Boolean get() = success
    val errorMessage: String? get() = if (success) null else message

    // 安全获取数据的方法
    fun getDataOrThrow(): T {
        return if (isSuccessful && data != null) {
            data
        } else {
            throw Exception(errorMessage ?: "未知错误")
        }
    }

    fun getDataOrNull(): T? = data
}