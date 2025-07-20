package com.xly.business.login.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.xly.business.login.model.LoginUser
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class LoginViewModel : ViewModel() {
    val loginState = MutableLiveData<Result<Boolean>>()
    val showTextIndex = MutableLiveData<Int>() // 控制动画显示第几行
    val showDialog = MutableLiveData<Boolean>()

    private val repository = LoginRepository()

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
} 