package com.wafflestudio.wafflestagram.repository

import com.wafflestudio.wafflestagram.model.Feed
import com.wafflestudio.wafflestagram.model.Page
import com.wafflestudio.wafflestagram.model.User
import com.wafflestudio.wafflestagram.network.FeedService
import com.wafflestudio.wafflestagram.network.dto.FeedPageRequest
import okhttp3.ResponseBody
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FeedRepository @Inject constructor(private val feedService: FeedService){

    suspend fun getFeeds(feedPageRequest: FeedPageRequest): Response<Page>{
        return feedService.getFeeds(feedPageRequest.offset, feedPageRequest.limit)
    }

    suspend fun getFeedById(id : Int): Response<Feed>{
        return feedService.getFeedById(id)
    }

    suspend fun like(id: Int): Response<Feed>{
        return feedService.like(id)
    }

    suspend fun unlike(id: Int): Response<Feed>{
        return feedService.unlike(id)
    }

    suspend fun getMe() : Response<User> {
        return feedService.getMe()
    }

    suspend fun deleteFeed(id: Int): Response<ResponseBody>{
        return feedService.deleteFeed(id)
    }
}