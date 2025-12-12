package com.xly.middlelibrary.net

import retrofit2.HttpException
import okhttp3.ResponseBody
import com.google.gson.Gson
import java.io.IOException

// NetworkUtils.kt - 网络工具类
object NetworkUtils {

    // 修复的安全API调用
    suspend fun <T> safeApiCall(
        call: suspend () -> LYResponse<T>
    ): Result<T> {
        return try {
            val response = call()
            Result.success(response.getDataOrThrow())
        } catch (e: HttpException) {
            // 处理HTTP错误状态码（如400、500等）
            // 尝试从响应体中提取错误信息
            val errorBody = e.response()?.errorBody()
            if (errorBody != null) {
                try {
                    val jsonString = errorBody.string()
                    val gson = Gson()
                    // 使用 TypeToken 来正确解析泛型类型
                    val type = com.google.gson.reflect.TypeToken.getParameterized(
                        LYResponse::class.java,
                        Any::class.java
                    ).type
                    val errorResponse = gson.fromJson<LYResponse<Any>>(jsonString, type)
                    val errorMessage = errorResponse.message ?: "请求失败: HTTP ${e.code()}"
                    Result.failure(Exception(errorMessage))
                } catch (parseException: Exception) {
                    Result.failure(Exception("请求失败: HTTP ${e.code()}"))
                }
            } else {
                Result.failure(Exception("请求失败: HTTP ${e.code()}"))
            }
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