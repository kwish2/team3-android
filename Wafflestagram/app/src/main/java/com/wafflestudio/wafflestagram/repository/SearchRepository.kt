package com.wafflestudio.wafflestagram.repository

import com.wafflestudio.wafflestagram.model.FollowPage
import com.wafflestudio.wafflestagram.model.User
import com.wafflestudio.wafflestagram.model.UserPage
import com.wafflestudio.wafflestagram.network.SearchService
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class SearchRepository @Inject constructor(private val searchService: SearchService){

    suspend fun search(nickname: String): Response<UserPage>{
        return searchService.search(nickname)
    }

    suspend fun checkFollowing(id: Int) : Response<Boolean> {
        return searchService.checkFollowing(id)
    }

    suspend fun follow(id: Int) {
        return searchService.follow(id)
    }

    suspend fun unfollow(id: Int) {
        return searchService.unfollow(id)
    }

    suspend fun getMyFollowing(offset: Int, limit: Int): Response<FollowPage> {
        return searchService.getMyFollowing(offset, limit)
    }

    suspend fun getMe() : Response<User> {
        return searchService.getMe()
    }
}