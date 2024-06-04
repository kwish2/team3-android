package com.wafflestudio.wafflestagram.network.dto

data class AddPostRequest(
    val content:String,
    val imageKeys: List<String>,
    val tags: List<String>,
    val userTags: List<String>,
)
