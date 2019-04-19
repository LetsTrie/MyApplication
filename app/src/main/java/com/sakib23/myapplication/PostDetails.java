package com.sakib23.myapplication;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import pub.devrel.easypermissions.EasyPermissions;

public class PostDetails extends AppCompatActivity implements EasyPermissions.PermissionCallbacks {

    private TextView PostTitle, PostTime, Postdetails, postDetailsUserName, editPost,deletePost;
    private static final int WRITE_REQUEST_CODE = 300;
    private String TitleName, TimeName, DescriptionName, downloadFileUrl, ext, userUid, userName, vis, postID;
    private Button downloadButton;
    private ImageView postUserPic;
    private ImageButton like;
    private TextView likeCount;
    private EditText Comment;
    private Button PostComment;
    private DatabaseReference userDatabaseRef, commentRef,postRef;
    private FirebaseUser user;
    private int cntLike;
    private boolean isLiked;

    private RecyclerView commentRecyclerView;
    private RecyclerView.Adapter commentRecyclerViewAdapter;
    private RecyclerView.LayoutManager commentLayoutManager;


    ArrayList<infoOfComment> Allcomments;
    private String commentPostID;
    private TextView CommentTextView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_details);

        TitleName = getIntent().getStringExtra("postTitle");//
        TimeName = getIntent().getStringExtra("postTime"); //
        DescriptionName = getIntent().getStringExtra("postDes");//
        downloadFileUrl = getIntent().getStringExtra("postURL"); //
        ext = getIntent().getStringExtra("postExt"); //
        userUid = getIntent().getStringExtra("authorID"); //
        userName = getIntent().getStringExtra("authorName"); //
        postID = getIntent().getStringExtra("postID"); //
        vis = getIntent().getStringExtra("vis"); //

        CommentTextView = findViewById(R.id.commentTextViewID);
        PostTitle = findViewById(R.id.ProjectTitleID);
        PostTime = findViewById(R.id.TimeID);
        Postdetails = findViewById(R.id.InformationID);
        downloadButton = findViewById(R.id.downloadButtonId);
        postUserPic = findViewById(R.id.postDetailsProPicID);
        postDetailsUserName = findViewById(R.id.postDetaisTextViewUserID);
        editPost = findViewById(R.id.postDetailsEditPostID);
        deletePost =findViewById(R.id.postDetailsDeletePostID);
        like = findViewById(R.id.LikeID);
        likeCount = findViewById(R.id.likeCountId);

        Comment = findViewById(R.id.CommentID);
        PostComment = findViewById(R.id.PostCommentID);

        Toast.makeText(PostDetails.this, vis, Toast.LENGTH_LONG).show();
        if(vis.equals("Homepage")) {
            Comment.setVisibility(View.GONE);
            PostComment.setVisibility(View.GONE);
            CommentTextView.setVisibility(View.GONE);
        } else {
            downloadButton.setVisibility(View.GONE);
        }

        Allcomments = new ArrayList<>();

        userDatabaseRef = FirebaseDatabase.getInstance().getReference("postLikes");
        postRef = FirebaseDatabase.getInstance().getReference("userPostCollection");
        user = FirebaseAuth.getInstance().getCurrentUser();
        commentRef = FirebaseDatabase.getInstance().getReference("postComments");

        postDetailsUserName.setText(userName);
        PostTitle.setText(TitleName);
        PostTime.setText(TimeName);
        Postdetails.setText(DescriptionName);

        if (downloadFileUrl.isEmpty()) downloadButton.setText("No File ");
        else downloadButton.setText("Download File");

        if (vis.equals("edit")) {
            editPost.setVisibility(View.VISIBLE);
            deletePost.setVisibility(View.VISIBLE);
        }

        StorageReference userProfileImageRef = FirebaseStorage.getInstance().getReference("profilePictures/" + userUid + ".jpg");

        GlideApp.with(getApplicationContext()/* context */)
                .load(userProfileImageRef)
                .into(postUserPic);

        editPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), CreateNewArticle.class);

                intent.putExtra("Title", TitleName);
                intent.putExtra("Timee", TimeName);
                intent.putExtra("Description", DescriptionName);
//                intent.putExtra("Tag", TagName);
                intent.putExtra("Chosen", downloadFileUrl);
                intent.putExtra("Ext", ext);
                intent.putExtra("UID", userUid);
                intent.putExtra("Name", userName);
                intent.putExtra("PostID", postID);
                intent.putExtra("vis", "edit");
                Log.d("postt", "PostDetails " + postID + "  " + TitleName + " " + DescriptionName);
                startActivity(intent);
            }
        });

        deletePost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                postRef.child(postID).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        finish();
                        startActivity(new Intent(getApplicationContext(),MyProfile.class));
                    }
                });
            }
        });

        PostComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String timeStamp = new SimpleDateFormat("dd/MM/yyyy_HH:mm:ss").format(Calendar.getInstance().getTime());
                String UserName = user.getDisplayName();
                final String userUid = user.getUid();
                commentPostID = commentRef.child(postID).push().getKey();
                commentRef.child(postID).child(commentPostID).setValue(new infoOfComment(UserName, Comment.getText().toString(), userUid, timeStamp));
                Comment.setText("");
