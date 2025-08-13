package com.xly.business.login.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.xly.business.login.model.LoginUser
import com.xly.middlelibrary.LYApiService
import com.xly.middlelibrary.net.LYHttpClient
import com.xly.middlelibrary.net.safeApiCall
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

    fun login(user: LoginUser) {
        viewModelScope.launch {
            loginState.value = repository.login(user)
        }
    }

    // 上传头像
    fun uploadAvatar(file: File, onSuccess: (String) -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            try {
                val requestBody = file.asRequestBody("image/*".toMediaTypeOrNull())
                val part = MultipartBody.Part.createFormData("file", file.name, requestBody)
                
                val result = safeApiCall {
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
                )
            } catch (e: Exception) {
                onError(e.message ?: "上传失败")
            }
        }
    }
} 