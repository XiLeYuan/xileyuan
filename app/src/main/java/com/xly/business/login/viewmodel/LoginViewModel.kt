package com.xly.business.login.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.xly.business.login.model.LoginUser
import com.xly.middlelibrary.AuthResponse
import com.xly.middlelibrary.net.LYHttpClient
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File

class LoginViewModel : ViewModel() {
    val loginState = MutableLiveData<Result<Boolean>>()
    val showTextIndex = MutableLiveData<Int>() // 控制动画显示第几行
    val showDialog = MutableLiveData<Boolean>()

    // 新增：保存用户信息的字段
    var gender: String? = null
    var age: Int? = null
    var height: Int? = null
    var education: String? = null
    var nickname: String? = null
    var avatarUrl: String? = null

    private val repository = LoginRepository()
    private val apiService = LYHttpClient.instance

    fun startTextAnimation() {
        viewModelScope.launch {
            for (i in 1..4) {
                showTextIndex.value = i
                delay(600)
            }
            showDialog.value = true
        }
    }


    // 上传头像
    fun uploadAvatar(file: File, onSuccess: (String) -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            try {
                val requestBody = file.asRequestBody("image/*".toMediaTypeOrNull())
                val part = MultipartBody.Part.createFormData("file", file.name, requestBody)
                
                /*val result = safeApiCall {
                    apiService.uploadAvatar(part)
                }
                
                result.fold(
                    onSuccess = { avatarUrl ->
                        this@LoginViewModel.avatarUrl = avatarUrl
                        onSuccess(avatarUrl)
                    },
                    onFailure = { exception ->
                        onError(exception.message ?: "上传失败")
                    }
                )*/
            } catch (e: Exception) {
                onError(e.message ?: "上传失败")
            }
        }
    }

    private val _loginResult = MutableLiveData<AuthResponse>()
    val loginResult: LiveData<AuthResponse> = _loginResult
    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> = _errorMessage
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading
    fun phoneLogin(phoneNumber: String, verificationCode: String) {
        viewModelScope.launch {
            _isLoading.value = true
            val result = repository.phoneLogin(phoneNumber, verificationCode)
            result.fold(
                onSuccess = { authResponse ->
                    _loginResult.value = authResponse
                    handleLoginSuccess(authResponse)
                },
                onFailure = { exception ->
                    _errorMessage.value = exception.message ?: "登录失败"
                }
            )

            _isLoading.value = false
        }
    }
    private fun handleLoginSuccess(authResponse: AuthResponse) {
        // 保存token
        authResponse.token?.let { saveToken(it) }

        // 根据消息判断流程
        when (authResponse.message) {
            "开始注册流程" -> navigateToRegistration()
            "登录成功" -> navigateToMain()
        }
    }
    private fun saveToken(token: String) {
        // 保存token逻辑
    }
    private fun navigateToRegistration() {
        // 导航到注册页面
    }
    private fun navigateToMain() {
        // 导航到主页面
    }

}