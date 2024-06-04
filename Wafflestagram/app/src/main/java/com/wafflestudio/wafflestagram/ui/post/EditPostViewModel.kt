package com.wafflestudio.wafflestagram.ui.post

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wafflestudio.wafflestagram.model.Feed
import com.wafflestudio.wafflestagram.network.dto.EditPostRequest
import com.wafflestudio.wafflestagram.repository.EditPostRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import retrofit2.Response
import timber.log.Timber
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class EditPostViewModel @Inject constructor(
    private val editPostRepository: EditPostRepository
): ViewModel(){

    private val _feedResponse = MutableLiveData<Response<Feed>>()
    val feedResponse: LiveData<Response<Feed>> = _feedResponse

    private val _fetchFeed = MutableLiveData<Response<Feed>>()
    val fetchFeed: LiveData<Response<Feed>> = _fetchFeed

    fun editPost(id: Int, editPostRequest: EditPostRequest){
        viewModelScope.launch {
            try {
                val response = editPostRepository.editPost(id, editPostRequest)
                _feedResponse.value = response
            }catch (e: IOException){
                Timber.e(e)
            }
        }
    }

    fun getFeedById(id: Int){
        viewModelScope.launch {
            try {
                val feed = editPostRepository.getFeedById(id)
                _fetchFeed.value = feed
            }catch (e: IOException){
                Timber.e(e)
            }
        }
    }
}