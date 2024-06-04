package com.wafflestudio.wafflestagram.ui.like

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wafflestudio.wafflestagram.model.Feed
import com.wafflestudio.wafflestagram.model.FollowPage
import com.wafflestudio.wafflestagram.model.User
import com.wafflestudio.wafflestagram.repository.LikeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import retrofit2.Response
import timber.log.Timber
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class LikeViewModel @Inject constructor(
    private val likeRepository: LikeRepository
) : ViewModel(){

    private val _feed = MutableLiveData<Response<Feed>>()
    val feed: LiveData<Response<Feed>> = _feed

    private val _user = MutableLiveData<Response<User>>()
    val user: LiveData<Response<User>> = _user

    private val _page = MutableLiveData<Response<FollowPage>>()
    val page: LiveData<Response<FollowPage>> = _page

    fun getFeedById(id: Int){
        viewModelScope.launch {
            try {
                val data = likeRepository.getFeedById(id)
                _feed.value = data
            }catch (e: IOException){
                Timber.e(e)
            }
        }
    }

    fun getMe(){
        viewModelScope.launch {
            try {
                val data = likeRepository.getMe()
                _user.value = data
            }catch (e: IOException){
                Timber.e(e)
            }
        }
    }

    fun checkFollowing(id: Int) : Response<Boolean>{
        return likeRepository.checkFollowing(id)
    }

    fun follow(id: Int){
        viewModelScope.launch {
            try {
                likeRepository.follow(id)
            }catch (e: IOException){
                Timber.e(e)
            }
        }
    }

    fun unfollow(id: Int){
        viewModelScope.launch {
            try {
                likeRepository.unfollow(id)
            }catch (e: IOException){
                Timber.e(e)
            }
        }
    }

    fun getMyFollowing(offset: Int, number: Int){
        viewModelScope.launch {
            try {
                val data = likeRepository.getMyFollowing(offset, number)
                _page.value = data
            }catch (e: IOException){
                Timber.e(e)
            }
        }
    }
}