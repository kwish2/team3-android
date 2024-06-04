package com.wafflestudio.wafflestagram.network

import com.wafflestudio.wafflestagram.model.FollowPage
import com.wafflestudio.wafflestagram.model.User
import com.wafflestudio.wafflestagram.model.UserPage
import retrofit2.Response
import retrofit2.http.*

interface SearchService {

    @GET("api/v1/users/search/")
    suspend fun search(@Query("nickname_prefix") nickname: String) : Response<UserPage>

    @GET("api/v1/users/following/{user_id}/")
    suspend fun checkFollowing(@Path("user_id") id: Int): Response<Boolean>

    @POST("api/v1/users/follow/{user_id}/")
    suspend fun follow(@Path("user_id") id: Int)

    @DELETE("api/v1/users/unfollow/{user_id}/")
    suspend fun unfollow(@Path("user_id") id: Int)

    @GET("api/v1/users/following/")
    suspend fun getMyFollowing(@Query("offset") offset: Int, @Query("number") number: Int) : Response<FollowPage>

    @GET("api/v1/users/me/")
    suspend fun getMe() : Response<User>
}