package com.wafflestudio.wafflestagram.repository

import com.wafflestudio.wafflestagram.model.Page
import com.wafflestudio.wafflestagram.model.User
import com.wafflestudio.wafflestagram.network.UserService
import com.wafflestudio.wafflestagram.network.dto.UpdateUserRequest
import okhttp3.ResponseBody
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepository @Inject constructor(private val userService: UserService){
    suspend fun getMyInfo(): Response<User> {
        return userService.getMyInfo()
    }

    suspend fun getMyFollower(): Response<ResponseBody> {
        return userService.getMyFollowerCount()
    }

    suspend fun getMyFollowing(): Response<ResponseBody> {
        return userService.getMyFollowingCount()
    }

    suspend fun getMyFeedCount(): Response<ResponseBody> {
        return userService.getMyFeedsCount()
    }

    suspend fun getInfoByUserId(userId: Int): Response<User> {
        return userService.getInfoByUserId(userId)
    }

    suspend fun getFollowerByUserId(userId: Int): Response<ResponseBody> {
        return userService.getFollowerCountByUserId(userId)
    }

    suspend fun getFollowingByUserId(userId: Int): Response<ResponseBody> {
        return userService.getFollowingCountByUserId(userId)
    }

    suspend fun getFeedCountByUserId(userId: Int): Response<ResponseBody> {
        return userService.getFeedCountByUserId(userId)
    }

    suspend fun updateUser(updateUserRequest: UpdateUserRequest): Response<Void> {
        return userService.updateUser(updateUserRequest)
    }

    suspend fun getMyFeeds(offset :Int, number: Int): Response<Page>{
        return userService.getMyFeeds(offset, number)
    }

    suspend fun getTaggedFeedsByUserId(id: Int, offset : Int, limit: Int):Response<Page>{
        return userService.getTaggedFeedsByUserId(id, offset, limit)
    }
}