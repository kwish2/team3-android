package com.wafflestudio.wafflestagram.ui.comment

import com.wafflestudio.wafflestagram.model.Comment

interface CommentInterface {

    fun deleteComment(id: Int, position: Int)

    fun addReply(comment: Comment, position: Int)

    fun deleteReply(id: Int, position: Int)
}