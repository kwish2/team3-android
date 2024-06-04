package com.wafflestudio.wafflestagram.network.dto

data class UpdateUserRequest (
    val name: String?,
    val nickname: String?,
    val website: String?,
    val bio: String?
)