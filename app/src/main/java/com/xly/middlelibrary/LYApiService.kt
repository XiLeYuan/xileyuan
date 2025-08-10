package com.xly.middlelibrary

import com.xly.business.recommend.model.User
import com.xly.business.user.UserInfo
import com.xly.middlelibrary.net.LYResponse
import okhttp3.MultipartBody
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.POST
import retrofit2.http.Part

interface LYApiService {

    @GET("users/{user}")
    suspend fun getUser(@Path("user") user: String): LYResponse<User>

    // 获取用户个人资料
    @GET("user/profile/{userId}")
    suspend fun getUserProfile(@Path("userId") userId: String): LYResponse<UserInfo>

    // 头像上传接口
    @POST("upload/avatar")
    suspend fun uploadAvatar(@Part file: MultipartBody.Part): LYResponse<String>
}