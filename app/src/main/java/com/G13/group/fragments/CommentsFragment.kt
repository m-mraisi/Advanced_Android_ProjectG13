package com.G13.group.fragments


import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.navOptions
import androidx.recyclerview.widget.LinearLayoutManager
import com.G13.group.R
import com.G13.group.adapters.CommentsAdapter
import com.G13.group.databinding.AddCommentDialogBinding
import com.G13.group.databinding.FragmentCommentsBinding
import com.G13.group.interfaces.IOnCommentListener
import com.G13.group.models.Comment
import com.G13.group.models.Post
import com.G13.group.repository.CommentsRepo
import com.G13.group.repository.DataSource
import kotlinx.coroutines.launch


class CommentsFragment : Fragment(), IOnCommentListener {
    val TAG: String = "COMMENTS-FRAGMENT"
    private var _binding: FragmentCommentsBinding? = null
    private val binding get() = _binding!!
    private lateinit var commentsArrayList: ArrayList<Comment>
    var commentAdapter: CommentsAdapter? = null
    private var selectedPost: Post = Post()
    lateinit var dataSource: DataSource
    lateinit var commentsRepo: CommentsRepo

    // class properties for
    private val args: CommentsFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCommentsBinding.inflate(inflater, container, false)
        val view = binding.root


        if (args.post != null) {
            selectedPost = args.post!!
        }

        return view
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Create the adapter to convert the array to views
        Log.d(TAG, "onViewCreated started")

        dataSource = DataSource.getInstance()

        commentsArrayList = arrayListOf<Comment>()

        commentAdapter = CommentsAdapter(view.context, commentsArrayList, this)
        binding.rvComments.layoutManager = LinearLayoutManager(view.context)
        binding.rvComments.adapter = commentAdapter

    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onResume() {
        super.onResume()

        commentsRepo = CommentsRepo()
        commentsArrayList.clear()


        viewLifecycleOwner.lifecycleScope.launch {
            commentsRepo.getPostComments(selectedPost)
            commentsRepo.allComments.observe(requireActivity()) { comments ->
                if (comments.isNotEmpty()) {
                    Log.d(TAG, "comments are note empty : $comments")
                    comments.forEach { comment ->
                        commentsArrayList.add(comment)
                    }
                }

                commentAdapter?.notifyDataSetChanged()
            }
        }


        binding.btnAddComment.setOnClickListener {
            addComment()
        }

    }

    @SuppressLint("NotifyDataSetChanged")
    private fun addComment() {
        // add comment
        val dialogBinding = AddCommentDialogBinding.inflate(layoutInflater)
        val addDialog = AlertDialog.Builder(requireContext())
            .setView(dialogBinding.root)
            .setPositiveButton("Add") { dialog, which ->
                val comment = dialogBinding.edtComment.text.toString().trim()
                if (comment.isBlank()) {
                    dialogBinding.edtComment.error = "Please insert a comment to add"
                    return@setPositiveButton
                } else {
                    commentsArrayList.add(Comment(comment, dataSource.username))
                    selectedPost.comments = commentsArrayList
                    viewLifecycleOwner.lifecycleScope.launch {
                        commentsRepo.addComment(selectedPost)
                    }
                    commentAdapter?.notifyDataSetChanged()
                }
            }
            .setNegativeButton("Cancel", null)
            .create()
        addDialog.show()
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onDeleteCommentListener(comment: Comment) {
        val addDialog = AlertDialog.Builder(requireContext())
            .setTitle("Are you sure you want to delete your comment?")
            .setPositiveButton("Delete") { dialog, which ->
                viewLifecycleOwner.lifecycleScope.launch {
                    Log.d(TAG, "index: ${commentsArrayList.indexOf(comment)}")
                    val isDeleted = commentsRepo.deleteComment(
                        selectedPost.id,
                        commentsArrayList,
                        commentsArrayList.indexOf(comment)
                    )
                    if (isDeleted) {
                        Log.d(TAG, "index: ${commentsArrayList.indexOf(comment)}")
                        commentAdapter?.notifyDataSetChanged()
                        if (commentsArrayList.size == 0) {

                            val options = navOptions {
                                anim {
                                    enter = R.anim.slide_in_left
                                    exit = R.anim.slide_out_right
                                    popEnter = R.anim.slide_in_right
                                    popExit = R.anim.slide_out_left
                                }
                            }

                            val previousFragmentName = dataSource.lastFragmentName

                            Log.d(TAG, "current_destination: dataSource ${previousFragmentName}")

                            if (previousFragmentName == "PROFILE-FRAGMENT") {
                                val action =
                                    CommentsFragmentDirections.actionCommentsFragmentToProfileFragment()
                                findNavController().navigate(action, options)
                            } else {
                                val action =
                                    CommentsFragmentDirections.actionCommentsFragmentToFeedFragment()
                                findNavController().navigate(action, options)
                            }
                        }
                        Toast.makeText(
                            requireContext(),
                            "Successfully deleted",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        Toast.makeText(
                            requireContext(),
                            "Failed to delete the comment",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
            .setNegativeButton("Cancel", null)
            .create()
        addDialog.show()
    }

}