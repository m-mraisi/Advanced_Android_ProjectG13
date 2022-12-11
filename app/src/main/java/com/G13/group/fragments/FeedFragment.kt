package com.G13.group.fragments

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.navOptions
import androidx.recyclerview.widget.LinearLayoutManager
import com.G13.group.R
import com.G13.group.adapters.PostsAdapter
import com.G13.group.databinding.AddCommentDialogBinding
import com.G13.group.databinding.FragmentFeedBinding
import com.G13.group.interfaces.IOnPostsListener
import com.G13.group.models.Comment
import com.G13.group.models.Post
import com.G13.group.repository.CommentsRepo
import com.G13.group.repository.DataSource
import com.G13.group.repository.PostsRepo
import kotlinx.coroutines.launch


class FeedFragment : Fragment(), IOnPostsListener {
    val TAG: String = "FEED-FRAGMENT"
    private var _binding: FragmentFeedBinding? = null
    private val binding get() = _binding!!
    private lateinit var postsRepo: PostsRepo
    var postAdapter: PostsAdapter? = null
    lateinit var postArrayList: ArrayList<Post>
    lateinit var dataSource: DataSource

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentFeedBinding.inflate(inflater, container, false)
        val view = binding.root

        dataSource = DataSource.getInstance()

        postArrayList = dataSource.dataSourcePostsArrayList
        Log.d(TAG, "postArrayList size at first: ${postArrayList.size}")

        // Create the adapter to convert the array to views
        postAdapter = PostsAdapter(requireContext(), postArrayList, this)
        binding.rvPosts.layoutManager = LinearLayoutManager(requireContext())
        binding.rvPosts.adapter = postAdapter

        return view
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onResume() {
        super.onResume()

        postArrayList.clear()
        postsRepo = PostsRepo(this)
        postsRepo.syncPosts()


        Log.d(TAG, "feeding posts")
        binding.rvPosts.setOnScrollChangeListener { view, i, i2, i3, i4 ->
            val layoutManager = binding.rvPosts.getLayoutManager() as LinearLayoutManager
            var currentShowingPost = layoutManager.findLastVisibleItemPosition()
            dataSource.currentShowingPost = currentShowingPost
            Log.d(TAG, "setOnScrollChangeListener: ${dataSource.currentShowingPost}")
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    override fun onCommentsClickListener(post: Post) {
        Log.d(TAG, "item clicked ${post.username}")
        if (post.comments.size == 0) {
            val commentsRepo = CommentsRepo()

            viewLifecycleOwner.lifecycleScope.launch {
                // add comment
                val dialogBinding = AddCommentDialogBinding.inflate(layoutInflater)
                val addDialog = AlertDialog.Builder(requireContext())
                    .setView(dialogBinding.root)
                    .setPositiveButton("Add") { dialog, which ->
                        val comment = dialogBinding.edtComment.text.toString().trim()
                        if (comment.isBlank()) {
                            Toast.makeText(
                                requireContext(),
                                "No comment added",
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            viewLifecycleOwner.lifecycleScope.launch {
                                postArrayList[postArrayList.indexOf(post)].comments.add(
                                    Comment(
                                        comment = comment,
                                        username = dataSource.username
                                    )
                                )
                                commentsRepo.addComment(postArrayList[postArrayList.indexOf(post)])
                                Toast.makeText(
                                    requireContext(),
                                    "Comment added successfully",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    }
                    .setNegativeButton("Cancel", null)
                    .create()
                addDialog.show()
            }
            return
        } else {
            val options = navOptions {
                anim {
                    enter = R.anim.slide_in_right
                    exit = R.anim.slide_out_left
                    popEnter = R.anim.slide_in_left
                    popExit = R.anim.slide_out_right
                }
            }

            val action = FeedFragmentDirections.actionFeedFragmentToCommentsFragment(post)
            findNavController().navigate(action, options)
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun postsDataChangeListener() {
        Log.d(TAG, "Notifying the adapter: ${dataSource.dataSourcePostsArrayList.size}")
        postArrayList = dataSource.dataSourcePostsArrayList
        Log.d(TAG, "postArrayList size: ${postArrayList.size}")
        postAdapter?.notifyDataSetChanged()
//        binding.rvPosts.smoothScrollToPosition(dataSource.currentShowingPost)
    }

    override fun onDeletePostListener(post: Post) {
        val addDialog = AlertDialog.Builder(requireContext())
            .setTitle("Are you sure you want to delete your post?")
            .setPositiveButton("Delete") { dialog, which ->
                viewLifecycleOwner.lifecycleScope.launch {
                    var isdeleted = postsRepo.deletePost(post)
                    if (isdeleted) {
                        Toast.makeText(
                            requireContext(),
                            "Successfully deleted",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        Toast.makeText(
                            requireContext(),
                            "Failed to delete the post",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
            .setNegativeButton("Cancel", null)
            .create()
        addDialog.show()
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