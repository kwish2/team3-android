package com.wafflestudio.wafflestagram.ui.detail

interface DetailTaggedFeedInterface {
    fun like(id: Int, position : Int)

    fun unlike(id: Int, position: Int)

    fun deleteFeed(id: Int, position: Int)
}