package com.wafflestudio.wafflestagram.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import java.io.Serializable

@JsonClass(generateAdapter = true)
class User(
    @Json(name = "bio")
    val bio: String? = null,
    @Json(name = "email")
    val email: String? = null,
    @Json(name = "id")
    val id: Long,
    @Json(name = "name")
    val name: String? = null,
    @Json(name = "nickname")
    val username: String? = null,
    @Json(name = "profilePhotoURL")
    val profilePhotoURL: String? = null,
    @Json(name = "public")
    val public: Boolean = true,
    @Json(name = "website")
    val website: String? = null
) : Serializable{
    override fun equals(other: Any?): Boolean {
        return if(other is User)
            other.id == this.id
        else
            false
    }
}