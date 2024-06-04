package com.wafflestudio.wafflestagram.repository

import com.wafflestudio.wafflestagram.model.Comment
import com.wafflestudio.wafflestagram.model.Feed
import com.wafflestudio.wafflestagram.model.Reply
import com.wafflestudio.wafflestagram.model.User
import com.wafflestudio.wafflestagram.network.CommentService
import com.wafflestudio.wafflestagram.network.dto.AddCommentRequest
import com.wafflestudio.wafflestagram.network.dto.AddReplyRequest
import okhttp3.ResponseBody
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CommentRepository @Inject constructor(private val commentService: CommentService) {

    suspend fun getComments(id : Int) : Response<List<Comment>>{
        return commentService.getComments(id)
    }
    suspend fun addComment(id: Int, addCommentRequest: AddCommentRequest) : Response<Comment>{
        return commentService.addComment(id, addCommentRequest)
    }
    suspend fun getFeedById(id : Int): Response<Feed>{
        return commentService.getFeedById(id)
    }
    suspend fun getMyInfo(): Response<User> {
        return commentService.getMyInfo()
    }
    suspend fun deleteComment(id: Int): Response<ResponseBody>{
        return commentService.deleteComment(id)
    }
    suspend fun addReply(id: Int, addReplyRequest: AddReplyRequest): Response<Reply>{
        return commentService.addReply(id, addReplyRequest)
    }
    suspend fun deleteReply(id: Int): Response<ResponseBody>{
        return commentService.deleteReply(id)
    }
}