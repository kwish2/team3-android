package com.wafflestudio.wafflestagram.ui.post

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
): ViewModel() {
    private val _page = MutableLiveData<Response<UserPage>>()
    val page: LiveData<Response<UserPage>> = _page

    fun search(nickname: String) {
        viewModelScope.launch {
            try {
                val data = searchRepository.search(nickname)
                _page.value = data
            } catch (e: IOException) {
                Timber.e(e)
            }
        }
    }
}