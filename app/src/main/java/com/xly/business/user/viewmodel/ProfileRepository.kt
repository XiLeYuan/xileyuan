package com.xly.business.user.viewmodel

import com.xly.business.user.UserInfo
import com.xly.middlelibrary.net.LYHttpClient
import com.xly.middlelibrary.net.NetworkUtils

class ProfileRepository {

    suspend fun getUserProfile(userId: String): Result<UserInfo> {



        return NetworkUtils.safeApiCall {
            LYHttpClient.instance.getUserProfile()
        }

    }

    suspend fun getProfileStats(userId: String): ProfileViewModel.ProfileStats {
        // 模拟获取统计数据
        return ProfileViewModel.ProfileStats(
            likesCount = 128,
            visitorsCount = 56,
            popularityScore = 89,
            interactionsCount = 234,
            likedPeopleCount = 128,
            likedPeopleNewCount = 0, // 由Fragment中的mock数据提供
            whoLikedMeCount = 89,
            whoLikedMeNewCount = 0, // 由Fragment中的mock数据提供
            visitorsNewCount = 0 // 由Fragment中的mock数据提供
        )
    }
}
