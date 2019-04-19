package com.sakib23.myapplication;


import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private EditText emailEditText,passwordEditText;
    private Button loginButton,signupButton;
    private ProgressBar progressBarLogin;
    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        emailEditText =(EditText) findViewById(R.id.emailLoginId);
        passwordEditText =(EditText) findViewById(R.id.passwordLoginId);
        loginButton =(Button) findViewById(R.id.loginButtonId);
        signupButton =(Button) findViewById(R.id.signupButtonId);
        progressBarLogin =(ProgressBar) findViewById(R.id.progressBarLoginId);
        mAuth =FirebaseAuth.getInstance();

        signupButton.setOnClickListener(this);
        loginButton.setOnClickListener(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(mAuth.getCurrentUser()!=null)
        {
            finish();
            startActivity(new Intent(MainActivity.this,Homepage.class));
        }
    }

    private void userLogin()
    {
        final String email =emailEditText.getText().toString().trim();//trim removes leaging and trailing zeros
        String password =passwordEditText.getText().toString().trim();
        if(email.isEmpty())
        {
            emailEditText.setError("Email is required");
            emailEditText.requestFocus();
            return;
        }
        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches())
        {
            emailEditText.setError("Please enter a valid email ");
            emailEditText.requestFocus();
            return;
        }
        if(password.isEmpty())
        {
            passwordEditText.setError("password is required");
            passwordEditText.requestFocus();
            return;
        }
        if(password.length()<6)
        {
            passwordEditText.setError("Minimum password length should be 6");
            passwordEditText.requestFocus();
            return;
        }
        progressBarLogin.setVisibility(View.VISIBLE);

        mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                progressBarLogin.setVisibility(View.GONE);
                if(task.isSuccessful()){
                    Toast.makeText(getApplicationContext(),"Login is successful",Toast.LENGTH_LONG).show();
                    finish();
                    startActivity(new Intent(MainActivity.this,Homepage.class));
                }
                else{
                    Toast.makeText(getApplicationContext(),task.getException().getMessage(),Toast.LENGTH_LONG).show();
                }
            }
        });

    }
    @Override
    public void onClick(View v) {
        if(v.getId()==R.id.loginButtonId){
            userLogin();
        }
        if (v.getId()==R.id.signupButtonId){
            finish();
            startActivity(new Intent(MainActivity.this,SignupActivity.class));
        }
    }
}