package com.sakib23.myapplication;

import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.support.annotation.NonNull;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.EventLogTags;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class MyProfile extends AppCompatActivity implements View.OnClickListener, NavigationView.OnNavigationItemSelectedListener {

    private static final int PROFILE_IMAGE = 100;
    private ImageView userImage;
    private TextView addChange;
    private TextView userName;
    private Uri uriProfileImage;
    private ProgressBar progressBarUserProfileImage;
    private String profileImageUrl;
    private FirebaseAuth mAuth;
    private FirebaseUser user;

    private RecyclerView recyclerView;
    private AdapterRecyclerViewClass myadapter;


    private DatabaseReference userDatabaseRef;
    private ArrayList<UserPost> userPost;

    ArrayList<userLoginData> uNameAndImages;

    // ****************
    //Navigation Drawer

    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private NavigationView navigationView;

    // ****************

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_profile);

        // ****************
        //Navigation Drawer
        drawerLayout = findViewById(R.id.DrawerLayoutID);
        navigationView = findViewById(R.id.NavigationViewID);
        navigationView.bringToFront();
        navigationView.setNavigationItemSelectedListener(this);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close);
        actionBarDrawerToggle.setDrawerIndicatorEnabled(true);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); //to display the navigation bar
        navigationView.setItemTextColor(ColorStateList.valueOf(getResources().getColor(R.color.Blue)));
        navigationView.setItemIconTintList(ColorStateList.valueOf(getResources().getColor(R.color.Blue)));
        navigationView.setCheckedItem(R.id.myProfileID);
        // Header Name Pics change korte hobe.....
        // ****************

        userDatabaseRef = FirebaseDatabase.getInstance().getReference("userPostCollection");
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

        userPost= new ArrayList<>();
        uNameAndImages = new ArrayList<>(); // not sure

        recyclerView = findViewById(R.id.userProfileRecyclerViewID);
        userDatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                userPost.clear();
                for (DataSnapshot X : dataSnapshot.getChildren()) {
                    if(X.getValue(UserPost.class).getMyUserID().equals(user.getUid()))
                        userPost.add( X.getValue(UserPost.class));
                }
                Collections.reverse(userPost);
                myadapter = new AdapterRecyclerViewClass(MyProfile.this, userPost);
                recyclerView.setAdapter(myadapter);
                recyclerView.setLayoutManager(new LinearLayoutManager(MyProfile.this));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}
        });

        userImage = (ImageView) findViewById(R.id.userImageViewId);
        addChange = (TextView) findViewById(R.id.addChangePhotoId);
        userName = (TextView) findViewById(R.id.userNameProfileId);
        progressBarUserProfileImage = (ProgressBar) findViewById(R.id.progressBarUserProfileImageId);

        loadUserInformation();
        addChange.setOnClickListener(this);
    }

    @Override
    public void onBackPressed() {
        finish();
        startActivity(new Intent(MyProfile.this, Homepage.class));
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mAuth.getCurrentUser() == null) {
            finish();
            startActivity(new Intent(MyProfile.this, MainActivity.class));
        }
    }

    private void loadUserInformation() {

        if (user != null) {
            if (user.getPhotoUrl() != null) {
                Glide.with(this)
                        .load(user.getPhotoUrl().toString())
                        .into(userImage);
            }
            if (user.getDisplayName() != null) {
                userName.setText("Hi, " + user.getDisplayName() + " !!");
            }
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.addChangePhotoId) showImageChooser();
    }


    private void showImageChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");

        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Profile Image"), PROFILE_IMAGE);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PROFILE_IMAGE && resultCode == RESULT_OK && data != null && data.getData() != null) {
            uriProfileImage = data.getData();

            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uriProfileImage);
                userImage.setImageBitmap(bitmap);
                uploadImageToFirebaseStorage();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void uploadImageToFirebaseStorage() {

        StorageReference userProfileImageRef = FirebaseStorage.getInstance().getReference("profilePictures/" + user.getUid() + ".jpg");

        if (uriProfileImage != null) {
            progressBarUserProfileImage.setVisibility(View.VISIBLE);


            userProfileImageRef.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    Toast.makeText(MyProfile.this, "old photo deleted", Toast.LENGTH_LONG).show();
                }
            });
            userProfileImageRef.putFile(uriProfileImage)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        progressBarUserProfileImage.setVisibility(View.GONE);
                        taskSnapshot.getMetadata().getReference().getDownloadUrl()
                            .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    profileImageUrl = uri.toString();

                                    FirebaseUser user = mAuth.getCurrentUser();

                                    if (user != null && profileImageUrl != null) {
                                        UserProfileChangeRequest profile = new UserProfileChangeRequest.Builder()
                                                .setPhotoUri(Uri.parse(profileImageUrl))
                                                .build();
                                        user.updateProfile(profile).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    Toast.makeText(MyProfile.this, "Profile Updated", Toast.LENGTH_LONG).show();
                                                } else {
                                                    Toast.makeText(MyProfile.this, "Some error occured", Toast.LENGTH_LONG).show();
                                                }

                                            }
                                        });
                                    }
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    progressBarUserProfileImage.setVisibility(View.GONE);
                                    Toast.makeText(MyProfile.this, e.getMessage(), Toast.LENGTH_LONG).show();
                                }
                            });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressBarUserProfileImage.setVisibility(View.GONE);
                        Toast.makeText(MyProfile.this, e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
        }
    }



    // Navigation Drawer...
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(actionBarDrawerToggle.onOptionsItemSelected(item)) return true;
        return super.onOptionsItemSelected(item);
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        if (menuItem.getItemId()==R.id.homepageID) {
            finish();
            Intent intent = new Intent(MyProfile.this,Homepage.class );
            startActivity(intent);
        }
        if (menuItem.getItemId()==R.id.forumID) {
            finish();
            Intent intent = new Intent(MyProfile.this,Forum.class );
            startActivity(intent);
        }
        if(menuItem.getItemId() == R.id.logoutID) {
            finish();
            Intent intent = new Intent(MyProfile.this,MainActivity.class );
            if(FirebaseAuth.getInstance().getCurrentUser() != null) FirebaseAuth.getInstance().signOut();
            startActivity(intent);
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

}
