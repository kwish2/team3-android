package com.wafflestudio.wafflestagram.repository

import com.wafflestudio.wafflestagram.model.Feed
import com.wafflestudio.wafflestagram.network.FeedService
import com.wafflestudio.wafflestagram.network.dto.EditPostRequest
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class EditPostRepository @Inject constructor(private val feedService: FeedService){

    suspend fun editPost(id: Int, editPostRequest: EditPostRequest): Response<Feed>{
        return feedService.editFeed(id, editPostRequest)
    }

    suspend fun getFeedById(id: Int):Response<Feed>{
        return feedService.getFeedById(id)
    }
}