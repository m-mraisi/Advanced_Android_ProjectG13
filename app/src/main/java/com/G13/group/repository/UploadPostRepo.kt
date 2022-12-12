package com.G13.group.repository

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.widget.Toast
import com.G13.group.models.Post
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.tasks.asDeferred
import java.util.*

class UploadPostRepo() {
    private val TAG = "UPLOAD_POST_REPO"
    private val db = Firebase.firestore
    private val storage = Firebase.storage
    private val REQUEST_CODE = 100
    private val COLLECTION_NAME = "posts"
    private var uploadedImageUrl: String? = null
    private val dataSource = DataSource.getInstance()

    fun uploadImage(requestCode: Int, resultCode: Int, data: Intent?, context: Context?) {
        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_CODE) {
            Log.d(TAG, "onActivityResult: Image Selected!")
            Toast.makeText(context, "Image was selected", Toast.LENGTH_LONG).show()
            val imageData = data?.data
            if (imageData != null) {
                uploadImageToStorage(imageData)
            }
        } else if (resultCode == 0) {
            Toast.makeText(context, "No image was selected!", Toast.LENGTH_LONG).show()
        }
    }

    suspend fun uploadPost(caption: String, username: String): Boolean {
        var isPosted = false
        if (uploadedImageUrl != null) {
            val postMap =
                Post(
                    caption = caption,
                    username = username,
                    imageId = uploadedImageUrl!!
                ).toHashMap()
            val task: Task<DocumentReference> =
                db.collection(COLLECTION_NAME)
                    .add(postMap).addOnSuccessListener { documentReference ->
                        Log.d(TAG, "DocumentSnapshot written with ID: ${documentReference.id}")
                        isPosted = true
                    }
                    .addOnFailureListener { e ->
                        Log.w(TAG, "Error adding document", e)
                    }
            val deferredDataSnapshot: Deferred<DocumentReference> =
                task.asDeferred()
            val result: DocumentReference = deferredDataSnapshot.await()
        }
        return isPosted
    }

    private fun uploadImageToStorage(imageData: Uri) {
        val storageRef = storage.reference
        val mountainsRef = storageRef.child("${dataSource.username}-${UUID.randomUUID()}.jpg")
        val uploadTask = mountainsRef.putFile(imageData)

        uploadTask.addOnFailureListener {
            Log.e(TAG, "uploadImageToStorage: Unable to upload image to storage")
        }.addOnSuccessListener { taskSnapshot ->
            taskSnapshot.storage.downloadUrl.addOnSuccessListener { imageUrl ->
                uploadedImageUrl = imageUrl.toString()
            }
        }
    }


}