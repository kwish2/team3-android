package com.wafflestudio.wafflestagram.repository

import com.wafflestudio.wafflestagram.model.PhotoUrl
import com.wafflestudio.wafflestagram.network.UserService
import com.wafflestudio.wafflestagram.network.dto.SetProfilePhotoRequest
import com.wafflestudio.wafflestagram.network.dto.UpdateUserRequest
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class EditProfileRepository @Inject constructor(private val userService: UserService){
    suspend fun setProfilePhoto(setProfilePhotoRequest: SetProfilePhotoRequest): Response<Void>{
        return userService.setProfilePhoto(setProfilePhotoRequest)
    }

    suspend fun updateUser(updateUserRequest: UpdateUserRequest): Response<Void> {
        return userService.updateUser(updateUserRequest)
    }

    suspend fun getProfilePhoto(id : Int): Response<PhotoUrl>{
        return userService.getProfilePhoto(id)
    }
}