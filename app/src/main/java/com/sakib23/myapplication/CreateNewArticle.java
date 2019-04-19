package com.sakib23.myapplication;

import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.internal.Storage;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;

public class CreateNewArticle extends AppCompatActivity {

    public static final int REQUEST_CODE_DOC=111;
    private Button Post,Upload;
    private EditText Title;
    private DatabaseReference userPostInfoDatabase;
    private EditText Description;
    private Spinner Tag;
    private Uri uriChosenFile;
    private String chosenFileUrl,ext;
    private String TitleName,TimeName,DescriptionName,downloadFileUrl,userUid,userName,vis,postID;
    private ProgressBar progressBarUploadFile;
    private FirebaseAuth mAuth;
    private FirebaseUser user;

    Bundle bundle;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_new_article);
        Title = findViewById(R.id.TitleID);
        Description = findViewById(R.id.DescriptionID);
        userPostInfoDatabase = FirebaseDatabase.getInstance().getReference("userPostCollection");
        Post = findViewById(R.id.ClickHereToPostID);
        Upload=findViewById(R.id.uploadButtonId);

        progressBarUploadFile=findViewById(R.id.progressBarUserUpleadFileId);

        TitleName = getIntent().getStringExtra("Title");
        TimeName = getIntent().getStringExtra("Timee");
        DescriptionName = getIntent().getStringExtra("Description");
        downloadFileUrl= getIntent().getStringExtra("Chosen");
        ext= getIntent().getStringExtra("Ext");
        userUid = getIntent().getStringExtra("UID");
        userName = getIntent().getStringExtra("Name");
        postID= getIntent().getStringExtra("PostID");
        vis=getIntent().getStringExtra("vis");



        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        chosenFileUrl = "";

        if(vis.equals("edit"))  uploadDataInViews();

        Post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PleasePostNow();
                finish();
                startActivity(new Intent(CreateNewArticle.this, Homepage.class));
                Toast.makeText(CreateNewArticle.this, "Successfully Uploaded", Toast.LENGTH_LONG).show();
            }
        });

        Upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFileChooser();
            }
        });
    }

    public void uploadDataInViews()
    {
        Title.setText(TitleName);
        Description.setText(DescriptionName);
        chosenFileUrl= downloadFileUrl;
    }

    private void showFileChooser() {

        String[] mimeTypes =
                {"application/msword","application/vnd.openxmlformats-officedocument.wordprocessingml.document", // .doc & .docx
                        "application/vnd.ms-powerpoint","application/vnd.openxmlformats-officedocument.presentationml.presentation", // .ppt & .pptx
                        "application/vnd.ms-excel","application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", // .xls & .xlsx
                        "text/plain",
                        "image/jpeg","image/bmp","image/gif","image/jpg","image/png",
                        "video/wav","video/mp4","video/x-flv","video/3gpp","video/quicktime","video/x-msvideo","video/x-ms-wmv",
                        "application/pdf",
                        "application/zip",
                        "application/vnd.android.package-archive"};

        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            intent.setType(mimeTypes.length == 1 ? mimeTypes[0] : "*/*");
            if (mimeTypes.length > 0) {
                intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
            }
        } else {
            String mimeTypesStr = "";
            for (String mimeType : mimeTypes) {
                mimeTypesStr += mimeType + "|";
            }
            intent.setType(mimeTypesStr.substring(0,mimeTypesStr.length() - 1));
        }
        startActivityForResult(Intent.createChooser(intent,"ChooseFile"), REQUEST_CODE_DOC);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_DOC && resultCode == RESULT_OK && data != null && data.getData() != null) {
            uriChosenFile = data.getData();
            ContentResolver cR = this.getContentResolver();
            MimeTypeMap mime = MimeTypeMap.getSingleton();
            ext = mime.getExtensionFromMimeType(cR.getType(uriChosenFile));

            //uploading data to firebase
            uploadFileToFirebaseStorage();
        }
    }


    private void uploadFileToFirebaseStorage() {

        StorageReference userProfileImageRef = FirebaseStorage.getInstance().getReference("uploadedFiles/" + user.getUid()+System.currentTimeMillis());

        if (uriChosenFile != null) {
            progressBarUploadFile.setVisibility(View.VISIBLE);

            userProfileImageRef.putFile(uriChosenFile)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            progressBarUploadFile.setVisibility(View.GONE);
                            taskSnapshot.getMetadata().getReference().getDownloadUrl()
                                    .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            chosenFileUrl = uri.toString();
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            progressBarUploadFile.setVisibility(View.GONE);
                                            Toast.makeText(CreateNewArticle.this, e.getMessage(), Toast.LENGTH_LONG).show();
                                        }
                                    });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressBarUploadFile.setVisibility(View.GONE);
                            Toast.makeText(CreateNewArticle.this, e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
        }
    }



    private void PleasePostNow() {
        TitleName = Title.getText().toString().trim();
        DescriptionName = Description.getText().toString().trim();

        if( TextUtils.isEmpty(TitleName) || TextUtils.isEmpty(DescriptionName) ) {
            Toast.makeText(CreateNewArticle.this, "Fill UP properly", Toast.LENGTH_LONG).show();
        } else {
            String timeStamp = new SimpleDateFormat("dd/MM/yyyy_HH:mm:ss").format(Calendar.getInstance().getTime());

            //check if its a new post or previous edit post
            if(vis.equals("From Homepage"))
                postID = userPostInfoDatabase.push().getKey();

            UserPost postInfo = new UserPost(user.getUid(), user.getDisplayName(), postID, TitleName, DescriptionName, timeStamp, chosenFileUrl, ext);
            userPostInfoDatabase.child(postID).setValue(postInfo);

            Toast.makeText(CreateNewArticle.this, "SuccessFully Added", Toast.LENGTH_LONG).show();
        }
    }
}