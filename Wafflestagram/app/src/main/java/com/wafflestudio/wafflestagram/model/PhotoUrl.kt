package com.wafflestudio.wafflestagram.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
class PhotoUrl(
    @Json(name = "profilePhotoURL")
    val profilePhotoURL : String
)