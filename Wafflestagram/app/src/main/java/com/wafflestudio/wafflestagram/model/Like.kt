package com.wafflestudio.wafflestagram.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
class Like(
    @Json(name = "id")
    val id: Long = 0,
    @Json(name = "writer")
    val writer: User
    ){
    override fun equals(other: Any?): Boolean {
        return if(other is Like)
            other.writer == this.writer
        else
            false
    }
}