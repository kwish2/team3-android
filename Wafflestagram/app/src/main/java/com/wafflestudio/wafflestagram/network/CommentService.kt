package com.wafflestudio.wafflestagram.network

import com.wafflestudio.wafflestagram.model.Comment
import com.wafflestudio.wafflestagram.model.Feed
import com.wafflestudio.wafflestagram.model.Reply
import com.wafflestudio.wafflestagram.model.User
import com.wafflestudio.wafflestagram.network.dto.AddCommentRequest
import com.wafflestudio.wafflestagram.network.dto.AddReplyRequest
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.*

interface CommentService {

    @GET("api/v1/comment/list/{feed_id}/")
    suspend fun getComments(@Path("feed_id") id: Int): Response<List<Comment>>

    @GET("api/v1/feed/{feed_id}/")
    suspend fun getFeedById(@Path("feed_id") id: Int): Response<Feed>

    @POST("api/v1/comment/{feed_id}/")
    suspend fun addComment(@Path("feed_id") id: Int, @Body addCommentRequest: AddCommentRequest) : Response<Comment>

    @GET("/api/v1/users/me/")
    suspend fun getMyInfo(): Response<User>

    @DELETE("api/v1/comment/{comment_id}/")
    suspend fun deleteComment(@Path("comment_id") id: Int): Response<ResponseBody>

    @POST("api/v1/reply/{comment_id}/")
    suspend fun addReply(@Path("comment_id") id: Int, @Body addReplyRequest: AddReplyRequest): Response<Reply>

    @DELETE("api/v1/reply/{reply_id}/")
    suspend fun deleteReply(@Path("reply_id") id: Int): Response<ResponseBody>
}