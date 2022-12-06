package com.G13.group.models

import java.util.*

data class User(var id: String = UUID.randomUUID().toString(), var username: String = "") {

    override fun toString(): String {
        return "User(id='$id', username='$username')"
    }
}