package com.xly.business.recommend.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.xly.business.recommend.model.User
import kotlinx.coroutines.launch

class RecommendViewModel : ViewModel() {

    private val repository = RecommendRepository()
    val userLiveData = MutableLiveData<Result<User>>()



}