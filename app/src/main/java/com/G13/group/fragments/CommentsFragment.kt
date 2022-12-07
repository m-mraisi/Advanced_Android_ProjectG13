package com.G13.group.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.G13.group.adapters.CommentsAdapter
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
        commentsRepo = CommentsRepo()

        commentsArrayList = arrayListOf<Comment>()

        commentAdapter = CommentsAdapter(view.context, commentsArrayList, this)
        binding.rvComments.layoutManager = LinearLayoutManager(view.context)
        binding.rvComments.adapter = commentAdapter

        binding.btnAddComment.setOnClickListener {
            addComment()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onResume() {
        super.onResume()

        commentsArrayList.clear()

        val postArrayList = dataSource.dataSourcePostsArrayList
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

    }

    private fun addComment() {
        // add comment
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun commentsChangeListener() {
        val postArrayList = dataSource.dataSourcePostsArrayList
        Log.d(TAG, "added a comment: $postArrayList")
        for (post in postArrayList) {
            if (post.id == selectedPost.id) {
                Log.d(TAG, "found it : ${post.comments}")
                commentsArrayList = post.comments
            }
        }
        commentAdapter?.notifyDataSetChanged()
    }

}