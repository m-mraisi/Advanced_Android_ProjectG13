package com.G13.group.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.G13.group.databinding.FragmentForgotPasswordBinding
import com.G13.group.repository.AuthRepo
import kotlinx.coroutines.launch

class ForgotPasswordFragment : Fragment() {
    val TAG: String = "FORGOT-PASSWORD-FRAGMENT"
    private var _binding: FragmentForgotPasswordBinding? = null
    private val binding get() = _binding!!
    private lateinit var authRepo: AuthRepo
    private val args: ForgotPasswordFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentForgotPasswordBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (args.email != null) {
            binding.edtEmail.setText(args.email)
        }
        authRepo = AuthRepo()

        binding.btnResetPassword.setOnClickListener {
            if (validateEmail()) {
                viewLifecycleOwner.lifecycleScope.launch {
                    val result = authRepo.sendResetPasswordLink(binding.edtEmail.text.toString())
                    if (result) {
                        Toast.makeText(requireContext(), "Reset email sent!", Toast.LENGTH_LONG)
                            .show()
                        val action =
                            ForgotPasswordFragmentDirections.actionForgotPasswordFragmentToLoginFragment()
                        findNavController().navigate(action)
                    } else {
                        Toast.makeText(
                            requireContext(),
                            "Unable to send resend link!",
                            Toast.LENGTH_LONG
                        )
                            .show()
                    }
                }

            }

        }
    }

    private fun validateEmail(): Boolean {
        val email: String = binding.edtEmail.text.toString()
        var isValidate: Boolean = true
        if (binding.edtEmail.text.toString().isEmpty()) {
            binding.edtEmail.error = "Email Cannot be Empty"
            isValidate = false
        } else {
            isValidate = true
        }
        return isValidate
    }

}