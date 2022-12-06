package com.G13.group.models

class Comment(var commentDetails: String, var username: String) {
    override fun toString(): String {
        return "Comment(commentDetails='$commentDetails', username='$username')"
    }
}