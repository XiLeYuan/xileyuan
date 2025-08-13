package com.xly.middlelibrary.net

// NetworkUtils.kt - 网络工具类
object NetworkUtils {

    // 修复的安全API调用
    suspend fun <T> safeApiCall(
        call: suspend () -> LYResponse<T>
    ): Result<T> {
        return try {
            val response = call()
            Result.success(response.getDataOrThrow())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // 带原始JSON的安全API调用
    suspend fun <T> safeApiCallWithRawJson(
        call: suspend () -> LYResponse<T>
    ): Result<Pair<T, String?>> {
        return try {
            val response = call()
            Result.success(Pair(response.getDataOrThrow(), response.rawJson))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}