package com.wafflestudio.wafflestagram.ui.share

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wafflestudio.wafflestagram.model.Feed
import com.wafflestudio.wafflestagram.model.User
import com.wafflestudio.wafflestagram.repository.SharedFeedRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import okhttp3.ResponseBody
import retrofit2.Response
import timber.log.Timber
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class SharedFeedViewModel @Inject constructor(
    private val sharedFeedRepository: SharedFeedRepository
): ViewModel(){

    private val _feed = MutableLiveData<Response<Feed>>()
    var feed : LiveData<Response<Feed>> = _feed

    private val _user = MutableLiveData<Response<User>>()
    var user: LiveData<Response<User>> = _user

    private val _likeRespone = MutableLiveData<Response<Feed>>()
    var likeResponse: LiveData<Response<Feed>> = _likeRespone

    private val _deleteResponse = MutableLiveData<Response<ResponseBody>>()
    var deleteResponse : LiveData<Response<ResponseBody>> = _deleteResponse

    fun getFeedById(id: Int){
        viewModelScope.launch {
            try {
                val data = sharedFeedRepository.getFeedById(id)
                _feed.value = data
            }catch (e: IOException){
                Timber.e(e)
            }
        }
    }

    fun like(id: Int){
        viewModelScope.launch {
            try {
                val data = sharedFeedRepository.like(id)
                _likeRespone.value = data
            }catch (e: IOException){
                Timber.e(e)
            }
        }
    }

    fun unlike(id: Int){
        viewModelScope.launch {
            try {
                val data = sharedFeedRepository.unlike(id)
                _likeRespone.value = data
            }catch (e: IOException){
                Timber.e(e)
            }
        }
    }

    fun getMe(){
        viewModelScope.launch {
            try {
                val data = sharedFeedRepository.getMe()
                _user.value = data
            }catch (e: IOException){
                Timber.e(e)
            }
        }
    }

    fun deleteFeed(id: Int){
        viewModelScope.launch {
            try {
                val data = sharedFeedRepository.deleteFeed(id)
                _deleteResponse.value = data
            }catch (e:IOException){
                Timber.e(e)
            }
        }
    }
}