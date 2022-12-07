package com.G13.group.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.navOptions
import androidx.recyclerview.widget.LinearLayoutManager
import com.G13.group.R
import com.G13.group.adapters.PostsAdapter
import com.G13.group.databinding.FragmentFeedBinding
import com.G13.group.interfaces.IOnCommentListener
import com.G13.group.interfaces.IOnPostsListener
import com.G13.group.models.Post
import com.G13.group.repository.DataSource
import com.G13.group.repository.PostsRepo


class FeedFragment : Fragment(), IOnPostsListener, IOnCommentListener {
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
        return view
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dataSource = DataSource.getInstance()


        postsRepo = PostsRepo(this, this)
        postArrayList = dataSource.dataSourcePostsArrayList
        postArrayList.clear()
        postsRepo.syncPosts()

        // Create the adapter to convert the array to views
        postAdapter = PostsAdapter(requireContext(), postArrayList, this)
        binding.rvPosts.layoutManager = LinearLayoutManager(requireContext())
        binding.rvPosts.adapter = postAdapter

//        postsRepo.allPosts.observe(requireActivity(), Observer { postsList->
//
//            if(postsList != null){
//                for (post in postsList){
//                    Log.d(TAG, "New in coming data")
//                    postArrayList.add(post)
//                }
//            }
//            postAdapter?.notifyDataSetChanged()
//        })

    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onResume() {
        super.onResume()


    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    override fun onCommentsClickListener(post: Post) {
        Log.d(TAG, "item clicked ${post.username}")

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

    @SuppressLint("NotifyDataSetChanged")
    override fun postsDataChangeListener() {
        Log.d(TAG, "Notified the adapter: ${dataSource.dataSourcePostsArrayList.size}")
        postAdapter?.notifyDataSetChanged()
    }

    override fun addCommentClickListener() {
        //TODO("Not yet implemented")
    }

    override fun commentsChangeListener() {
        //TODO("Not yet implemented")
    }


}