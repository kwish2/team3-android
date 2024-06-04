package com.wafflestudio.wafflestagram.network

import com.wafflestudio.wafflestagram.model.Feed
import com.wafflestudio.wafflestagram.model.FollowPage
import com.wafflestudio.wafflestagram.model.Page
import com.wafflestudio.wafflestagram.model.User
import com.wafflestudio.wafflestagram.network.dto.AddPostRequest
import com.wafflestudio.wafflestagram.network.dto.EditPostRequest
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.*

interface FeedService {

    @POST("api/v1/feed/")
    suspend fun addPost(@Body addPostRequest: AddPostRequest): Response<Feed>

    @GET("api/v1/feed/")
    suspend fun getFeeds(@Query("offset") offset: Int, @Query("number") number: Int) : Response<Page>

    @GET("api/v1/feed/{feed_id}/")
    suspend fun getFeedById(@Path("feed_id") id: Int): Response<Feed>

    @DELETE("api/v1/feed/{feed_id}/")
    suspend fun deleteFeed(@Path("feed_id")id: Int): Response<ResponseBody>

    @PUT("api/v1/feed/{feed_id}/")
    suspend fun editFeed(@Path("feed_id")id: Int, @Body editPostRequest: EditPostRequest): Response<Feed>

    @POST("api/v1/feed/like/{feed_id}/")
    suspend fun like(@Path("feed_id") id :Int): Response<Feed>

    @DELETE("api/v1/feed/like/{feed_id}/")
    suspend fun unlike(@Path("feed_id") id: Int): Response<Feed>

    @GET("api/v1/users/me/")
    suspend fun getMe() : Response<User>

    @GET("api/v1/users/following/{user_id}/")
    fun checkFollowing(@Path("user_id") id: Int): Response<Boolean>

    @POST("api/v1/users/follow/{user_id}/")
    suspend fun follow(@Path("user_id") id: Int)

    @DELETE("api/v1/users/unfollow/{user_id}/")
    suspend fun unfollow(@Path("user_id") id: Int)

    @GET("api/v1/feed/other/{user_id}/")
    suspend fun getFeedsByUserId(@Path("user_id")id: Int, @Query("offset") offset: Int, @Query("number") number: Int): Response<Page>

    @GET("api/v1/feed/tag/{user_id}/")
    suspend fun getTaggedFeedsByUserId(@Path("user_id")id: Int, @Query("offset") offset: Int, @Query("number") number: Int): Response<Page>

    @GET("api/v1/users/following/{user_id}/")
    suspend fun getFollowingByUserId(@Path("user_id")id: Int, @Query("offset") offset: Int, @Query("number") number: Int) : Response<FollowPage>

    @GET("api/v1/users/follower/{user_id}/")
    suspend fun getFollowerByUserId(@Path("user_id")id: Int, @Query("offset") offset: Int, @Query("number") number: Int) : Response<FollowPage>

    @GET("api/v1/users/following/")
    suspend fun getMyFollowing(@Query("offset") offset: Int, @Query("number") number: Int) : Response<FollowPage>
}