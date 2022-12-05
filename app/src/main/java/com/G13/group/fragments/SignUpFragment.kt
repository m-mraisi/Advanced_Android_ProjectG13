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
    val TAG:String = "SIGNUP-FRAGMENT"
    private var _binding: FragmentSignUpBinding? = null
    private lateinit var mAuth:FirebaseAuth
    private val binding get() = _binding!!
    private var showSignUpButton = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentSignUpBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: android.view.View, savedInstanceState: android.os.Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onResume() {
        super.onResume()
        mAuth = FirebaseAuth.getInstance()
        binding.btnSignUp.setOnClickListener {
            Log.d(TAG, "SignUpFragment: SignUp Button Clicked")
//            viewLifecycleOwner.lifecycleScope.launch {
                if(validateData()){
                    // TODO navigate to feed screen
                    Log.d(TAG, "SignUpFragment: success creation!")
                }
                else{
                    Log.d(TAG, "SignUpFragment: failed creation!")
                }
//            }
        }
    }

    private fun validateData():Boolean {
        var validData = true
        var email = ""
        var password = ""
        if (binding.edtEmail.text.toString().isEmpty()) {
            binding.edtEmail.error = "Email Cannot be Empty"
            validData = false
        } else {
            email = binding.edtEmail.text.toString()
        }
        if (binding.edtPassword.text.toString().isEmpty()) {
            binding.edtPassword.error = "Password Cannot be Empty"
            validData = false
        } else {
            if (binding.edtPasswordConfirm.getText().toString().isEmpty()) {
                binding.edtPasswordConfirm.setError("Confirm Password Cannot be Empty")
                validData = false
            } else {
                if (binding.edtPassword.text.toString() != binding.edtPasswordConfirm.text.toString()
                ) {
                    binding.edtPasswordConfirm.error = "Both passwords must be same"
                    validData = false
                } else {
                    password = binding.edtPassword.text.toString()
                }
            }
        }
        return if (validData) {
            createAccount(email, password)
        } else {
            Toast.makeText(requireContext(), "Please provide correct inputs", Toast.LENGTH_SHORT).show()
            false
        }
    }

    private fun createAccount(email: String, password: String):Boolean {
//        SignUp using FirebaseAuth
        var validate = false
        mAuth.createUserWithEmailAndPassword(email,password)
            .addOnCompleteListener {task->
                if(task.isSuccessful){
                    // account successfully created
                    Log.d(TAG, "SignUpFragment: Created account successfully")
                    saveToPrefs(email, password)
                    validate = true
                }
                else{
                    // Failed to create account
                    Log.d(TAG,"SignUpFragment:  ${task.exception}")
                    Toast.makeText(requireContext(), "${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
        return validate
    }

    private fun saveToPrefs(email: String, password: String) {
        Log.d(TAG, "SignUpFragment: Saving to prefs")
        val prefs = requireContext().getSharedPreferences(requireContext().toString(), AppCompatActivity.MODE_PRIVATE)
        prefs.edit().putString("USER_EMAIL", email).apply()
        prefs.edit().putString("USER_PASSWORD", password).apply()
    }
}