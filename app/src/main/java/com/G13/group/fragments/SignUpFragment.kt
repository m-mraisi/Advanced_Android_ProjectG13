package com.G13.group.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.G13.group.databinding.FragmentSignUpBinding
import com.google.firebase.auth.FirebaseAuth

class SignUpFragment : Fragment() {
    val TAG:String = "LOGIN-FRAGMENT"
    private var _binding: FragmentSignUpBinding? = null
    private val binding get() = _binding!!
    private lateinit var mAuth: FirebaseAuth


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentSignUpBinding.inflate(inflater, container, false)
        val view = binding.root

        return view
    }

    override fun onViewCreated(view: android.view.View, savedInstanceState: android.os.Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//        mAuth = FirebaseAuth.getInstance()

    }

    private fun validateData() {
        var validData = true
        var email = ""
        var password = ""
        if (binding.edtEmail.getText().toString().isEmpty()) {
            binding.edtEmail.setError("Email Cannot be Empty")
            validData = false
        } else {
            email = binding.edtEmail.getText().toString()
        }
        if (binding.edtPassword.getText().toString().isEmpty()) {
            binding.edtPassword.setError("Password Cannot be Empty")
            validData = false
        } else {
            if (binding.edtPasswordConfirm.getText().toString().isEmpty()) {
                binding.edtPasswordConfirm.setError("Confirm Password Cannot be Empty")
                validData = false
            } else {
                if (!binding.edtPassword.getText().toString()
                        .equals(binding.edtPasswordConfirm.getText().toString())
                ) {
                    binding.edtPasswordConfirm.setError("Both passwords must be same")
                    validData = false
                } else {
                    password = binding.edtPassword.getText().toString()
                }
            }
        }
        if (validData) {
//            createAccount(email, password)
        } else {
            //Toast.makeText(this, "Please provide correct inputs", Toast.LENGTH_SHORT).show()
        }
    }

    private fun createAccount(email: String, password: String) {
//        SignUp using FirebaseAuth
        mAuth.createUserWithEmailAndPassword(email,password)
            .addOnCompleteListener {task->
                if(task.isSuccessful){
                    // account successfully created
                    Log.d(TAG, "Created account successfully")
                    saveToPrefs(email, password)
                }
                else{
                    // Failed to create account
                    Log.d(TAG, "Failed to create account!, ${task.exception}")
                    //Toast.makeText(this@SignUpFragment, "${task.exception?.localizedMessage}", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun saveToPrefs(email: String, password: String) {
//        val prefs = applicationContext.getSharedPreferences(packageName, AppCompatActivity.MODE_PRIVATE
//        )
//        prefs.edit().putString("USER_EMAIL", email).apply()
//        prefs.edit().putString("USER_PASSWORD", password).apply()
    }
}