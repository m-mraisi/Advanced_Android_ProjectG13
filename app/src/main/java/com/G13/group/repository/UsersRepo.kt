package com.G13.group.repository

import android.util.Log
import com.G13.group.models.User
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.inject.Deferred
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.asDeferred

class UsersRepo {

    private val TAG = this.toString()
    private val db = Firebase.firestore
    private val COLLECTION_NAME = "users"
    private val FIELD_USERNAME = "username"

    suspend fun addUserToDB(newUser: User):String? {
        try {
            var userId:String? = null

            if(checkIfUserExists(newUser.username)){
                Log.d(TAG, "addUserToDB: User already taken")
                return userId
            }
            Log.d(TAG, "addUserToDB: it did not stop me! ${newUser.username}")
            val data: MutableMap<String, Any> = HashMap() // needed to store to FireStore collection

            data[FIELD_USERNAME] = newUser.username

            val task: Task<DocumentReference> =  db.collection(COLLECTION_NAME).add(data).addOnSuccessListener { docRef ->
                Log.d(TAG, "addUserToDB - added document: ${docRef.id}")
                userId = docRef.id
            }.addOnFailureListener { ex ->
                Log.d(TAG, "addUserToDB: $ex")
            }
            val deferredDataSnapshot: kotlinx.coroutines.Deferred<DocumentReference> = task.asDeferred()
            val result: DocumentReference = deferredDataSnapshot.await()


            return userId
        } catch (ex: Exception) {
            Log.e(TAG, "addUserToDB: $ex")
            return null
        }
    }

    private suspend fun checkIfUserExists(username:String):Boolean {
        var found = false
        Log.d(TAG, "addUserToDB: checking user")
        val task: Task<QuerySnapshot> = db.collection(COLLECTION_NAME).whereEqualTo(FIELD_USERNAME, username)
            .get()
            .addOnSuccessListener {documents->
                found = if (documents.isEmpty){
                    Log.d(TAG, "addUserToDB: checking user: user available")
                    false
                } else{
                    Log.d(TAG, "addUserToDB: checking user: User already taken")
                    true
                }
        }.addOnFailureListener {
                Log.d(TAG, "addUserToDB: Error getting documents")
                found = true
        }
        Log.d(TAG, "addUserToDB: checking user 2")
        val deferredDataSnapshot: kotlinx.coroutines.Deferred<QuerySnapshot> = task.asDeferred()
        Log.d(TAG, "addUserToDB: checking user 3")
        val data: QuerySnapshot = deferredDataSnapshot.await()
        Log.d(TAG, "addUserToDB: checking user 4")
        return found
    }
}