package com.wafflestudio.wafflestagram.repository

import com.wafflestudio.wafflestagram.model.Feed
import com.wafflestudio.wafflestagram.network.FeedService
import com.wafflestudio.wafflestagram.network.dto.AddPostRequest
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AddPostRepository @Inject constructor(private val feedService: FeedService) {

    suspend fun addPost(addPostRequest: AddPostRequest): Response<Feed>{
        return feedService.addPost(addPostRequest)
    }
}