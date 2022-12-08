package com.G13.group.models

import java.io.Serializable

data class Comment(var comment: String = "", var username: String = "") : Serializable {
    override fun toString(): String {
        return "Comment(comment='$comment', username='$username')"
    }
}