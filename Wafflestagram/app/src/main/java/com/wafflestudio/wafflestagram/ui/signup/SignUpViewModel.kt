package com.wafflestudio.wafflestagram.ui.signup

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wafflestudio.wafflestagram.model.User
import com.wafflestudio.wafflestagram.network.dto.SignUpRequest
import com.wafflestudio.wafflestagram.repository.SignUpRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import retrofit2.Response
import timber.log.Timber
import java.io.IOException
import javax.inject.Inject


@HiltViewModel
class SignUpViewModel @Inject constructor(
    private val signUpRepository: SignUpRepository
): ViewModel(){

    private val _idResponse = MutableLiveData<Response<User>>()
    val idResponse : LiveData<Response<User>> = _idResponse

    fun signUp(signUpRequest: SignUpRequest){
        viewModelScope.launch {
            try {
                val response = signUpRepository.signUp(signUpRequest)
                _idResponse.value = response
            }catch (e: IOException){
                Timber.e(e)
            }
        }
    }

}