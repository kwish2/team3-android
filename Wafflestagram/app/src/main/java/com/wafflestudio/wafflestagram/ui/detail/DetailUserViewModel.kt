package com.wafflestudio.wafflestagram.ui.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wafflestudio.wafflestagram.model.Page
import com.wafflestudio.wafflestagram.model.User
import com.wafflestudio.wafflestagram.repository.DetailUserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import okhttp3.ResponseBody
import retrofit2.Response
import timber.log.Timber
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class DetailUserViewModel @Inject constructor(
    private val detailUserRepository: DetailUserRepository
): ViewModel(){

    private val _page = MutableLiveData<Response<Page>>()
    val page: LiveData<Response<Page>> = _page

    private val _fetchUserInfo = MutableLiveData<Response<User>>()
    val fetchUserInfo: LiveData<Response<User>> = _fetchUserInfo

    private val _fetchMyInfo = MutableLiveData<Response<User>>()
    val fetchMyInfo: LiveData<Response<User>> = _fetchMyInfo

    private val _fetchFollowerCount = MutableLiveData<Response<ResponseBody>>()
    val fetchFollowerCount: LiveData<Response<ResponseBody>> = _fetchFollowerCount

    private val _fetchFollowingCount = MutableLiveData<Response<ResponseBody>>()
    val fetchFollowingCount: LiveData<Response<ResponseBody>> = _fetchFollowingCount

    private val _fetchFeedCount = MutableLiveData<Response<ResponseBody>>()
    val fetchFeedCount: LiveData<Response<ResponseBody>> = _fetchFeedCount

    private val _checkFollowing = MutableLiveData<Response<ResponseBody>>()
    val checkFollowing: LiveData<Response<ResponseBody>> = _checkFollowing

    fun getInfoById(id:Int){
        viewModelScope.launch {
            try{
                val data = detailUserRepository.getInfoByUserId(id)
                _fetchUserInfo.value = data
            } catch (e: IOException) {
                Timber.e(e)
            }
        }
    }

    fun getFollowerCount(id: Int){
        viewModelScope.launch {
            try {
                val data = detailUserRepository.getFollowerCountByUserId(id)
                _fetchFollowerCount.value = data
            } catch (e: IOException) {
                Timber.e(e)
            }
        }
    }

    fun getFollowingCount(id: Int){
        viewModelScope.launch {
            try {
                val data = detailUserRepository.getFollowingCountByUserId(id)
                _fetchFollowingCount.value = data
            } catch (e: IOException) {
                Timber.e(e)
            }
        }
    }

    fun getFeedCount(id: Int){
        viewModelScope.launch {
            try {
                val data = detailUserRepository.getFeedCountByUserId(id)
                _fetchFeedCount.value = data
            } catch (e: IOException) {
                Timber.e(e)
            }
        }
    }

    fun getFeedsById(id: Int, offset : Int, number: Int){
        viewModelScope.launch {
            try {
                val data = detailUserRepository.getFeedsById(id, offset, number)
                _page.value = data
            }catch (e: IOException){
                Timber.e(e)
            }
        }
    }

    fun checkFollowing(id: Int){
        viewModelScope.launch {
            try {
                val data = detailUserRepository.checkFollowing(id)
                _checkFollowing.value = data
            }catch (e: IOException){
                Timber.e(e)
            }
        }
    }

    fun follow(id: Int){
        viewModelScope.launch {
            try {
                detailUserRepository.follow(id)
            }catch (e: IOException){
                Timber.e(e)
            }
        }
    }

    fun unfollow(id: Int){
        viewModelScope.launch {
            try {
                detailUserRepository.unfollow(id)
            }catch (e: IOException){
                Timber.e(e)
            }
        }
    }

    fun getMyInfo(){
        viewModelScope.launch {
            try{
                val data = detailUserRepository.getMyInfo()
                _fetchMyInfo.value = data
            } catch (e: IOException) {
                Timber.e(e)
            }
        }
    }
}