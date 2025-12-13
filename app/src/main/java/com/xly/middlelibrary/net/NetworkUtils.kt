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
            val httpCode = e.code()
            val errorBody = e.response()?.errorBody()
            val contentType = e.response()?.headers()?.get("Content-Type") ?: ""
            
            // 根据HTTP状态码提供友好的错误提示
            val friendlyMessage = when (httpCode) {
                502 -> "服务器网关错误，请稍后重试"
                503 -> "服务暂时不可用，请稍后重试"
                504 -> "服务器响应超时，请稍后重试"
                500 -> "服务器内部错误，请稍后重试"
                404 -> "请求的资源不存在"
                403 -> "没有权限访问"
                401 -> "未授权，请重新登录"
                400 -> "请求参数错误"
                else -> null
            }
            
            if (errorBody != null) {
                try {
                    val jsonString = errorBody.string()
                    
                    // 检查Content-Type，如果是HTML则不尝试解析JSON
                    if (contentType.contains("text/html", ignoreCase = true)) {
                        // 返回友好的错误消息，而不是尝试解析HTML
                        val message = friendlyMessage ?: "服务器错误: HTTP $httpCode"
                        Result.failure(Exception(message))
                    } else {
                        // 尝试解析JSON响应
                        val gson = Gson()
                        val type = com.google.gson.reflect.TypeToken.getParameterized(
                            LYResponse::class.java,
                            Any::class.java
                        ).type
                        val errorResponse = gson.fromJson<LYResponse<Any>>(jsonString, type)
                        val errorMessage = errorResponse.message ?: friendlyMessage ?: "请求失败: HTTP $httpCode"
                        Result.failure(Exception(errorMessage))
                    }
                } catch (parseException: Exception) {
                    // JSON解析失败，使用友好的错误消息
                    val message = friendlyMessage ?: "请求失败: HTTP $httpCode"
                    Result.failure(Exception(message))
                }
            } else {
                val message = friendlyMessage ?: "请求失败: HTTP $httpCode"
                Result.failure(Exception(message))
            }
        } catch (e: IOException) {
            // 网络连接错误
            Result.failure(Exception("网络连接失败，请检查网络设置"))
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