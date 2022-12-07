package com.G13.group.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.G13.group.databinding.PostItemBinding
import com.G13.group.interfaces.IOnPostsListener
import com.G13.group.models.Post
import com.squareup.picasso.Picasso

class PostsAdapter(
    private val context: Context,
    private var postsList: ArrayList<Post>,
    private val clickListener: IOnPostsListener
) : RecyclerView.Adapter<PostsAdapter.PostViewHolder>() {

    internal val TAG = "PostsAdapter"

    class PostViewHolder(val binding: PostItemBinding) : RecyclerView.ViewHolder(binding.root) {
        internal val TAG = "PostsAdapter"

        @SuppressLint("SetTextI18n")
        fun bind(currentItem: Post, clickListener: IOnPostsListener) {
            // associate individual view with data
            binding.tvUsername.text = currentItem.username
            binding.tvCaption.text = currentItem.caption
            binding.tvComments.text = "View all ${currentItem.comments.size} comments"
            Picasso.get().load(currentItem.imageId).into(binding.imgPost)


            // use this approach to use the object and perform the data here in the bind() function
            binding.tvComments.setOnClickListener {
                Log.d(TAG, "bind: ${currentItem.username} selected")
                clickListener.onCommentsClickListener(currentItem)
            }

            // use this approach to send the object to MainActivity and perform the operation there
//            binding.tvComments.setOnClickListener {
//                clickListener.onFruitClicked(currentItem)
//                Log.d("FruitViewHolder", "bind: ${currentItem.title} selected")
//            }
        }
    }

    // create the view
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostsAdapter.PostViewHolder {
        return PostsAdapter.PostViewHolder(
            PostItemBinding.inflate(
                LayoutInflater.from(context),
                parent,
                false
            )
        )
    }

    // binds the data with view
    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        Log.d(TAG, "working on: ${postsList[position].username}")
        holder.bind(postsList[position], clickListener)
    }

    // identifies the number of items/elements that we will display
    override fun getItemCount(): Int {
        return postsList.size
    }
}