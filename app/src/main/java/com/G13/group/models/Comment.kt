package com.G13.group.models

data class Comment(var comment: String = "", var username: String = "") {
    override fun toString(): String {
        return "Comment(comment='$comment', username='$username')"
    }
}