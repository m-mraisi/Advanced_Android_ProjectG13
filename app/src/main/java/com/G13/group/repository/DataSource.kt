package com.G13.group.repository

import com.G13.group.models.Comment
import com.G13.group.models.Post

open class DataSource {

    companion object {
        @Volatile
        private lateinit var instance: DataSource

        fun getInstance(): DataSource {
            synchronized(this) {
                if (!::instance.isInitialized) {
                    instance = DataSource()
                }
                return instance
            }
        }
    }

    var dataSourcePostsArrayList: ArrayList<Post> = arrayListOf()
    var dataSourceCommentsArrayList: ArrayList<Comment> = arrayListOf()

//    var selectedLessonPosition:Int? = null


    // put any functions / operations you want to perform on data in the singleton here
}