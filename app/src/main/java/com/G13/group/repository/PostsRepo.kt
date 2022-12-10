package com.G13.group.repository

import android.annotation.SuppressLint
import android.util.Log
import com.G13.group.interfaces.IOnPostsListener
import com.G13.group.models.Post
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.*
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.tasks.asDeferred

class PostsRepo(
    private val clickListener: IOnPostsListener,
) {
    private val TAG = "POSTS_REPO"
    private val db = Firebase.firestore
    private val COLLECTION_NAME = "posts"
    private val FIELD_USERNAME = "username"
    private val FIELD_CAPTION = "caption"
    private val FIELD_COMMENTS = "comments"
    private val FIELD_IMAGE_ID = "imageId"
    private val FIELD_COMMENT = "comment"


    @SuppressLint("CheckResult")
    fun syncPosts() {
        try {
            db.collection(COLLECTION_NAME).addSnapshotListener(EventListener { snapshot, error ->

                if (error != null) {
                    Log.e(TAG, "SyncPosts: Listening to collection documents failed $error")
                    return@EventListener
                }
                if (snapshot != null) {
                    Log.d(
                        TAG,
                        "SyncPosts: ${snapshot.size()} Received the documents from collection $snapshot"
                    )

                    // process the received documents
                    val postsArrayList: MutableList<Post> = mutableListOf<Post>()

                    for (documentChange in snapshot.documentChanges) {

                        val currentPost: Post = documentChange.document.toObject(Post::class.java)
                        currentPost.id = documentChange.document.id
                        when (documentChange.type) {
                            DocumentChange.Type.ADDED -> {
                                Log.d(TAG, "SyncPosts: added a document $currentPost")
                                postsArrayList.add(currentPost)
                                var dataSourceArrayList: ArrayList<Post> =
                                    DataSource.getInstance().dataSourcePostsArrayList
                                dataSourceArrayList.add(currentPost)
                                //clickListener.postsDataChangeListener()
                            }
                            DocumentChange.Type.MODIFIED -> {
                                var dataSourceArrayList: ArrayList<Post> =
                                    DataSource.getInstance().dataSourcePostsArrayList
                                Log.d(TAG, "SyncPosts: modified a document $currentPost")
                                Log.d(
                                    TAG,
                                    "SyncPosts: modified a document: dataPostsSize: ${dataSourceArrayList.size}"
                                )
                                for ((i, post) in dataSourceArrayList.withIndex()) {
                                    Log.d(
                                        TAG,
                                        "SyncPosts: modified a document postId: ${post.id} --- documentId: ${documentChange.document.id}"
                                    )
                                    if (post.id == documentChange.document.id) {
                                        Log.d(TAG, "SyncPosts: found a an id to modify")
                                        dataSourceArrayList[i].comments =
                                            documentChange.document.toObject(Post::class.java).comments
                                        dataSourceArrayList[i].caption =
                                            documentChange.document.toObject(Post::class.java).caption
                                    } else {
                                        Log.d(TAG, "SyncPosts: did not find the post")
                                    }
                                }
                            }
                            DocumentChange.Type.REMOVED -> {
                                postsArrayList.remove(currentPost)
                                var dataSourceArrayList: ArrayList<Post> =
                                    DataSource.getInstance().dataSourcePostsArrayList
                                dataSourceArrayList.remove(currentPost)
                                //clickListener.postsDataChangeListener()

                            }
                        }

                        clickListener.postsDataChangeListener()
                    }
                    Log.d(TAG, "SyncPosts: $postsArrayList")
                } else {
                    Log.d(TAG, "SyncPosts: No documents received from collection $COLLECTION_NAME")
                }

            })
        } catch (ex: Exception) {
            Log.d(TAG, "SyncPosts: exception: $ex")
        }
    }

    suspend fun deletePost(post: Post): Boolean {
        var isSuccess = false
        val task: Task<Void> = db.collection(COLLECTION_NAME).document(post.id)
            .delete()
            .addOnSuccessListener {
                Log.d(TAG, "DocumentSnapshot successfully deleted!")
                isSuccess = true
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error deleting document", e)
            }
        val deferredDataSnapshot: Deferred<Void> =
            task.asDeferred()
        val result: Void = deferredDataSnapshot.await()
        return isSuccess
    }

}