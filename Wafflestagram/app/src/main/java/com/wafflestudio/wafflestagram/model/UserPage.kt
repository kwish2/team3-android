package com.wafflestudio.wafflestagram.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
class UserPage(
    @Json(name = "content")
    val content : List<User>,
    @Json(name = "last")
    val last: Boolean,
    @Json(name = "first")
    val fisrt: Boolean,
    @Json(name = "totalPages")
    val totalPages: Int
)