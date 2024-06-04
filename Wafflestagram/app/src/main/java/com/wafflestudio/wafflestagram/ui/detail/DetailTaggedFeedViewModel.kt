package com.wafflestudio.wafflestagram.ui.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wafflestudio.wafflestagram.model.Feed
import com.wafflestudio.wafflestagram.model.Page
import com.wafflestudio.wafflestagram.model.User
import com.wafflestudio.wafflestagram.repository.DetailTaggedFeedRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import retrofit2.Response
import timber.log.Timber
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class DetailTaggedFeedViewModel @Inject constructor(
    private val detailTaggedFeedRepository: DetailTaggedFeedRepository
): ViewModel(){

    private val _page = MutableLiveData<Response<Page>>()
    val page: LiveData<Response<Page>> = _page

    private val _user = MutableLiveData<Response<User>>()
    val user: LiveData<Response<User>> = _user

    private val _likeRespone = MutableLiveData<Response<Feed>>()
    val likeResponse: LiveData<Response<Feed>> = _likeRespone

    fun getTaggedFeedsByUserId(id: Int, offset: Int, limit : Int){
        viewModelScope.launch {
            try {
                val data = detailTaggedFeedRepository.getTaggedFeedsByUserId(id, offset, limit)
                _page.value = data
            }catch (e: IOException){
                Timber.e(e)
            }
        }
    }

    fun like(id: Int){
        viewModelScope.launch {
            try {
                val data = detailTaggedFeedRepository.like(id)
                _likeRespone.value = data
            }catch (e: IOException){
                Timber.e(e)
            }
        }
    }

    fun unlike(id: Int){
        viewModelScope.launch {
            try {
                val data = detailTaggedFeedRepository.unlike(id)
                _likeRespone.value = data
            }catch (e: IOException){
                Timber.e(e)
            }
        }
    }

    fun getMe(){
        viewModelScope.launch {
            try {
                val data = detailTaggedFeedRepository.getMe()
                _user.value = data
            }catch (e: IOException){
                Timber.e(e)
            }
        }
    }

    fun deleteFeed(id: Int){
        viewModelScope.launch {
            try {
                detailTaggedFeedRepository.deleteFeed(id)
            }catch (e: IOException){
                Timber.e(e)
            }
        }
    }
}