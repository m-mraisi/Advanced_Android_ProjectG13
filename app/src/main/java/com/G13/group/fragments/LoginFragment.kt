package com.G13.group.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.navOptions
import com.G13.group.R
import com.G13.group.databinding.FragmentLoginBinding
import com.G13.group.repository.AuthRepo
import com.G13.group.repository.UsersRepo
import kotlinx.coroutines.launch

class LoginFragment : Fragment() {

    val TAG: String = "LOGIN-FRAGMENT"
    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!
    private lateinit var authRepo: AuthRepo
    private lateinit var usersRepo: UsersRepo
    var email = ""
    var password = ""


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: android.view.View, savedInstanceState: android.os.Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        authRepo = AuthRepo() // initialize AuthRepo
        usersRepo = UsersRepo() // initialize UsersRepo

        binding.btnSignIn.setOnClickListener {
            Log.d(TAG, "SignInFragment: SignIn Button Clicked")
            if (validateData()) {
                viewLifecycleOwner.lifecycleScope.launch {
                    val authResult = authRepo.signIn(requireContext(), email, password)
                    Log.d(TAG, "SignInFragment: authResult $authResult")
                    if (authResult) {
                        saveToPrefs(email, password)
                        val options = navOptions {
                            anim {
                                enter = R.anim.slide_in_right
                                exit = R.anim.slide_out_left
                                popEnter = R.anim.slide_in_left
                                popExit = R.anim.slide_out_right
                            }
                        }
                        val action = LoginFragmentDirections.actionLoginFragmentToFeedFragment()
                        findNavController().navigate(action, options)
                        Log.d(TAG, "SignInFragment: success creation!")
                    }

                }
            } else {
                Log.d(TAG, "SignInFragment: failed creation!")
            }
        }

        binding.btnGoToSignUp.setOnClickListener {
            val options = navOptions {
                anim {
                    enter = R.anim.slide_in_right
                    exit = R.anim.slide_out_left
                    popEnter = R.anim.slide_in_left
                    popExit = R.anim.slide_out_right
                }
            }
            val action = LoginFragmentDirections.actionLoginFragmentToSignUpFragment()
            findNavController().navigate(action, options)

        }
    }

    private fun validateData(): Boolean {
        var validData = true
        email = ""
        password = ""
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
            password = binding.edtPassword.text.toString()
        }
        return validData
    }

    private fun saveToPrefs(email: String, password: String) {
        val prefs = requireContext().getSharedPreferences(
            requireContext().toString(),
            AppCompatActivity.MODE_PRIVATE
        )
        prefs.edit().putString("USER_EMAIL", email).apply()
        prefs.edit().putString("USER_PASSWORD", password).apply()
//        prefs.edit().putString("USERNAME", username).apply()
    }

}
