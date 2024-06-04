package com.wafflestudio.wafflestagram.repository

import com.wafflestudio.wafflestagram.model.FollowPage
import com.wafflestudio.wafflestagram.model.User
import com.wafflestudio.wafflestagram.network.FeedService
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FollowRepository @Inject constructor(private val feedService: FeedService){

    suspend fun getFollowingById(id : Int, offset: Int, limit: Int): Response<FollowPage> {
        return feedService.getFollowingByUserId(id, offset, limit)
    }

    suspend fun getFollowerById(id : Int, offset: Int, limit: Int): Response<FollowPage> {
        return feedService.getFollowerByUserId(id, offset, limit)
    }

    suspend fun getMe() : Response<User> {
        return feedService.getMe()
    }

    fun checkFollowing(id: Int) : Response<Boolean> {
        return feedService.checkFollowing(id)
    }

    suspend fun follow(id: Int) {
        return feedService.follow(id)
    }

    suspend fun unfollow(id: Int) {
        return feedService.unfollow(id)
    }

    suspend fun getMyFollowing(offset: Int, limit: Int): Response<FollowPage> {
        return feedService.getMyFollowing(offset, limit)
    }
}