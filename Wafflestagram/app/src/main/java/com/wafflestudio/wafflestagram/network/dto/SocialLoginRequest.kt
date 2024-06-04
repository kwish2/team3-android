package com.wafflestudio.wafflestagram.network.dto
// social login 이후 token 인증을 위해 token을 backend 서버에 보내기 위한 dto
data class SocialLoginRequest(
    val token: String
)
