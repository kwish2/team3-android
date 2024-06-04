package com.wafflestudio.wafflestagram.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wafflestudio.wafflestagram.model.Feed
import com.wafflestudio.wafflestagram.model.Page
import com.wafflestudio.wafflestagram.model.User
import com.wafflestudio.wafflestagram.network.dto.FeedPageRequest
import com.wafflestudio.wafflestagram.repository.FeedRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import retrofit2.Response
import timber.log.Timber
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class FeedViewModel @Inject constructor(
    private val feedRepository: FeedRepository
) : ViewModel(){

    private val _page = MutableLiveData<Response<Page>>()
    val page: LiveData<Response<Page>> = _page

    private val _user = MutableLiveData<Response<User>>()
    val user: LiveData<Response<User>> = _user

    private val _likeRespone = MutableLiveData<Response<Feed>>()
    val likeResponse: LiveData<Response<Feed>> = _likeRespone

    fun getFeeds(feedPageRequest: FeedPageRequest){
        viewModelScope.launch {
            try {
                val data = feedRepository.getFeeds(feedPageRequest)
                _page.value = data
            }catch (e: IOException){
                Timber.e(e)
            }
        }
    }


    fun like(id: Int){
        viewModelScope.launch {
            try {
                val data = feedRepository.like(id)
                _likeRespone.value = data
            }catch (e: IOException){
                Timber.e(e)
            }
        }
    }

    fun unlike(id: Int){
        viewModelScope.launch {
            try {
                val data = feedRepository.unlike(id)
                _likeRespone.value = data
            }catch (e: IOException){
                Timber.e(e)
            }
        }
    }

    fun getMe(){
        viewModelScope.launch {
            try {
                val data = feedRepository.getMe()
                _user.value = data
            }catch (e: IOException){
                Timber.e(e)
            }
        }
    }

    fun deleteFeed(id: Int){
        viewModelScope.launch {
            try {
                feedRepository.deleteFeed(id)
            }catch (e: IOException){
                Timber.e(e)
            }
        }
    }

}