package com.G13.group.interfaces

import com.G13.group.models.Post

interface IOnPostsListener {
    fun onCommentsClickListener(post: Post)

    fun postsDataChangeListener()

    fun onDeletePostListener(post: Post)
}