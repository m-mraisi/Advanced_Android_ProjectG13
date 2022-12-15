package com.G13.group.fragments

import android.content.Intent
import android.os.Bundle
import android.transition.TransitionInflater
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.G13.group.R
import com.G13.group.databinding.FragmentUploadPostBinding
import com.G13.group.repository.DataSource
import com.G13.group.repository.UploadPostRepo
import com.google.android.material.bottomnavigation.BottomNavigationItemView
import kotlinx.coroutines.launch

class UploadPostFragment : Fragment() {

    val TAG: String = "UPLOAD-POST-FRAGMENT"
    private var _binding: FragmentUploadPostBinding? = null
    private val binding get() = _binding!!
    private lateinit var dataSource: DataSource
    private lateinit var uploadPostRepo: UploadPostRepo

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val inflater = TransitionInflater.from(requireContext())
        enterTransition = inflater.inflateTransition(R.transition.slide)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentUploadPostBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        uploadPostRepo = UploadPostRepo()
        dataSource = DataSource.getInstance()

        binding.btnUploadImage.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, 100)
        }

        binding.btnUploadPost.setOnClickListener {
            val username = dataSource.username
            val caption = binding.edtCaption.text.toString()
            lifecycleScope.launch {
                val postingResult = uploadPostRepo.uploadPost(caption, username)
                if (postingResult) {
                    Toast.makeText(
                        requireContext(),
                        "Successfully added the post",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    Toast.makeText(
                        requireContext(),
                        "Failed to add the post",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                val bottomProfileBtn =
                    activity?.findViewById<BottomNavigationItemView>(R.id.feedFragment)
                bottomProfileBtn?.isSoundEffectsEnabled = false
                bottomProfileBtn?.performClick()
                bottomProfileBtn?.isSoundEffectsEnabled = true
            }

        }

    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        uploadPostRepo.uploadImage(requestCode, resultCode, data, context)
    }
}