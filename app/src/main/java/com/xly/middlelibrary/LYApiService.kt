package com.xly.middlelibrary

import com.xly.business.login.model.LoginUser
import com.xly.business.login.model.UserInfoRegisterReq
import com.xly.business.user.UserInfo
import com.xly.middlelibrary.net.LYResponse
import okhttp3.MultipartBody
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Query

// LYApiService.kt - 更新后的API接口
interface LYApiService {

    @POST("api/auth/phoneLogin")
    suspend fun phoneLogin(@Body loginRequest: LoginUser): LYResponse<AuthResponse>

    @POST("api/auth/stepRegister")
    suspend fun userInfoRegister(@Body userInfoRegisterReq: UserInfoRegisterReq): LYResponse<AuthResponse>

    @POST("api/auth/send-verification-code")
    suspend fun sendVerificationCode(
        @Query("phoneNumber") phoneNumber: String
    ): LYResponse<String>


    @GET("api/auth/user-status")
    suspend fun getUserStatus(
        @Query("phoneNumber") phoneNumber: String
    ): LYResponse<UserStatus>

    // 用户相关
    @GET("api/users/profile")
    suspend fun getUserProfile(): LYResponse<UserInfo>

    @PUT("api/users/profile")
    suspend fun updateUserProfile(
        @Body userInfo: UserInfo
    ): LYResponse<UserInfo>

    // 标签相关
    @GET("api/tags/categories")
    suspend fun getTagCategories(): LYResponse<List<String>>

    @GET("api/tags/user-tags")
    suspend fun getUserTags(): LYResponse<List<String>>

    @GET("api/tags/preferred-tags")
    suspend fun getPreferredTags(): LYResponse<List<String>>

    // 文件上传
    @Multipart
    @POST("api/upload/avatar")
    suspend fun uploadAvatar(
        @Part file: MultipartBody.Part
    ): LYResponse<String>

    @Multipart
    @POST("api/upload/photos")
    suspend fun uploadPhotos(
        @Part files: List<MultipartBody.Part>
    ): LYResponse<List<String>>

    // 测试接口
    @GET("api/test/health")
    suspend fun getHealth(): LYResponse<Map<String, Any>>
}

// 数据模型
data class AuthResponse(
    var token: String? = null,
    var refreshToken: String? = null,
    var expiresIn: Long = 0L,
    var userInfo: UserInfo? = null,
    var message: String? = null
)

data class UserInfo(
    var id: Long? = null,
    var username: String? = null,
    var email: String? = null,
    var nickname: String? = null,
    var avatarUrl: String? = null,
    var role: String? = null,
    var isVerified: Boolean = false
)

data class UserStatus(
    var exists: Boolean = false,
    var registrationStep: Int = 0,
    var isVerified: Boolean = false,
    var isComplete: Boolean = false,
    var userId: Long? = null
)