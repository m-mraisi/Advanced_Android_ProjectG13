package com.G13.group

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.navOptions
import androidx.navigation.ui.AppBarConfiguration
import com.G13.group.databinding.ActivityMainBinding
import com.G13.group.fragments.FeedFragmentDirections
import com.G13.group.fragments.ProfileFragmentDirections
import com.G13.group.repository.DataSource


class MainActivity : AppCompatActivity() {
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var navController: NavController
    lateinit var binding: ActivityMainBinding
    lateinit var dataSource: DataSource
    val TAG = "MAIN_ACTIVITY"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        dataSource = DataSource.getInstance()

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.fragment_container) as NavHostFragment
        navController = navHostFragment.navController

        val feedOptions = navOptions {
            anim {
                popEnter = R.anim.slide_in_right
                popExit = R.anim.slide_out_left
                enter = R.anim.slide_in_left
                exit = R.anim.slide_out_right
            }
        }
        val profileOptions = navOptions {
            anim {
                enter = R.anim.slide_in_right
                exit = R.anim.slide_out_left
                popEnter = R.anim.slide_in_left
                popExit = R.anim.slide_out_right
            }
        }

        binding.bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.feedFragment -> {
                    navController.navigate(R.id.feedFragment, null, feedOptions)
                }
                R.id.profileFragment -> {
                    navController.navigate(R.id.profileFragment, null, profileOptions)
                }
                else -> {
                    hideBottomNav()
                }
            }
            true
        }

        binding.bottomNavigationView.setOnNavigationItemReselectedListener { item -> return@setOnNavigationItemReselectedListener }


//        setupWithNavController(binding.bottomNavigationView, navController)
//
//
        navController.addOnDestinationChangedListener { _, destination, _ ->
            Log.d(TAG, "current_destination: profile ${destination.id == R.id.profileFragment}")
            Log.d(TAG, "current_destination: feed ${destination.id == R.id.feedFragment}")
            when (destination.id) {
                R.id.splashFragment -> this.supportActionBar?.hide()
                R.id.profileFragment -> {
                    showBottomNav()
                    dataSource.lastFragmentName = "PROFILE-FRAGMENT"
                }
                R.id.feedFragment -> {
                    showBottomNav()
                    dataSource.lastFragmentName = "FEED-FRAGMENT"
                }
                else -> hideBottomNav()
            }
            Log.d(TAG, "current_destination: dataSource ${dataSource.lastFragmentName}")
        }

    }

    override fun onStart() {
        super.onStart()
        binding.fabUploadPost.setOnClickListener {
            val options = navOptions {
                anim {
                    enter = R.anim.slide_in_right
                    exit = R.anim.slide_out_left
                    popEnter = R.anim.slide_in_left
                    popExit = R.anim.slide_out_right
                }
            }
            if (dataSource.lastFragmentName == "PROFILE-FRAGMENT") {
                val action = ProfileFragmentDirections.actionProfileFragmentToUploadPostFragment()
                navController.navigate(action)
            } else {
                val action = FeedFragmentDirections.actionFeedFragmentToUploadFragment()
                navController.navigate(action)
            }

        }
    }

    private fun showBottomNav() {
        binding.bottomNavigationView.visibility = View.VISIBLE
        this.supportActionBar?.show()

    }

    private fun hideBottomNav() {
        binding.bottomNavigationView.visibility = View.GONE
        this.supportActionBar?.show()

    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }
}