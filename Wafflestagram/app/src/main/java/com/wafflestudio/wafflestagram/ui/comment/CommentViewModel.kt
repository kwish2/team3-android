package com.wafflestudio.wafflestagram.ui.comment

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wafflestudio.wafflestagram.model.Comment
import com.wafflestudio.wafflestagram.model.Feed
import com.wafflestudio.wafflestagram.model.Reply
import com.wafflestudio.wafflestagram.model.User
import com.wafflestudio.wafflestagram.network.dto.AddCommentRequest
import com.wafflestudio.wafflestagram.network.dto.AddReplyRequest
import com.wafflestudio.wafflestagram.repository.CommentRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import okhttp3.ResponseBody
import retrofit2.Response
import timber.log.Timber
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class CommentViewModel @Inject constructor(
    private val commentRepository: CommentRepository
): ViewModel() {

    private val _feed = MutableLiveData<Response<Feed>>()
    val feed: LiveData<Response<Feed>> = _feed

    private val _comment = MutableLiveData<Response<Comment>>()
    val comment: LiveData<Response<Comment>> = _comment

    private val _myInfo = MutableLiveData<Response<User>>()
    val myInfo: LiveData<Response<User>> = _myInfo

    private val _reply = MutableLiveData<Response<Reply>>()
    val reply: LiveData<Response<Reply>> = _reply

    private val _deleteCommentResponse = MutableLiveData<Response<ResponseBody>>()
    val deleteCommentResponse: LiveData<Response<ResponseBody>> = _deleteCommentResponse

    fun getMyInfo(){
        viewModelScope.launch {
            try{
                val data = commentRepository.getMyInfo()
                _myInfo.value = data
            } catch (e: IOException) {
                Timber.e(e)
            }
        }
    }

    fun getFeedById(id: Int){
        viewModelScope.launch {
            try {
                val data = commentRepository.getFeedById(id)
                _feed.value = data
            }catch (e: IOException){
                Timber.e(e)
            }
        }
    }

    fun addComment(id: Int, addCommentRequest: AddCommentRequest){
        viewModelScope.launch {
            try {
                val data = commentRepository.addComment(id, addCommentRequest)
                _comment.value = data
            }catch (e : IOException){
                Timber.e(e)
            }
        }
    }

    fun deleteComment(id: Int){
        viewModelScope.launch {
            try {
                val data = commentRepository.deleteComment(id)
                _deleteCommentResponse.value = data
            }catch (e:IOException){
                Timber.e(e)
            }
        }
    }

    fun addReply(id: Int, addReplyRequest: AddReplyRequest){
        viewModelScope.launch {
            try {
                val data = commentRepository.addReply(id, addReplyRequest)
                _reply.value = data
            }catch (e: IOException){
                Timber.e(e)
            }
        }
    }

    fun deleteReply(id: Int){
        viewModelScope.launch {
            try {
                val data = commentRepository.deleteReply(id)
                _deleteCommentResponse.value = data
            }catch (e:IOException){
                Timber.e(e)
            }
        }
    }
}