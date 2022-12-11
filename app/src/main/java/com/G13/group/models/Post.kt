package com.G13.group.models

import java.io.Serializable
import java.util.*

data class Post(
    var id: String = UUID.randomUUID().toString(),
    var caption: String = "",
    var imageId: String = "",
    var username: String = "",
    var comments: ArrayList<Comment> = arrayListOf<Comment>()
) : Serializable {
    override fun toString(): String {
        return "Post(id='$id', caption='$caption', imageId='$imageId', username='$username', comments=$comments)"
    }

    fun toHashMap(): HashMap<String, Any> {
        val map = hashMapOf<String, Any>(
            "id" to id,
            "caption" to caption,
            "imageId" to imageId,
            "username" to username,
            "comments" to comments
        )
        return map
    }
}