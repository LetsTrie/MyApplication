package com.sakib23.myapplication;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class AdapterRecyclerViewClass extends RecyclerView.Adapter<AdapterRecyclerViewClass.MyViewHolder> {

    public Context context;
    public ArrayList<UserPost> info;
    private String vis;

    public AdapterRecyclerViewClass(Context context, ArrayList<UserPost> info) {
        this.context = context;
        this.info = info;
        this.vis = "";
    }

    public AdapterRecyclerViewClass(Context context, ArrayList<UserPost> info, String vis) {
        this.context = context;
        this.info = info;
        this.vis = vis;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.sample_post, viewGroup, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int i) {

        String showDescription = "", realDescription = info.get(i).getMyPostDes();

        for (int idx = 0; idx < Math.min(200, realDescription.length()); idx++)
            showDescription += realDescription.charAt(idx);

        if(realDescription.length()>200)showDescription+="...";



        myViewHolder.myPostTitle.setText(info.get(i).getMyPostTitle());
        myViewHolder.myPostDes.setText(showDescription);
        myViewHolder.myPostTime.setText(info.get(i).getMyPostTime());
        myViewHolder.myAuthorName.setText(info.get(i).getMyUserName());


        StorageReference userProfileImageRef = FirebaseStorage.getInstance().getReference("profilePictures/" + info.get(i).getMyUserID() + ".jpg");
        Log.d("Heyyy", " " + userProfileImageRef);
        GlideApp.with(context)
            .load(userProfileImageRef)
            .into(myViewHolder.myAuthorProPic);
    }
    @Override
    public int getItemCount() {
        return info.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        public TextView myAuthorName, myPostDes, myPostTime, myPostTitle;
        public ImageView myAuthorProPic;

        public MyViewHolder(@NonNull final View itemView) {
            super(itemView);

            myPostTime = itemView.findViewById(R.id.sampleTimeID);
            myAuthorName = itemView.findViewById(R.id.sampleUserNameID);
            myPostDes = itemView.findViewById(R.id.sampleDesID);
            myAuthorProPic = itemView.findViewById(R.id.samplePropicID);
            myPostTitle = itemView.findViewById(R.id.sampleTitleID);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, PostDetails.class);

                    UserPost itemClicked = info.get(getAdapterPosition());

                    if (vis.equals("Forum"))
                        intent.putExtra("vis", "Forum");
                    else
                        intent.putExtra("vis", "Homepage");



                    intent.putExtra("authorID"      , itemClicked.getMyUserID());
                    intent.putExtra("postID"        , itemClicked.getMyPostID());
                    intent.putExtra("postURL"       , itemClicked.getMyPostURL());
                    intent.putExtra("postExt"       , itemClicked.getMyPostExt());
                    intent.putExtra("authorName"    , itemClicked.getMyUserName());
                    intent.putExtra("postTitle"     , itemClicked.getMyPostTitle());
                    intent.putExtra("postDes"       , itemClicked.getMyPostDes());
                    intent.putExtra("postTime"      , itemClicked.getMyPostTime());

                    context.startActivity(intent);
                }
            });
        }
    }
}