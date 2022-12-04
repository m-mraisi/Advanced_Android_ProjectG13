package com.G13.group.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.navOptions
import com.G13.group.R
import com.G13.group.databinding.FragmentBoardingBinding
import com.G13.group.databinding.FragmentLoginBinding
import com.G13.group.databinding.FragmentSignUpBinding


class BoardingFragment : Fragment() {
    val TAG:String = "BOARDING-FRAGMENT"
    private var _binding: FragmentBoardingBinding? = null
    private val binding get() = _binding!!


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentBoardingBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: android.view.View, savedInstanceState: android.os.Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val options = navOptions {
            anim {
                enter = R.anim.slide_in_right
                exit = R.anim.slide_out_left
                popEnter = R.anim.slide_in_left
                popExit = R.anim.slide_out_right
            }
        }
        binding.btnContinue?.setOnClickListener {
            val action = BoardingFragmentDirections.actionBoardingFragmentToSignUpFragment()
            findNavController().navigate(action, options)
        }
    }
}