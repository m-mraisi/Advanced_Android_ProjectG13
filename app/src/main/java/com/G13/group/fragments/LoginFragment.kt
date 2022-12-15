package com.G13.group.fragments

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.navOptions
import com.G13.group.R
import com.G13.group.databinding.FragmentLoginBinding
import com.G13.group.repository.AuthRepo
import com.G13.group.repository.DataSource
import com.G13.group.repository.UsersRepo
import kotlinx.coroutines.launch

class LoginFragment : Fragment() {

    val TAG: String = "LOGIN-FRAGMENT"
    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!
    private lateinit var authRepo: AuthRepo
    private lateinit var usersRepo: UsersRepo
    private lateinit var dataSource: DataSource
    private lateinit var prefs: SharedPreferences
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
        dataSource = DataSource.getInstance()
        prefs = requireContext().getSharedPreferences(
            requireContext().toString(),
            androidx.appcompat.app.AppCompatActivity.MODE_PRIVATE
        )

        setLoginDataFromSharedPrefs()

        binding.btnSignIn.setOnClickListener {
            Log.d(TAG, "SignInFragment: SignIn Button Clicked")
            if (validateData()) {
                viewLifecycleOwner.lifecycleScope.launch {
                    val signedInUserUid = authRepo.signIn(requireContext(), email, password)
                    Log.d(TAG, "SignInFragment: authResult $signedInUserUid")
                    if (signedInUserUid != null) {
                        val username: String? = usersRepo.getUsernameFromUId(signedInUserUid)
                        if (username != null) {
                            if (binding.cbRememberMe.isChecked) {
                                saveToPrefs(email, password, username)
                            } else {
                                saveToPrefs(email, password, username)
                            }

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
                    } else {
                        Toast.makeText(
                            requireContext(),
                            "User not found on server!",
                            Toast.LENGTH_LONG
                        )
                            .show()
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

        binding.btnForgotPassword.setOnClickListener {
            val goToForgotPassword =
                LoginFragmentDirections
                    .actionLoginFragmentToForgotPasswordFragment(
                        binding.edtEmail.text.toString()
                    )
            findNavController().navigate(goToForgotPassword)
        }
    }

    private fun setLoginDataFromSharedPrefs() {
        binding.edtEmail.setText(prefs.getString("USER_EMAIL", ""))
        binding.edtPassword.setText(prefs.getString("USER_PASSWORD", ""))
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

    private fun saveToPrefs(email: String? = null, password: String? = null, username: String) {
        if (email != null && password != null) {
            prefs.edit().putString("USER_EMAIL", email).apply()
            prefs.edit().putString("USER_PASSWORD", password).apply()
        }
        prefs.edit().putString("USER_USERNAME", username).apply()

        dataSource.username = username
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        val callback: OnBackPressedCallback =
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    activity?.finish()
                }
            }
        requireActivity().onBackPressedDispatcher.addCallback(
            this,
            callback
        )

    }
}
