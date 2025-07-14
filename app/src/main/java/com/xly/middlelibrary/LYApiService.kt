package com.xly.middlelibrary

import com.xly.business.recommend.model.User
import com.xly.middlelibrary.net.LYResponse
import retrofit2.http.GET
import retrofit2.http.Path

interface LYApiService {


    @GET("users/{user}")
    suspend fun getUser(@Path("user") user: String): LYResponse<User>
}