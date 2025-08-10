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
        val interactionsCount: Int = 0
    )
}
