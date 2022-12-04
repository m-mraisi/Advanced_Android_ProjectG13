package com.G13.group.models

class Post(var id:String, var caption:String, var imageId:String,var comments:ArrayList<Comment>) {

    override fun toString(): String {
        return "Post(id='$id', caption='$caption', imageId='$imageId', comments=$comments)"
    }
}