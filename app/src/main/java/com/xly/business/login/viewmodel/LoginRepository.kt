package com.xly.business.login.viewmodel

import com.xly.business.login.model.LoginUser
import kotlinx.coroutines.delay

class LoginRepository {
    suspend fun login(user: LoginUser): Result<Boolean> {
        delay(1000) // 模拟网络
        return if (user.phone == "123456" && user.password == "123456") {
            Result.success(true)
        } else {
            Result.failure(Exception("账号或密码错误"))
        }
    }





} 