//                finish();
//                startActivity(getIntent());
            }
        });

        commentRecyclerView = findViewById(R.id.commentRecyclerViewId);
        commentRecyclerView.setHasFixedSize(true);
        commentLayoutManager = new LinearLayoutManager(this);
        commentRecyclerView.setLayoutManager(commentLayoutManager);
        commentRef.child(postID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Allcomments.clear();
                int cnt = 0;
                for (DataSnapshot X : dataSnapshot.getChildren()) {
                    final infoOfComment info = X.getValue(infoOfComment.class);
                    cnt++;
                    Allcomments.add(info);
                }
                Collections.reverse(Allcomments);
                Toast.makeText(PostDetails.this, "Total " + cnt + " Comment(s).", Toast.LENGTH_LONG).show();

                commentRecyclerViewAdapter = new CommentAdapter(PostDetails.this, Allcomments);
                commentRecyclerView.setAdapter(commentRecyclerViewAdapter);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        userDatabaseRef.child(postID).child("likeCount").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() == null || dataSnapshot.getValue().toString().equals("0"))
                    cntLike = 0;
                else
                    cntLike = Integer.parseInt(dataSnapshot.getValue().toString());
                likeCount.setText(String.valueOf(cntLike) + " Likes");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        userDatabaseRef.child(postID).child("likeDetails").child(user.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() == null || dataSnapshot.getValue().toString().equals("0")) {
                    like.setImageResource(R.drawable.not_liked);
                    isLiked = false;
                } else {
                    like.setImageResource(R.drawable.not_liked);
                    isLiked = true;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
//like added
        like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isLiked) {
                    cntLike--;
                    userDatabaseRef.child(postID).child("likeDetails").child(user.getUid()).setValue("0");
                    userDatabaseRef.child(postID).child("likeCount").setValue(String.valueOf(cntLike));
                } else {
                    cntLike++;
                    userDatabaseRef.child(postID).child("likeDetails").child(user.getUid()).setValue("1");
                    userDatabaseRef.child(postID).child("likeCount").setValue(String.valueOf(cntLike));
                }
            }
        });


        downloadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Check if SD card is present or not
                if (CheckForSDCard.isSDCardPresent()) {

                    //check if app has permission to write to the external storage.
                    if (EasyPermissions.hasPermissions(PostDetails.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                        //Get the URL entered
                        new DownloadFile().execute(downloadFileUrl);

                    } else {
                        //If permission is not present request for the same.
                        EasyPermissions.requestPermissions(PostDetails.this, getString(R.string.write_file), WRITE_REQUEST_CODE, Manifest.permission.READ_EXTERNAL_STORAGE);
                    }


                } else {
                    Toast.makeText(getApplicationContext(), "SD Card not found", Toast.LENGTH_LONG).show();

                }
            }
        });

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, PostDetails.this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {
        //Download the file once permission is granted
        new DownloadFile().execute(downloadFileUrl);
    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
    }

    /**
     * Async Task to download file from URL
     */
    private class DownloadFile extends AsyncTask<String, String, String> {

        private ProgressDialog progressDialog;
        private String fileName;
        private String folder;
        private boolean isDownloaded;

        /**
         * Before starting background thread
         * Show Progress Bar Dialog
         */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            this.progressDialog = new ProgressDialog(PostDetails.this);
            this.progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            this.progressDialog.setCancelable(false);
            this.progressDialog.show();
        }

        /**
         * Downloading file in background thread
         */
        @Override
        protected String doInBackground(String... f_url) {
            int count;
            try {
                URL url = new URL(f_url[0]);
                URLConnection connection = url.openConnection();
                connection.connect();
                // getting file length
                int lengthOfFile = connection.getContentLength();


                // input stream to read file - with 8k buffer
                InputStream input = new BufferedInputStream(url.openStream(), 8192);

                String timestamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date());

                //Extract file name from URL
                fileName = f_url[0].substring(f_url[0].lastIndexOf('/') + 1, f_url[0].length());

                //Append timestamp to file name
                fileName = timestamp + "_" + fileName + "." + ext;

                //External directory path to save file
                folder = Environment.getExternalStorageDirectory() + File.separator + "Medicorum/";

                //Create androiddeft folder if it does not exist
                File directory = new File(folder);

                if (!directory.exists()) {
                    directory.mkdirs();
                }

                // Output stream to write file
                OutputStream output = new FileOutputStream(folder + fileName);

                byte data[] = new byte[1024];

                long total = 0;

                while ((count = input.read(data)) != -1) {
                    total += count;
                    // publishing the progress....
                    // After this onProgressUpdate will be called
                    publishProgress("" + (int) ((total * 100) / lengthOfFile));

                    // writing data to file
                    output.write(data, 0, count);
                }

                // flushing output
                output.flush();

                // closing streams
                output.close();
                input.close();
                Toast.makeText(PostDetails.this, " " + folder + " " + fileName, Toast.LENGTH_LONG).show();
                return "Downloaded at: " + folder + fileName;

            } catch (Exception e) {
                Log.e("Error: ", e.getMessage());
            }

            return "Something went wrong";
        }

        /**
         * Updating progress bar
         */
        protected void onProgressUpdate(String... progress) {
            // setting progress percentage
            progressDialog.setProgress(Integer.parseInt(progress[0]));
        }


        @Override
        protected void onPostExecute(String message) {
            // dismiss the dialog after the file was downloaded
            this.progressDialog.dismiss();

            // Display File path after downloading
            Toast.makeText(getApplicationContext(),
                    message, Toast.LENGTH_LONG).show();
        }
    }
}