package com.G13.group.repository

import android.util.Log
import com.G13.group.models.User
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.tasks.asDeferred

class UsersRepo {

    private val TAG = this.toString()
    private val db = Firebase.firestore
    private val COLLECTION_NAME = "users"
    private val FIELD_USERNAME = "username"

    suspend fun addUserToDB(newUser: User): String? {
        try {
            var userRes: String? = null

            if (checkIfUserExists(newUser.username)) {
                Log.d(TAG, "addUserToDB: User already taken")
                return userRes
            }
            val data: MutableMap<String, Any> = HashMap() // needed to store to FireStore collection

            data[FIELD_USERNAME] = newUser.username

            val task: Task<Void> =
                db.collection(COLLECTION_NAME).document(newUser.id).set(data)
                    .addOnSuccessListener { doc ->
                        Log.d(TAG, "addUserToDB - added document: $doc")
                        userRes = "success"
                    }.addOnFailureListener { ex ->
                    Log.d(TAG, "addUserToDB: $ex")
                }
            val deferredDataSnapshot: Deferred<Void> =
                task.asDeferred()
            val result: Void = deferredDataSnapshot.await()


            return userRes
        } catch (ex: Exception) {
            Log.e(TAG, "addUserToDB: $ex")
            return null
        }
    }

    private suspend fun checkIfUserExists(username: String): Boolean {
        var found = false
        Log.d(TAG, "addUserToDB: checking user")
        val task: Task<QuerySnapshot> =
            db.collection(COLLECTION_NAME).whereEqualTo(FIELD_USERNAME, username)
                .get()
                .addOnSuccessListener { documents ->
                    found = if (documents.isEmpty) {
                        Log.d(TAG, "addUserToDB: checking user: user available")
                        false
                    } else {
                        Log.d(TAG, "addUserToDB: checking user: User already taken")
                        true
                    }
                }.addOnFailureListener {
                    Log.d(TAG, "addUserToDB: Error getting documents")
                    found = true
                }
        val deferredDataSnapshot: kotlinx.coroutines.Deferred<QuerySnapshot> = task.asDeferred()
        val data: QuerySnapshot = deferredDataSnapshot.await()
        return found
    }
}