package com.wafflestudio.wafflestagram.ui.main

interface FeedInterface {
    fun like(id: Int, position: Int)

    fun unlike(id: Int, position: Int)

    fun deleteFeed(id: Int, position: Int)
}