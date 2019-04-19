package com.sakib23.myapplication;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class forum_question extends AppCompatActivity {
    private Button Post;
    private EditText Title;
    private DatabaseReference userForumQuesDatabase;
    private EditText Description;
    private FirebaseAuth mAuth;
    private FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forum_question);
        Title = findViewById(R.id.TitleID);
        Description = findViewById(R.id.DescriptionID);
        userForumQuesDatabase = FirebaseDatabase.getInstance().getReference("userQuestionCollection");
        Post = findViewById(R.id.ClickHereToPostID);
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

        Post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String TitleName = Title.getText().toString().trim();
                String DescriptionName = Description.getText().toString().trim();

                if( TextUtils.isEmpty(TitleName) || TextUtils.isEmpty(DescriptionName) ) {
                    Toast.makeText(forum_question.this, "Fill UP properly", Toast.LENGTH_LONG).show();
                } else {
                    String timeStamp = new SimpleDateFormat("dd/MM/yyyy_HH:mm:ss").format(Calendar.getInstance().getTime());
                    String postID = userForumQuesDatabase.push().getKey();
                    UserPost postInfo = new UserPost(user.getUid(), user.getDisplayName(), postID, TitleName, DescriptionName, timeStamp);
                    userForumQuesDatabase.child(postID).setValue(postInfo);
                    Toast.makeText(forum_question.this, "SuccessFully Added", Toast.LENGTH_LONG).show();
                }
                finish();
                startActivity(new Intent(forum_question.this, Forum.class));
                Toast.makeText(forum_question.this, "Successfully Uploaded", Toast.LENGTH_LONG).show();
            }
        });
    }
}
