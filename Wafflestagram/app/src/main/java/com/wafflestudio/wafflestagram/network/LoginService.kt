package com.wafflestudio.wafflestagram.network

import com.wafflestudio.wafflestagram.model.User
import com.wafflestudio.wafflestagram.network.dto.LoginRequest
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface LoginService {
    @POST("/api/v1/users/signin/")
    suspend fun getResponseByLogin(
        @Body loginRequest: LoginRequest
    ): Response<ResponseBody>

    @POST("/api/v1/social_login/google/")
    suspend fun getResponseByGoogleLogin(
        @Header("idToken") idToken: String
    ): Response<User>

    @POST("/api/v1/social_login/facebook/verify/")
    suspend fun getResponseByFacebookLogin(
        @Header("idToken") token: String
    ): Response<User>
}