package com.wafflestudio.wafflestagram.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wafflestudio.wafflestagram.model.FollowPage
import com.wafflestudio.wafflestagram.model.User
import com.wafflestudio.wafflestagram.model.UserPage
import com.wafflestudio.wafflestagram.repository.SearchRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import retrofit2.Response
import timber.log.Timber
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val searchRepository: SearchRepository
): ViewModel(){
    private val _page = MutableLiveData<Response<UserPage>>()
    val page: LiveData<Response<UserPage>> = _page

    private val _followPage = MutableLiveData<Response<FollowPage>>()
    val followPage: LiveData<Response<FollowPage>> = _followPage

    private val _user = MutableLiveData<Response<User>>()
    val user: LiveData<Response<User>> = _user

    fun search(nickname :String){
        viewModelScope.launch {
            try {
                val data = searchRepository.search(nickname)
                _page.value = data
            }catch (e: IOException){
                Timber.e(e)
            }
        }
    }

    fun checkFollowing(id: Int) {

    }

    fun follow(id: Int){
        viewModelScope.launch {
            try {
                searchRepository.follow(id)
            }catch (e: IOException){
                Timber.e(e)
            }
        }
    }

    fun unfollow(id: Int){
        viewModelScope.launch {
            try {
                searchRepository.unfollow(id)
            }catch (e: IOException){
                Timber.e(e)
            }
        }
    }

    fun getMyFollowing(offset: Int, number: Int){
        viewModelScope.launch {
            try {
                val data = searchRepository.getMyFollowing(offset, number)
                _followPage.value = data
            }catch (e: IOException){
                Timber.e(e)
            }
        }
    }

    fun getMe(){
        viewModelScope.launch {
            try {
                val data = searchRepository.getMe()
                _user.value = data
            }catch (e: IOException){
                Timber.e(e)
            }
        }
    }
}