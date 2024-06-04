package com.wafflestudio.wafflestagram.network

import com.wafflestudio.wafflestagram.model.Page
import com.wafflestudio.wafflestagram.model.PhotoUrl
import com.wafflestudio.wafflestagram.model.User
import com.wafflestudio.wafflestagram.network.dto.SetProfilePhotoRequest
import com.wafflestudio.wafflestagram.network.dto.UpdateUserRequest
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.*

interface UserService {

    @GET("/api/v1/users/me/")
    suspend fun getMyInfo(): Response<User>

    @GET("/api/v1/users/followerNum/")
    suspend fun getMyFollowerCount(): Response<ResponseBody>

    @GET("/api/v1/users/followingNum/")
    suspend fun getMyFollowingCount(): Response<ResponseBody>

    @GET("/api/v1/users/feedsNum/")
    suspend fun getMyFeedsCount(): Response<ResponseBody>

    @GET("/api/v1/users/profile/{user_id}/")
    suspend fun getInfoByUserId(
        @Path("user_id") userId: Int
    ): Response<User>

    @GET("/api/v1/users/followerNum/{user_id}/")
    suspend fun getFollowerCountByUserId(
        @Path("user_id") userId: Int
    ): Response<ResponseBody>

    @GET("/api/v1/users/followingNum/{user_id}/")
    suspend fun getFollowingCountByUserId(
        @Path("user_id") userId: Int
    ): Response<ResponseBody>

    @GET("/api/v1/users/feedsNum/{user_id}/")
    suspend fun getFeedCountByUserId(
        @Path("user_id") userId: Int
    ): Response<ResponseBody>

    @POST("/api/v1/users/profile/")
    suspend fun updateUser(
        @Body updateUserRequest: UpdateUserRequest
    ): Response<Void>

    @GET("api/v1/feed/self/")
    suspend fun getMyFeeds(@Query("offset")offset : Int, @Query("number") number:Int):Response<Page>

    @GET("api/v1/feed/other/{user_id}/")
    suspend fun getFeedsById(@Path("user_id") id: Int, @Query("offset")offset : Int, @Query("number") number:Int):Response<Page>

    @GET("api/v1/feed/tag/{user_id}/")
    suspend fun getTaggedFeedsByUserId(@Path("user_id") id: Int, @Query("offset")offset : Int, @Query("number") number:Int):Response<Page>

    @POST("api/v1/users/profilePhoto/")
    suspend fun setProfilePhoto(@Body setProfilePhotoRequest: SetProfilePhotoRequest): Response<Void>

    @GET("api/v1/users/profilePhoto/{user_id}/")
    suspend fun getProfilePhoto(@Path("user_id") id: Int): Response<PhotoUrl>

    @GET("api/v1/users/isFollowing/{user_id}/")
    suspend fun checkFollowing(@Path("user_id") id: Int): Response<ResponseBody>

    @POST("api/v1/users/follow/{user_id}/")
    suspend fun follow(@Path("user_id") id: Int)

    @DELETE("api/v1/users/unfollow/{user_id}/")
    suspend fun unfollow(@Path("user_id") id: Int)
}