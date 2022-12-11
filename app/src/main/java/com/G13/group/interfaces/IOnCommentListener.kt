package com.G13.group.interfaces

import com.G13.group.models.Comment

interface IOnCommentListener {

    fun onDeleteCommentListener(comment: Comment)
}