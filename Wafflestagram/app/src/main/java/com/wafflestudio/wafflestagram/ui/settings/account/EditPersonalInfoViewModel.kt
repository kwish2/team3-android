package com.wafflestudio.wafflestagram.ui.settings.account

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wafflestudio.wafflestagram.network.dto.UpdateUserRequest
import com.wafflestudio.wafflestagram.repository.EditProfileRepository
import com.wafflestudio.wafflestagram.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import retrofit2.Response
import timber.log.Timber
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class EditPersonalInfoViewModel @Inject constructor(
    private val editProfileRepository: EditProfileRepository
): ViewModel() {
    private val _fetchUserInfoResponse = MutableLiveData<Response<Void>>()
    var fetchUserInfoResponse: LiveData<Response<Void>> = _fetchUserInfoResponse

    fun updateUser(updateUserRequest: UpdateUserRequest){
        viewModelScope.launch {
            try {
                val data = editProfileRepository.updateUser(updateUserRequest)
                _fetchUserInfoResponse.value = data
            } catch (e: IOException) {
                Timber.e(e)
            }
        }
    }
}