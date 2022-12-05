package com.G13.group.repository

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.core.content.ContentProviderCompat.requireContext
import com.google.firebase.auth.FirebaseAuth

class AuthRepo() {
    var mAuth = FirebaseAuth.getInstance()
    var TAG = "Auth_REPO"

    fun createAccount(contect: Context, email: String, password: String):Boolean {
//        SignUp using FirebaseAuth
        var validate = false
        mAuth.createUserWithEmailAndPassword(email,password)
            .addOnCompleteListener {task->
                if(task.isSuccessful){
                    // account successfully created
                    Log.d(TAG, "SignUpFragment: Created account successfully")
                    validate = true
                }
                else{
                    // Failed to create account
                    Log.d(TAG,"SignUpFragment:  ${task.exception}")
                    Toast.makeText(contect, "${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
        return validate
    }
}