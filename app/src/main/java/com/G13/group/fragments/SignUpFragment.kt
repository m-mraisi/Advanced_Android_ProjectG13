package com.G13.group.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.navOptions
import com.G13.group.R
import com.G13.group.databinding.FragmentSignUpBinding
import com.G13.group.models.User
import com.G13.group.repository.AuthRepo
import com.G13.group.repository.DataSource
import com.G13.group.repository.UsersRepo
import kotlinx.coroutines.launch


class SignUpFragment : Fragment() {
    val TAG: String = "SIGNUP-FRAGMENT"
    private var _binding: FragmentSignUpBinding? = null
    private val binding get() = _binding!!
    private lateinit var authRepo: AuthRepo
    private lateinit var usersRepo: UsersRepo
    private lateinit var datasource: DataSource
    var email = ""
    var password = ""
    var username = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSignUpBinding.inflate(inflater, container, false)
        val view = binding.root
        datasource = DataSource.getInstance()
        return view
    }

    override fun onViewCreated(view: android.view.View, savedInstanceState: android.os.Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        authRepo = AuthRepo() // initialize AuthRepo
        usersRepo = UsersRepo() // initialize UsersRepo

        binding.btnSignUp.setOnClickListener {
            Log.d(TAG, "SignUpFragment: SignUp Button Clicked")
            if (validateData()) {
                viewLifecycleOwner.lifecycleScope.launch {
                    val userUIDFromAuth = authRepo.createAccount(requireContext(), email, password)
                    if (userUIDFromAuth != null) {
                        val userId =
                            usersRepo.addUserToDB(User(id = userUIDFromAuth, username = username))
                        if (userId == null) {
                            Toast.makeText(
                                requireContext(),
                                "Username already taken",
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            saveToPrefs(email, password, username)
                            Log.d(TAG, "SignUpFragment: success creation!")
                            // navigate to feed screen

                            val options = navOptions {
                                anim {
                                    enter = R.anim.slide_in_right
                                    exit = R.anim.slide_out_left
                                    popEnter = R.anim.slide_in_left
                                    popExit = R.anim.slide_out_right
                                }
                            }
                            val action =
                                SignUpFragmentDirections.actionSignUpFragmentToFeedFragment()
                            findNavController().navigate(action, options)
                        }
                    }
                }
            } else {
                Log.d(TAG, "SignUpFragment: failed creation!")
            }
        }


        binding.edtSignIn.setOnClickListener {
            val options = navOptions {
                anim {
                    enter = R.anim.slide_in_right
                    exit = R.anim.slide_out_left
                    popEnter = R.anim.slide_in_left
                    popExit = R.anim.slide_out_right
                }
            }
            val action = SignUpFragmentDirections.actionSignUpFragmentToLoginFragment()
            findNavController().navigate(action, options)
        }
    }

    override fun onResume() {
        super.onResume()
    }

    private fun validateData(): Boolean {
        var validData = true
        email = ""
        password = ""
        username = binding.edtUsername.text.toString()
        if (binding.edtEmail.text.toString().isEmpty()) {
            binding.edtEmail.error = "Email Cannot be Empty"
            validData = false
        } else {
            email = binding.edtEmail.text.toString()
        }
        if (username.isBlank()) {
            binding.edtUsername.error = "Username is invalid"
        } else if (username.contains(" ") || username.length < 4) {
            binding.edtUsername.error = "Username must be >= 8 characters and not to contain spaces"
            validData = false
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
        return validData
    }

    private fun saveToPrefs(email: String, password: String, username: String) {
        Log.d(TAG, "SignUpFragment: Saving to prefs")
        val prefs = requireContext().getSharedPreferences(
            requireContext().toString(),
            AppCompatActivity.MODE_PRIVATE
        )
        datasource.username = username
        prefs.edit().putString("USER_EMAIL", email).apply()
        prefs.edit().putString("USER_PASSWORD", password).apply()
        prefs.edit().putString("USERNAME", username).apply()
    }


    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}