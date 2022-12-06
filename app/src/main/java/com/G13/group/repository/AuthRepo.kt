package com.G13.group.repository

import android.content.Context
import android.util.Log
import android.widget.Toast
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.tasks.asDeferred

class AuthRepo() {
    var mAuth = FirebaseAuth.getInstance()
    var TAG = "Auth_REPO"

    suspend fun createAccount(contect: Context, email: String, password: String): Boolean {
//        SignUp using FirebaseAuth
        var validate = false
        val task: Task<AuthResult> = mAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // account successfully created
                    Log.d(TAG, "SignUpFragment: Created account successfully")
                    validate = true
                } else {
                    // Failed to create account
                    Log.d(TAG, "SignUpFragment:  ${task.exception}")
                    Toast.makeText(contect, "${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
        val deferredDataSnapshot: kotlinx.coroutines.Deferred<AuthResult> = task.asDeferred()
        val data: AuthResult = deferredDataSnapshot.await()
        return validate
    }
}