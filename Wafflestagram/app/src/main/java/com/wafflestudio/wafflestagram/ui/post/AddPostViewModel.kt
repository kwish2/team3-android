package com.wafflestudio.wafflestagram.ui.post

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wafflestudio.wafflestagram.model.Feed
import com.wafflestudio.wafflestagram.network.dto.AddPostRequest
import com.wafflestudio.wafflestagram.repository.AddPostRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import retrofit2.Response
import timber.log.Timber
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class AddPostViewModel @Inject constructor(
    private val addPostRepository: AddPostRepository
): ViewModel(){

    private val _feedResponse = MutableLiveData<Response<Feed>>()
    val feedResponse: LiveData<Response<Feed>> = _feedResponse

    fun addPost(addPostRequest: AddPostRequest){
        viewModelScope.launch {
            try {
                val response = addPostRepository.addPost(addPostRequest)
                _feedResponse.value = response
            }catch (e: IOException){
                Timber.e(e)
            }
        }
    }
}