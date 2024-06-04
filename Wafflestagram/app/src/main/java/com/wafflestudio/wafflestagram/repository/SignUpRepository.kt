package com.wafflestudio.wafflestagram.repository

import com.wafflestudio.wafflestagram.model.User
import com.wafflestudio.wafflestagram.network.SignUpService
import com.wafflestudio.wafflestagram.network.dto.SignUpRequest
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SignUpRepository @Inject constructor(private val signUpService: SignUpService){

    suspend fun signUp(signUpRequest: SignUpRequest): Response<User>{
        return signUpService.signUp(signUpRequest)
    }
}