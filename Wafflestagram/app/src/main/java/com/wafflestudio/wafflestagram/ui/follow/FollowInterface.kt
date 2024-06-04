package com.wafflestudio.wafflestagram.ui.follow

interface FollowInterface {
    fun follow(id : Int)
    //fun checkFollowing(id: Int) : Response<Boolean>
    fun unfollow(id : Int)
}