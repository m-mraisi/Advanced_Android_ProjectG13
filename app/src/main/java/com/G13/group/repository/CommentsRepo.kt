package com.G13.group.repository

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.G13.group.models.Comment
import com.G13.group.models.Post
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.tasks.asDeferred

class CommentsRepo {

    private val TAG = "COMMENTS_REPO"
    private val db = Firebase.firestore
    private val COLLECTION_NAME = "posts"
    private val FIELD_USERNAME = "username"
    private val FIELD_CAPTION = "caption"
    private val FIELD_COMMENTS = "comments"
    private val FIELD_IMAGE_ID = "imageId"
    private val FIELD_COMMENT = "comment"

    var allComments: MutableLiveData<List<Comment>> = MutableLiveData<List<Comment>>()

    suspend fun getPostComments(post: Post) {
        val commentsArrayList: ArrayList<Comment> = arrayListOf<Comment>()

        val docRef = db.collection(COLLECTION_NAME).document(post.id)
        val task = docRef.get().addOnSuccessListener { document ->
            if (document != null) {
                val firestorePost = document.toObject(Post::class.java)
                if (firestorePost != null) {
                    allComments.postValue(firestorePost.comments)
                    Log.d(TAG, "getPostComments: ${document.data}")
                }
            } else {
                Log.d(TAG, "No such document")
            }
        }.addOnFailureListener { ex ->
            Log.e(TAG, "getPostComments: $ex")
        }

        val deferredDataSnapshot: Deferred<DocumentSnapshot> = task.asDeferred()
        val data: DocumentSnapshot = deferredDataSnapshot.await()
    }

    suspend fun addComment(post: Post) {
        try {
            val task: Task<Void> =
                db.collection(COLLECTION_NAME).document(post.id).set(post).addOnSuccessListener {
                    Log.d(TAG, "add - added comment - new comments size ${post.comments.size}")
                }.addOnFailureListener { ex ->
                    Log.d(TAG, "addComment: $ex")
                }
            val deferredDataSnapshot: Deferred<Void> =
                task.asDeferred()
            val result: Void = deferredDataSnapshot.await()

        } catch (ex: Exception) {
            Log.e(TAG, "addUserToDB: $ex")
        }
    }
}