package com.wafflestudio.wafflestagram.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import java.time.LocalDateTime

@JsonClass(generateAdapter = true)
class Comment (
    @Json(name = "id")
    val id: Long = 0,
    @Json(name = "writer")
    val writer: User,
    @Json(name = "text")
    val text: String = "",
    @Json(name = "reply")
    val replies: List<Reply>,
    @Json(name = "createdAt")
    val createdAt : LocalDateTime? = LocalDateTime.now(),
    @Json(name = "updatedAt")
    val updatedAt : LocalDateTime? = LocalDateTime.now()
)