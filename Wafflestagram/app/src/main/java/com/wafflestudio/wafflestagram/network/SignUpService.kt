package com.wafflestudio.wafflestagram.network

import com.wafflestudio.wafflestagram.model.User
import com.wafflestudio.wafflestagram.network.dto.SignUpRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface SignUpService {
    @POST("/api/v1/users/signup/")
    suspend fun signUp(@Body signUpRequest: SignUpRequest): Response<User>
}