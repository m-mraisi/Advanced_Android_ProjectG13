package com.G13.group.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.G13.group.databinding.FragmentSplashBinding
import com.G13.group.repository.DataSource

class SplashFragment : Fragment() {

    val TAG: String = "SPLASH-FRAGMENT"
    private var _binding: FragmentSplashBinding? = null
    private val binding get() = _binding!!
    private lateinit var dataSource: DataSource

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSplashBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dataSource = DataSource.getInstance()

        (activity as AppCompatActivity).supportActionBar?.hide()

        val prefs = requireContext().getSharedPreferences(
            requireContext().toString(),
            AppCompatActivity.MODE_PRIVATE
        )

        val isFirstTime = prefs.getBoolean("IS_FIRST_TIME", true)
        val username = prefs.getString("USER_USERNAME", "")

        if (isFirstTime) {
            val goToOnboardingFragment =
                SplashFragmentDirections.actionSplashFragmentToBoardingFragment()
            findNavController().navigate(goToOnboardingFragment)
            with(prefs.edit()) {
                this.putBoolean("IS_FIRST_TIME", false)
                apply()
            }
        } else if (username != "") {
            dataSource.username = username!!
            val goToFeedFragment = SplashFragmentDirections.actionSplashFragmentToFeedFragment()
            findNavController().navigate(goToFeedFragment)
        } else {
            val goToLoginFragment = SplashFragmentDirections.actionSplashFragmentToLoginFragment()
            findNavController().navigate(goToLoginFragment)
        }
        activity?.supportFragmentManager?.popBackStack()

    }
}