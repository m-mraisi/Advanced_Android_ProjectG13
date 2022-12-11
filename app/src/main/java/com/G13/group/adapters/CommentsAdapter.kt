package com.G13.group.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.G13.group.databinding.CommentItemBinding
import com.G13.group.interfaces.IOnCommentListener
import com.G13.group.models.Comment
import com.G13.group.repository.DataSource

class CommentsAdapter(
    private val context: Context,
    private var commentsList: ArrayList<Comment>,
    private val commentsListener: IOnCommentListener
) : RecyclerView.Adapter<CommentsAdapter.CommentViewHolder>() {
    internal val TAG = "CommentsAdapter"

    class CommentViewHolder(val binding: CommentItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        internal val TAG = "CommentsAdapter"

        val dataSource = DataSource.getInstance()

        @SuppressLint("SetTextI18n")
        fun bind(currentItem: Comment, commentsListener: IOnCommentListener) {
            // associate individual view with data

            val currentUser = dataSource.username

            Log.d(TAG, "bind: ${currentItem.comment} selected")
            binding.tvUsername.text = currentItem.username
            binding.tvComment.text = currentItem.comment


            if (currentUser == currentItem.username) {
                binding.tvDeleteComment.visibility = View.VISIBLE
            } else {
                binding.tvDeleteComment.visibility = View.GONE
            }

            binding.tvDeleteComment.setOnClickListener {
                commentsListener.onDeleteCommentListener(currentItem)
            }


        }
    }

    // create the view
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CommentsAdapter.CommentViewHolder {
        return CommentsAdapter.CommentViewHolder(
            CommentItemBinding.inflate(
                LayoutInflater.from(context),
                parent,
                false
            )
        )
    }

    // binds the data with view
    override fun onBindViewHolder(holder: CommentsAdapter.CommentViewHolder, position: Int) {
        Log.d(TAG, "working on: ${commentsList[position].username}")
        holder.bind(commentsList[position], commentsListener)
    }

    // identifies the number of items/elements that we will display
    override fun getItemCount(): Int {
        Log.d(TAG, "bind: side: ${commentsList.size}")
        return commentsList.size
    }
}