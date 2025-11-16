package com.xly.business.user.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.xly.business.user.UserInfo
import kotlinx.coroutines.launch

class ProfileViewModel : ViewModel() {

    private val repository = ProfileRepository()
    val userProfileLiveData = MutableLiveData<Result<UserInfo>>()
    val profileStatsLiveData = MutableLiveData<ProfileStats>()

    fun loadUserProfile(userId: String) {
        viewModelScope.launch {
            val result = repository.getUserProfile(userId)
            userProfileLiveData.value = result
        }
    }

    fun loadProfileStats(userId: String) {
        viewModelScope.launch {
            val stats = repository.getProfileStats(userId)
            profileStatsLiveData.value = stats
        }
    }

    data class ProfileStats(
        val likesCount: Int = 0,
        val visitorsCount: Int = 0,
        val popularityScore: Int = 0,
        val interactionsCount: Int = 0,
        // 新增数量（用于角标显示）
        val likedPeopleCount: Int = 0, // 喜欢的人总数
        val likedPeopleNewCount: Int = 0, // 喜欢的人新增数量
        val whoLikedMeCount: Int = 0, // 谁喜欢我总数
        val whoLikedMeNewCount: Int = 0, // 谁喜欢我新增数量
        val visitorsNewCount: Int = 0 // 访客新增数量
    )
}
