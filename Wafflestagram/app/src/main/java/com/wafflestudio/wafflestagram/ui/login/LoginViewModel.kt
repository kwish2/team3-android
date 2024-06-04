package com.wafflestudio.wafflestagram.ui.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wafflestudio.wafflestagram.model.User
import com.wafflestudio.wafflestagram.network.dto.LoginRequest
import com.wafflestudio.wafflestagram.repository.LoginRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import okhttp3.ResponseBody
import retrofit2.Response
import timber.log.Timber
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginRepository: LoginRepository
): ViewModel(){

    private val _fetchDummy = MutableLiveData<String>()
    var fetchDummy: LiveData<String> = _fetchDummy

    private val _fetchLoginResponse = MutableLiveData<Response<ResponseBody>>()
    var fetchLoginResponse: LiveData<Response<ResponseBody>> = _fetchLoginResponse

    private val _fetchSocialLoginResponse = MutableLiveData<Response<User>>()
    var fetchSocialLoginResponse: LiveData<Response<User>> = _fetchSocialLoginResponse

    fun getResponseByLogin(loginRequest: LoginRequest) {
        viewModelScope.launch {
            try {
                val data = loginRepository.getResponseByLogin(loginRequest)
                _fetchLoginResponse.value = data
            } catch (e: IOException) {
                Timber.e(e)
            }
        }
    }

    fun getResponseByGoogleLogin(idToken: String) {
        viewModelScope.launch {
            try {
                // _fetchDummy.value = idToken
                val data = loginRepository.getResponseByGoogleLogin(idToken)
                _fetchSocialLoginResponse.value = data
            } catch (e: IOException) {
                Timber.e(e)
            }
        }
    }

    fun getResponseByFacebookLogin(token: String){
        viewModelScope.launch {
            try {
                val data = loginRepository.getResponseByFacebookLogin(token)
                _fetchSocialLoginResponse.value = data
            } catch (e: IOException) {
                Timber.e(e)
            }
        }
    }
}