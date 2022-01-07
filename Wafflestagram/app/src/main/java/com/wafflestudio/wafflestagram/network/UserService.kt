package com.wafflestudio.wafflestagram.network

import com.wafflestudio.wafflestagram.model.User
import com.wafflestudio.wafflestagram.network.dto.UpdateUserRequest
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface UserService {

    @GET("/api/v1/users/me/")
    suspend fun getMyInfo(): Response<User>

    @GET("/api/v1/users/profile/{user_id}")
    suspend fun getInfoByUserId(
        @Path("user_id") userId: Long
    ): Response<User>

    @POST("/api/v1/users/profile/")
    fun updateUser(
        @Body updateUserRequest: UpdateUserRequest
    ): Response<ResponseBody>
}