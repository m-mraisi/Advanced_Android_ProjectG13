package com.G13.group.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.G13.group.databinding.FragmentUploadPostBinding
import com.G13.group.repository.UploadPostRepo
import kotlinx.coroutines.launch

class UploadPostFragment : Fragment() {

    val TAG: String = "UPLOAD-POST-FRAGMENT"
    private var _binding: FragmentUploadPostBinding? = null
    private val binding get() = _binding!!

    private lateinit var uploadPostRepo: UploadPostRepo


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

        binding.btnUploadImage.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, 100)
        }

        binding.btnUploadPost.setOnClickListener {
            val username = "om"
            val caption = binding.edtCaption.text.toString()
            lifecycleScope.launch {
                uploadPostRepo.uploadPost(caption, username)
            }

        }

    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        uploadPostRepo.uploadImage(requestCode, resultCode, data, context)
    }
}