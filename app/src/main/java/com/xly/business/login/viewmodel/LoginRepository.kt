package com.xly.business.login.viewmodel

import com.xly.business.login.model.LoginUser
import com.xly.business.login.model.UserInfoRegisterReq
import com.xly.middlelibrary.AuthResponse
import com.xly.middlelibrary.net.LYHttpClient
import com.xly.middlelibrary.net.NetworkUtils
import kotlinx.coroutines.delay

class LoginRepository {



    private val apiService = LYHttpClient.instance

    // 手机登录
    suspend fun phoneLogin(loginRequest: LoginUser): Result<AuthResponse> {
        return NetworkUtils.safeApiCall {
            apiService.phoneLogin(loginRequest)
        }
    }

    suspend fun userInfoRegister(loginRequest: UserInfoRegisterReq): Result<AuthResponse> {
        return NetworkUtils.safeApiCall {
            apiService.userInfoRegister(loginRequest)
        }
    }


    suspend fun getHealth(): Result<Map<String, Any>> {
        return NetworkUtils.safeApiCall {
            apiService.getHealth()
        }
    }








} 