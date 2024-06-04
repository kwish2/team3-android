package com.wafflestudio.wafflestagram.network.dto

data class SignUpRequest(
    val email:String,
    val password: String,
    val public: Boolean = true,
    val name: String,
    val nickname: String,
    val birthday: String,
    val phoneNumber: String
)
