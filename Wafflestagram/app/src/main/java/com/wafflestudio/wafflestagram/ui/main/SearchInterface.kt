package com.wafflestudio.wafflestagram.ui.main

interface SearchInterface {
    fun follow(id : Int)
    //fun checkFollowing(id: Int) : Response<Boolean>
    fun unfollow(id : Int)
}