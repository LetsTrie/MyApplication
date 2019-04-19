package com.sakib23.myapplication;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.CommentViewHOlder> {
    private Context context;
    private List<infoOfComment> allComments;
    private int pos;


    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public class CommentViewHOlder extends RecyclerView.ViewHolder {

        public TextView userNameTextView,commentTextView,timeTextView;
        public ImageView sampleCommentImageView;

        public CommentViewHOlder(View itemView) {
            super(itemView);
            userNameTextView = (TextView) itemView.findViewById(R.id.whoCommentedNameID);
            commentTextView = (TextView) itemView.findViewById(R.id.CommentID);
            timeTextView = (TextView) itemView.findViewById(R.id.timeStampID);
            sampleCommentImageView = (ImageView) itemView.findViewById(R.id.whoCommentedPicID);
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public CommentAdapter(Context context, List<infoOfComment> allComments) {
        this.context = context;
        this.allComments = allComments;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public CommentAdapter.CommentViewHOlder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(context).inflate(R.layout.sample_comments, parent, false);
        return new CommentViewHOlder(v);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(CommentViewHOlder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element

        pos = position;
        final infoOfComment currentComment = allComments.get(position);



        StorageReference userProfileImageRef = FirebaseStorage.getInstance().getReference("profilePictures/" + currentComment.getUserUid() + ".jpg");

        GlideApp.with(context/* context */)
                .load(userProfileImageRef)
                .into(holder.sampleCommentImageView);

        holder.userNameTextView.setText(currentComment.getUserName());
        holder.commentTextView.setText(currentComment.getcommentDetails());
        holder.timeTextView.setText(currentComment.getTimestamp());

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return allComments.size();
    }

}