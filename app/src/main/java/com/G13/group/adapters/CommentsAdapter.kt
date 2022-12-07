package com.G13.group.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.G13.group.databinding.CommentItemBinding
import com.G13.group.models.Comment

class CommentsAdapter(
    private val context: Context,
    private var commentsList: ArrayList<Comment>
) : RecyclerView.Adapter<CommentsAdapter.CommentViewHolder>() {
    internal val TAG = "CommentsAdapter"

    class CommentViewHolder(val binding: CommentItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        internal val TAG = "CommentsAdapter"

        @SuppressLint("SetTextI18n")
        fun bind(currentItem: Comment) {
            // associate individual view with data
            Log.d(TAG, "bind: ${currentItem.comment} selected")
            binding.tvUsername.text = currentItem.username
            binding.tvComment.text = currentItem.comment
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
        holder.bind(commentsList[position])
    }

    // identifies the number of items/elements that we will display
    override fun getItemCount(): Int {
        Log.d(TAG, "bind: side: ${commentsList.size}")
        return commentsList.size
    }
}