package com.xly.business.login.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.xly.business.login.model.LoginUser
import com.xly.business.login.model.UserInfoRegisterReq
import com.xly.middlelibrary.AuthResponse
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
    var school: String? = null
    var job: String? = null
    var income: String? = null
    var houseStatus: String? = null
    var carStatus: String? = null
    var maritalStatus: String? = null
    var childrenStatus: String? = null
    var nickname: String? = null
    var avatarUrl: String? = null

    private val _loginResult = MutableLiveData<AuthResponse>()
    val loginResult: LiveData<AuthResponse> = _loginResult

    private val _registerResult = MutableLiveData<AuthResponse>()
    val registerResult: LiveData<AuthResponse> = _registerResult

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> = _errorMessage

    val repository: LoginRepository by lazy {
        LoginRepository()
    }

    fun phoneLogin(phoneNumber: String, verificationCode: String) {
        viewModelScope.launch {
            val result = repository.phoneLogin(LoginUser(phoneNumber,verificationCode))
            result.fold(
                onSuccess = { authResponse ->
                    _loginResult.value = authResponse
                },
                onFailure = { exception ->
                    _errorMessage.value = exception.message ?: "登录失败"
                }
            )
        }
    }

    fun userInfoRegister(loginRequest: UserInfoRegisterReq) {
        viewModelScope.launch {
            val result = repository.userInfoRegister(loginRequest)
            result.fold(
                onSuccess = { authResponse ->
                    _registerResult.value = authResponse
                },
                onFailure = { exception ->
                    _errorMessage.value = exception.message ?: "登录失败"
                }
            )
        }
    }

    fun startTextAnimation() {
        viewModelScope.launch {
            for (i in 1..4) {
                showTextIndex.value = i
                delay(600)
            }
            showDialog.value = true
        }
    }

    fun uploadAvatar(file: File, onSuccess: (String) -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            try {
                val requestBody = file.asRequestBody("image/*".toMediaTypeOrNull())
                val part = MultipartBody.Part.createFormData("file", file.name, requestBody)

                val result = repository.uploadAvatar(part)

                result.fold(
                    onSuccess = { avatarUrl ->
                        this@LoginViewModel.avatarUrl = avatarUrl
                        onSuccess(avatarUrl)
                    },
                    onFailure = { exception ->
                        onError(exception.message ?: "上传失败")
                    }
                )
            } catch (e: Exception) {
                onError(e.message ?: "上传失败")
            }
        }
    }

}