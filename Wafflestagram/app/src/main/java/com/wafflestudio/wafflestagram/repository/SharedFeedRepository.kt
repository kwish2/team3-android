package com.wafflestudio.wafflestagram.repository

import com.wafflestudio.wafflestagram.model.Feed
import com.wafflestudio.wafflestagram.model.User
import com.wafflestudio.wafflestagram.network.FeedService
import okhttp3.ResponseBody
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SharedFeedRepository @Inject constructor(private val feedService: FeedService){

    suspend fun getFeedById(id: Int) : Response<Feed>{
        return feedService.getFeedById(id)
    }

    suspend fun getMe(): Response<User>{
        return feedService.getMe()
    }

    suspend fun like(id: Int): Response<Feed>{
        return feedService.like(id)
    }

    suspend fun unlike(id: Int): Response<Feed>{
        return feedService.unlike(id)
    }

    suspend fun deleteFeed(id: Int): Response<ResponseBody>{
        return feedService.deleteFeed(id)
    }
}