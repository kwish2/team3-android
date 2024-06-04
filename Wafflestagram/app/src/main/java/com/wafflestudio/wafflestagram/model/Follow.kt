package com.wafflestudio.wafflestagram.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
class Follow(
    @Json(name = "id")
    val id : Long = 0,
    @Json(name = "user")
    val user: User
){
    override fun equals(other: Any?): Boolean {
        return if(other is Follow)
            other.user == this.user
        else
            false
    }
}