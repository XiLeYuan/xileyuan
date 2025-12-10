package com.xly.middlelibrary.net

import com.xly.middlelibrary.LYApiService
import com.xly.middlelibrary.utils.JsonResponseConverterFactory
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

// LYHttpClient.kt - 改进后的HTTP客户端
object LYHttpClient {

    //CY本地"http://192.168.1.6:9090/" CL本地："http://192.168.1.102:9090/"
    private const val BASE_URL = "http://192.168.3.143:8080/"

    // 日志拦截器
    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    // 认证拦截器
    private val authInterceptor = Interceptor { chain ->
        val originalRequest = chain.request()

        // 从SharedPreferences获取token
        val token = getStoredToken()

        val newRequest = if (token != null) {
            originalRequest.newBuilder()
                .header("Authorization", "Bearer $token")
                .build()
        } else {
            originalRequest
        }

        chain.proceed(newRequest)
    }

    // 错误处理拦截器
    private val errorInterceptor = Interceptor { chain ->
        val request = chain.request()
        val response = chain.proceed(request)

        when (response.code) {
            401 -> {
                // Token过期，清除本地token
                clearStoredToken()
                // 可以在这里跳转到登录页面
            }
            403 -> {
                // 权限不足
            }
            500 -> {
                // 服务器错误
            }
        }

        response
    }

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
//        .addInterceptor(authInterceptor)
//        .addInterceptor(errorInterceptor)
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    val instance: LYApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(JsonResponseConverterFactory())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(LYApiService::class.java)
    }

    // Token管理
    private fun getStoredToken(): String? {
        // 从SharedPreferences获取token
        return null // 实现你的token获取逻辑
    }

    private fun clearStoredToken() {
        // 清除本地token
    }
}