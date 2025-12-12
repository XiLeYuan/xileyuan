package com.xly.business.recommend.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.xly.business.recommend.model.User

class RecommendViewModel : ViewModel() {

    private val repository = RecommendRepository()
    val userLiveData = MutableLiveData<Result<User>>()



}