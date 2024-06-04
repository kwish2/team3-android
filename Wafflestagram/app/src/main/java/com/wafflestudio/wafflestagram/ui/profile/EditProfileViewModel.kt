package com.wafflestudio.wafflestagram.ui.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wafflestudio.wafflestagram.model.PhotoUrl
import com.wafflestudio.wafflestagram.network.dto.SetProfilePhotoRequest
import com.wafflestudio.wafflestagram.network.dto.UpdateUserRequest
import com.wafflestudio.wafflestagram.repository.EditProfileRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import retrofit2.Response
import timber.log.Timber
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class EditProfileViewModel @Inject constructor(
    private val editProfileRepository: EditProfileRepository
): ViewModel(){

    private val _fetchUserInfoResponse = MutableLiveData<Response<Void>>()
    var fetchUserInfoResponse: LiveData<Response<Void>> = _fetchUserInfoResponse

    private val _response = MutableLiveData<Response<Void>>()
    var response : LiveData<Response<Void>> = _response

    private val _image = MutableLiveData<Response<PhotoUrl>>()
    var image:LiveData<Response<PhotoUrl>> = _image

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

    fun setProfilePhoto(setProfilePhotoRequest: SetProfilePhotoRequest){
        viewModelScope.launch {
            try {
                val data = editProfileRepository.setProfilePhoto(setProfilePhotoRequest)
                _response.value = data
            }catch (e:IOException){
                Timber.e(e)
            }
        }
    }

    fun getProfilePhoto(id: Int){
        viewModelScope.launch {
            try {
                val data = editProfileRepository.getProfilePhoto(id)
                _image.value = data
            }catch (e:IOException){
                Timber.e(e)
            }
        }
    }
}