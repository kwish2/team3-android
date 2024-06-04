package com.wafflestudio.wafflestagram.network.dto

data class EditPostRequest(
    val content: String,
    val tags: List<String>,
    val userTags: List<String>
)
