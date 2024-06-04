package com.wafflestudio.wafflestagram.repository

import com.wafflestudio.wafflestagram.model.Page
import com.wafflestudio.wafflestagram.model.User
import com.wafflestudio.wafflestagram.network.UserService
import okhttp3.ResponseBody
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DetailUserRepository @Inject constructor(private val userService: UserService) {

    suspend fun getInfoByUserId(userId: Int): Response<User> {
        return userService.getInfoByUserId(userId)
    }

    suspend fun getFeedCountByUserId(userId: Int): Response<ResponseBody> {
        return userService.getFeedCountByUserId(userId)
    }

    suspend fun getFollowerCountByUserId(userId: Int): Response<ResponseBody> {
        return userService.getFollowerCountByUserId(userId)
    }

    suspend fun getFollowingCountByUserId(userId: Int): Response<ResponseBody> {
        return userService.getFollowingCountByUserId(userId)
    }

    suspend fun getFeedsById(id: Int, offset : Int, number: Int): Response<Page>{
        return userService.getFeedsById(id, offset, number)
    }

    suspend fun checkFollowing(id: Int): Response<ResponseBody>{
        return userService.checkFollowing(id)
    }

    suspend fun follow(id: Int) {
        return userService.follow(id)
    }

    suspend fun unfollow(id: Int) {
        return userService.unfollow(id)
    }

    suspend fun getMyInfo(): Response<User> {
        return userService.getMyInfo()
    }
}