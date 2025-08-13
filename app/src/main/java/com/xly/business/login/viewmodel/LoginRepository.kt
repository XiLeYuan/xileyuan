package com.xly.business.login.viewmodel

import com.xly.business.login.model.LoginUser
import com.xly.middlelibrary.AuthResponse
import com.xly.middlelibrary.net.LYHttpClient
import com.xly.middlelibrary.net.NetworkUtils
import kotlinx.coroutines.delay

class LoginRepository {



    private val apiService = LYHttpClient.instance

    // 手机登录
    suspend fun phoneLogin(phoneNumber: String, verificationCode: String): Result<AuthResponse> {
        return NetworkUtils.safeApiCall {
            apiService.phoneLogin(phoneNumber, verificationCode)
        }
    }





} 