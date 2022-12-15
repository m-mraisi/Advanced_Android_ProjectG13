package com.G13.group.repository

import android.content.Context
import android.util.Log
import android.widget.Toast
import com.google.android.gms.tasks.Task
import com.google.firebase.FirebaseException
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.tasks.asDeferred

class AuthRepo() {
    var mAuth = FirebaseAuth.getInstance()
    var TAG = "Auth_REPO"

    suspend fun createAccount(contect: Context, email: String, password: String): String? {
//        SignUp using FirebaseAuth
        var userUID: String? = null
        val task: Task<AuthResult> = mAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    userUID = mAuth.currentUser?.uid
                    // account successfully created
                    Log.d(TAG, "SignUpFragment: Created account successfully")
                } else {
                    // Failed to create account
                    Log.d(TAG, "SignUpFragment:  ${task.exception}")
                    Toast.makeText(contect, "${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
        val deferredDataSnapshot: kotlinx.coroutines.Deferred<AuthResult> = task.asDeferred()
        val data: AuthResult = deferredDataSnapshot.await()
        return userUID
    }

    suspend fun signIn(context: Context, email: String, password: String): String? {
        var signedInUserUId: String? = null
        try {
            val task: Task<AuthResult> = mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        signedInUserUId = task.result?.user?.uid
                    } else {
                        Toast.makeText(context, "Invalid email and password.", Toast.LENGTH_SHORT)
                            .show()
                    }
                }
            val deferredDataSnapshot: kotlinx.coroutines.Deferred<AuthResult> = task.asDeferred()
            deferredDataSnapshot.await()
        } catch (e: FirebaseException) {
            Log.d(TAG, "signIn: ${e.cause?.message}")
        }
        return signedInUserUId
    }

    suspend fun sendResetPasswordLink(email: String): Boolean {
        var wasSuccess: Boolean = false
        return try {
            val task = mAuth.sendPasswordResetEmail(email).addOnSuccessListener {
                wasSuccess = true
            }.addOnFailureListener {
                Log.e(TAG, "sendResetPasswordLink: ${it.cause?.message}")
            }
            task.asDeferred().await()
            wasSuccess
        } catch (e: FirebaseException) {
            Log.e(TAG, "sendResetPasswordLink: ${e.cause?.message}")
            wasSuccess
        }
    }

    fun logoutUser() {
        try {
            mAuth.signOut()
        } catch (e: FirebaseException) {
            Log.d(TAG, "logoutUser: Signout on firebse failed")
        }
    }
}