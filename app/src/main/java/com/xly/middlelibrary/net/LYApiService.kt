package com.xly.middlelibrary.net

import com.xly.business.recommend.model.User
import retrofit2.http.GET
import retrofit2.http.Path

interface LYApiService {


    @GET("users/{user}")
    suspend fun getUser(@Path("user") user: String): LYResponse<User>
}