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
    // 注意：Android设备无法访问127.0.0.1，需要使用以下地址：
    // - 模拟器：使用 10.0.2.2 访问PC的localhost
    // - 真机：使用PC的局域网IP地址（如 192.168.x.x）
    // 当前PC IP: 192.168.20.121（根据实际情况修改）
    private const val BASE_URL = "http://192.168.20.121:8080/"  // 真机使用PC的局域网IP
    // private const val BASE_URL = "http://10.0.2.2:8080/"  // 模拟器使用此地址
    // private const val BASE_URL = "http://127.0.0.1:8080/"  // 仅PC浏览器可用，Android设备不可用

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

    // 错误处理拦截器 - 处理HTTP错误状态码，允许解析响应体
    private val errorInterceptor = Interceptor { chain ->
        val request = chain.request()
        val response = chain.proceed(request)

        // 对于400等错误状态码，如果响应体是JSON格式，允许继续解析
        // 这样可以让JsonResponseConverter正常解析服务器返回的错误信息
        when (response.code) {
            401 -> {
                // Token过期，清除本地token
                clearStoredToken()
                // 可以在这里跳转到登录页面
            }
            403 -> {
                // 权限不足
            }
            400, 500 -> {
                // 400和500错误，如果响应体是JSON，允许继续解析
                // Retrofit默认会抛出HttpException，但我们的JsonResponseConverter会处理
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