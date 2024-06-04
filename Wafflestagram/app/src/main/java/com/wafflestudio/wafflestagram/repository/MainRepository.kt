package com.wafflestudio.wafflestagram.repository

import com.wafflestudio.wafflestagram.model.User
import com.wafflestudio.wafflestagram.network.UserService
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MainRepository @Inject constructor(private val userService: UserService){

    suspend fun getMyInfo(): Response<User> {
        return userService.getMyInfo()
    }

}