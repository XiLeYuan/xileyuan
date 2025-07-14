package com.xly.business.recommend.viewmodel

import com.xly.business.recommend.model.User
import com.xly.middlelibrary.net.LYHttpClient
import com.xly.middlelibrary.net.safeApiCall

class RecommendRepository {

    suspend fun getUser(user: String): Result<User> {
        return safeApiCall { LYHttpClient.instance.getUser(user) }
    }
}