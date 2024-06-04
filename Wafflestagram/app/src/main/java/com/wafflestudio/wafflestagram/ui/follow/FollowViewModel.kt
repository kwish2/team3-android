package com.wafflestudio.wafflestagram.ui.follow

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wafflestudio.wafflestagram.model.FollowPage
import com.wafflestudio.wafflestagram.model.User
import com.wafflestudio.wafflestagram.repository.FollowRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import retrofit2.Response
import timber.log.Timber
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class FollowViewModel @Inject constructor(
    private val followRepository: FollowRepository
): ViewModel(){

    private val _page = MutableLiveData<Response<FollowPage>>()
    val page: LiveData<Response<FollowPage>> = _page

    private val _user = MutableLiveData<Response<User>>()
    val user: LiveData<Response<User>> = _user

    private val _followPage = MutableLiveData<Response<FollowPage>>()
    val followPage: LiveData<Response<FollowPage>> = _followPage

    fun getFollowingById(id: Int, offset : Int, limit: Int){
        viewModelScope.launch {
            try {
                val data = followRepository.getFollowingById(id, offset, limit)
                _page.value = data
            }catch (e: IOException){
                Timber.e(e)
            }
        }
    }

    fun getFollowerById(id: Int, offset : Int, limit: Int){
        viewModelScope.launch {
            try {
                val data = followRepository.getFollowerById(id, offset, limit)
                _page.value = data
            }catch (e: IOException){
                Timber.e(e)
            }
        }
    }

    fun getMe(){
        viewModelScope.launch {
            try {
                val data = followRepository.getMe()
                _user.value = data
            }catch (e: IOException){
                Timber.e(e)
            }
        }
    }

    fun checkFollowing(id: Int) : Response<Boolean> {
        return followRepository.checkFollowing(id)
    }

    fun follow(id: Int){
        viewModelScope.launch {
            try {
                followRepository.follow(id)
            }catch (e: IOException){
                Timber.e(e)
            }
        }
    }

    fun unfollow(id: Int){
        viewModelScope.launch {
            try {
                followRepository.unfollow(id)
            }catch (e: IOException){
                Timber.e(e)
            }
        }
    }

    fun getMyFollowing(offset: Int, number: Int){
        viewModelScope.launch {
            try {
                val data = followRepository.getMyFollowing(offset, number)
                _followPage.value = data
            }catch (e: IOException){
                Timber.e(e)
            }
        }
    }
}