package com.wafflestudio.wafflestagram.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wafflestudio.wafflestagram.model.User
import com.wafflestudio.wafflestagram.repository.MainRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import retrofit2.Response
import timber.log.Timber
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val mainRepository: MainRepository
) : ViewModel(){

    private val _myInfo = MutableLiveData<Response<User>>()
    val myInfo: LiveData<Response<User>> = _myInfo

    fun getMyInfo(){
        viewModelScope.launch {
            try{
                val data = mainRepository.getMyInfo()
                _myInfo.value = data
            } catch (e: IOException) {
                Timber.e(e)
            }
        }
    }
}