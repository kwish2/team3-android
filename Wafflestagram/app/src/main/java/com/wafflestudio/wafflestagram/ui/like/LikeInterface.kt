package com.wafflestudio.wafflestagram.ui.like

interface LikeInterface {
    fun follow(id : Int)
    //fun checkFollowing(id: Int) : Response<Boolean>
    fun unfollow(id : Int)
}