package com.sakib23.myapplication;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class SignupActivity extends AppCompatActivity implements View.OnClickListener {
    private static final int PROFILE_IMAGE = 100;
    private EditText firstname, lastname, emailadd, password_val, confirmpassword;
    private Button signupButton, propicUpButton;
    private FirebaseAuth mAuth;
    private ProgressBar progressBarSignup;
    private DatabaseReference userInfoDatabase;
    private Uri uriProfileImage;
    private String profileImageUrl;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        firstname = (EditText)  findViewById(R.id.firstNameID);
        lastname = (EditText)  findViewById(R.id.lastNameID);
        emailadd =(EditText) findViewById(R.id.emailID);
        password_val =(EditText) findViewById(R.id.passwordID);
        confirmpassword = (EditText)  findViewById(R.id.confirmPasswordId);
        signupButton =(Button) findViewById(R.id.SignUPbutton);
        mAuth = FirebaseAuth.getInstance();
        progressBarSignup =(ProgressBar) findViewById(R.id.progressBarSignupId);
        propicUpButton = findViewById(R.id.uploadProPicbutton);

        propicUpButton.setOnClickListener(this);
        signupButton.setOnClickListener(this);
    }
    private void registerUser()
    {
        Log.d("Hello", "registerUser: ");
        final String MyFirstName = firstname.getText().toString().trim();
        final String MyLastName = lastname.getText().toString().trim();
        final String email = emailadd.getText().toString().trim();//trim removes leaging and trailing zeros
        final String password = password_val.getText().toString().trim();
        String Confirm_pass = confirmpassword.getText().toString().trim();

        if(MyFirstName.isEmpty())
        {
            firstname.setError("First Name is required");
            firstname.requestFocus();
            return;
        }
        if(MyLastName.isEmpty())
        {
            lastname.setError("Last Name is required");
            lastname.requestFocus();
            return;
        }

        if(email.isEmpty())
        {
            emailadd.setError("Email is required");
            emailadd.requestFocus();
            return;
        }
        if(password.isEmpty()) {
            password_val.setError("Password is required");
            password_val.requestFocus();
            return;
        }
        if(Confirm_pass.isEmpty()) {
            confirmpassword.setError("Password is required");
            confirmpassword.requestFocus();
            return;
        }

        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches())
        {
            emailadd.setError("Please enter a valid email ");
            emailadd.requestFocus();
            return;
        }
        if(password.length()<6)
        {
            password_val.setError("Minimum password length should be 6");
            password_val.requestFocus();
            return;
        }
        if( !password.equals( Confirm_pass) ){
            confirmpassword.setError("Passwords are not Matching");
            confirmpassword.requestFocus();
            return;
        }
        progressBarSignup.setVisibility(View.VISIBLE);
        userInfoDatabase = FirebaseDatabase.getInstance().getReference("USER");

        mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){

                    String Randomkey = userInfoDatabase.push().getKey();
                    String timeStamp = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(Calendar.getInstance().getTime());
                    userInfoDatabase.child(Randomkey).setValue(new userLoginData(mAuth.getUid().toString(), MyFirstName, MyLastName, email, timeStamp));

                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                    UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                            .setDisplayName(MyFirstName + " " + MyLastName)
                            .build();

                    user.updateProfile(profileUpdates)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        try {
                                            uploadImageToFirebaseStorage();
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }
                            });

                    //final variable hole niche amon dag ashe.....
                }
                else if(task.getException() instanceof FirebaseAuthUserCollisionException) {
                    Toast.makeText(getApplicationContext(), "This email is already registered", Toast.LENGTH_LONG).show();
                }
                else {
                    Toast.makeText(getApplicationContext(),"Unexpected error occured",Toast.LENGTH_LONG).show();
                }
            }
        });
    }
    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.uploadProPicbutton) showImageChooser();
        if(v.getId()==R.id.SignUPbutton) registerUser();
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
        }
    }

    private void uploadImageToFirebaseStorage() {

        StorageReference userProfileImageRef = FirebaseStorage.getInstance().getReference("profilePictures/" + FirebaseAuth.getInstance().getCurrentUser().getUid() + ".jpg");

        if (uriProfileImage != null) {
            progressBarSignup.setVisibility(View.VISIBLE);

        userProfileImageRef.putFile(uriProfileImage)
            .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
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
                                            progressBarSignup.setVisibility(View.GONE);
                                            if (task.isSuccessful()) {
                                                        startActivity(new Intent(SignupActivity.this,Homepage.class));
                                                        Toast.makeText(getApplicationContext(),"Signup completed",Toast.LENGTH_LONG).show();
                                            } else {
                                                Toast.makeText(SignupActivity.this, "Some error occured", Toast.LENGTH_LONG).show();
                                            }

                                        }
                                    });
                                }
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                progressBarSignup.setVisibility(View.GONE);
                                Toast.makeText(SignupActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                            }
                        });
                }
            })
            .addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progressBarSignup.setVisibility(View.GONE);
                    Toast.makeText(SignupActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
        }
    }
}
